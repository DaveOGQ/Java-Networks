import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.TimerTask;

class TimeoutHandler extends TimerTask {

    private FtpSegment segment;
    private DatagramSocket DSocket;
    private int serverPort;
    

    public TimeoutHandler(FtpSegment segment, DatagramSocket DSocket, int serverPort){
        this.segment = segment;
        this.DSocket = DSocket;
        this.serverPort = serverPort;
    }
    // define the constructor
    // the run method
    public void run() {
        System.out.println("retx <" + segment.getSeqNum() + "> ");
        
        // process re-transmission of the pending segment
        DatagramPacket packet = FtpSegment.makePacket(segment, DSocket.getLocalAddress(), serverPort);

        try {
            DSocket.send(packet);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        //start the timer and resend the segment thats been passed in after the timer runs out
    }
}