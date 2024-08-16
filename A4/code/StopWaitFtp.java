

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.*;

import javax.print.attribute.standard.Severity;

public class StopWaitFtp {
	
	private static final Logger logger = Logger.getLogger("StopWaitFtp"); // global logger	

	private int timeout;
	private Socket Socket;
	private DatagramSocket DSocket;
	private  BufferedOutputStream writer;
	private  BufferedInputStream reader;
	private DataOutputStream DWriter;
	private DataInputStream DReader;

	/**
	 * Constructor to initialize the program 
	 * 
	 * @param timeout		The time-out interval for the retransmission timer, in milli-seconds
	 */
	public StopWaitFtp(int timeout){
		this.timeout = timeout;
	};


	/**
	 * Send the specified file to the specified remote server.
	 * 
	 * @param serverName	Name of the remote server
	 * @param serverPort	Port number of the remote server
	 * @param fileName		Name of the file to be trasferred to the rmeote server
	 * @return 				true if the file transfer completed successfully, false otherwise
	 */
	public boolean send(String serverName, int serverPort, String fileName){
		try {
			Socket = new Socket(serverName, serverPort);
		    // reader =  new BufferedInputStream(Socket.getInputStream());
			// writer = new BufferedOutputStream(Socket.getOutputStream());

			DWriter = new DataOutputStream(Socket.getOutputStream());
			DReader = new DataInputStream(Socket.getInputStream());


			DSocket = new DatagramSocket();
			// SocketAddress SocketIp  = DSocket.getRemoteSocketAddress();

			Handshake handshake = initiateHandshake(fileName, serverPort);

			int Seqnum = handshake.getSeq();
			int port = handshake.getPort();

			

			System.out.println("Handshake Complete, ServerPort: " + port + ", Initial Sequence Number: " + Seqnum);

			sendFileContents(fileName, DSocket, timeout, Seqnum, port);

			cleanUp();


		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return false;
	};


	public Handshake initiateHandshake(String fileName, int LocalPort){
		Handshake handshake = null;
		try {
			File file = new File("./" + fileName);

			if(file.exists()){
				System.out.println("file exists");
			}

			long fileLen = Long.parseLong(ServerUtils.getContentLength(file));
			
			System.out.println(fileLen);

			DWriter.writeUTF(fileName);
			DWriter.flush();

			DWriter.writeLong(fileLen);
			DWriter.flush();

			DWriter.writeInt(LocalPort);
			DWriter.flush();

			int serverPort = DReader.readInt();

			int Seqnum = DReader.readInt();

			handshake = new Handshake(Seqnum, serverPort);


		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return handshake;
	}

	public void sendFileContents(String fileName, DatagramSocket DSocket, int timeout, int seqNum, int serverPort){
		System.out.println("Handshake Complete, ServerPort: " + serverPort + ", Initial Sequence Number: " + seqNum);


		byte[] buffer = new byte [FtpSegment.MAX_PAYLOAD_SIZE];
		byte[] rcvBuffer = new byte [FtpSegment.MAX_SEGMENT_SIZE];
		//create timer
		Timer timer = new Timer();

		//REMEMBER TO DO SETSOCKETTIMEOUT
		
		try{
			DSocket.setSoTimeout(timeout * 10);

            int bytesRead;
			FileInputStream fileInputStream = new FileInputStream(fileName); //stream for outfile

			//write the contents to the server
			while ((bytesRead = fileInputStream.read(buffer)) != -1 ){ //read from file
				System.out.println("read " + bytesRead + " bytes");
				//Encapsulate and Send Packet
				

				FtpSegment segment = new FtpSegment(seqNum, buffer, buffer.length);
				DatagramPacket packet = FtpSegment.makePacket(segment, DSocket.getInetAddress(), serverPort);
				System.out.println("send <" + seqNum + ">");
				DSocket.send(packet);

				System.out.println("packetsent");

				//create timer task
				TimerTask timeoutHandler = new TimeoutHandler(segment, DSocket, serverPort);

				//schedule new timer task
				timer.scheduleAtFixedRate(timeoutHandler, timeout, 0);


				//Recieve and decapsulate Packet
				DatagramPacket packetReceive = new DatagramPacket(rcvBuffer, rcvBuffer.length);
				DSocket.receive(packetReceive);

				System.out.println("packet recieved");

				FtpSegment ack = new FtpSegment(packetReceive);
				int ackNum = ack.getSeqNum();
				System.out.println("ack <" + seqNum + ">");


				//if the correct ack has not been recieved, continue to receive the acks for the packets the timer is resending
				while(ackNum != seqNum + 1){
					DSocket.receive(packetReceive);
					ack = new FtpSegment(packetReceive);
					ackNum = ack.getSeqNum();
					System.out.println(ackNum);

					
					// if the correct ask is recieved, cancel the task and increment the ack by 1
					if (ackNum == seqNum + 1){
						//correct ack 
						seqNum = ackNum;
						//cancel the timer task, ack has been recieved successfully
						timer.cancel();
					}
				}
			}
			timer.purge();
			fileInputStream.close(); //close file
        }
		catch(SocketTimeoutException e){
			timer.cancel();
			timer.purge();
			System.out.println("timeout error?");
		}
        catch (Exception e){
            System.out.println(e.getMessage());
        }

	}

	public void cleanUp(){
		try {
			Socket.close();
			DSocket.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

} // end of class