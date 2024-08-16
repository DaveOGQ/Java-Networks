/**
 * WebClient Class
 * 
 * CPSC 441
 * Assignment 2
 * 
 * @author 	Majid Ghaderi
 * @author David Oti-George
 * UCID:30141134
 * @version	2024
 *
 */

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.*;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class WebClient {

	private static final Logger logger = Logger.getLogger("WebClient"); // global logger

    /**
     * Default no-arg constructor
     */
	public WebClient() {
		// nothing to do!
	}

    private Socket  socket;
    private SSLSocket sslSocket;

    private  BufferedOutputStream writer;
	private  BufferedInputStream reader;
	int buffer_size;
	
	
    /**
     * Downloads the object specified by the parameter url.
	 *
     * @param url	URL of the object to be downloaded. It is a fully qualified URL.
     * @throws IOException 
     */
	public void getObject(String url) {

        UrlParser parser = new UrlParser(url);

        //parse the url
        String protocol = parser.getProtocol();
        String hostname = parser.getHostname();
        String port = parser.getPort();
        String pathname = parser.getPathName();
        String fileName = parser.getFileName(pathname);
        int portNumber;

        // System.out.println("protocol: " + protocol + ", hostname: " + hostname + ", port: " + port + ", pathname: " + pathname + ", fileName: " + fileName);

        if (protocol.equalsIgnoreCase("http")){
            //Tcp connection

            if(port == ""){
                portNumber = 80;
            }else {
                portNumber = Integer.parseInt(port);
            }
            
            createRegularSocket(hostname, portNumber);
            


        } else {//HTTPS
            //secure tcp connection

            

            if(port == ""){
                portNumber = 443;
            }else {
                portNumber = Integer.parseInt(port);
            }
            // System.out.println(portNumber + " " + ((Object)portNumber).getClass().getSimpleName());

            createSecureSocket(hostname, portNumber);
        }


        HTTP http = new HTTP();
        String requestStr = http.SendRequest(writer, pathname, hostname);
        String header = http.readHeader(reader);
        System.out.println(requestStr);
        System.out.println(header);

        if(http.checkCode(header)){
            //read the rest of inputs
            // System.out.println("OK");
            try{

                int bytesRead;
                byte[] buffer = new byte [16000];
                FileOutputStream fileOutputStream = new FileOutputStream(fileName); //stream for outfile

                // Continuous reading and writing in a loop
                while ((bytesRead = reader.read(buffer)) != -1) { // Read the data back from the server using the specified buffer size
                    fileOutputStream.write(buffer, 0, bytesRead); // Write it to the correct outfile
                    fileOutputStream.flush();
                }
                 
                fileOutputStream.close(); //close outfile

                //close the correct socket
                if(protocol.equals("http")){
                    socket.close();
                }else{
                    sslSocket.close();
                }
                


            } catch(IOException e){
                e.printStackTrace();
            }

        }else{
            System.out.println("Wrong status code recieved");
        }
    };



    /**
     * creates a regular socket 
     * @param host name of host 
     * @param port port number 
     */
    public void createRegularSocket(String host, int port) {        
        try {
            socket = new Socket(host, port);
            writer = new BufferedOutputStream(socket.getOutputStream());
			reader = new BufferedInputStream(socket.getInputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }
    

    /**
     * Creates a secure SSLsocket
     * @param host name of host 
     * @param port port number 
     */
    public void createSecureSocket(String host, int port){
        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            sslSocket = (SSLSocket)factory.createSocket(host, port);
            writer = new BufferedOutputStream(sslSocket.getOutputStream());
			reader = new BufferedInputStream(sslSocket.getInputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }

}
