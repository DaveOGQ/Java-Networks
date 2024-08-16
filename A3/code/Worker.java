import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;

public class Worker implements Runnable {

    private BufferedInputStream reader;
    private BufferedOutputStream writer;
    private byte[] buffer = new byte [32000];
    private Socket socket;
    private int timeout;
    private String root;
    

    Worker(String root, Socket socket, int timeout){
        this.socket = socket;

        this.timeout = timeout;
        this.root = root;
        try {
            writer = new BufferedOutputStream(socket.getOutputStream());
            reader = new BufferedInputStream(socket.getInputStream());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //start the timeout the request doesnt come in time
        try {

            socket.setSoTimeout(timeout);

            String getRequest = readRequest();   //String of the get request

            if(getRequest.equals("return")){ //incase timeout occured just end the thread
                socket.close();
                return;
            }


            //check format 
            if(checkFormat(getRequest)){
                //get the pathname
                String objectPathname = getPathName(getRequest);

                
                //see if this file exists in the root directory
                File file = new File(root + objectPathname);

                if(file.exists()){
                    // 200 OK
                    SendHttpResponse(file, 200, "OK");
                    SendFileContentsToClient(root + objectPathname);
                }
                else {
                    //404 not found
                    SendHttpResponse(null, 404, "Not Found");
                }
		
            }
            else{
                // 400 Bad Request
                SendHttpResponse(null, 400, "Bad Request");
            }

            socket.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    // GET /TCPServer.java HTTP/1.1
    /**
     * 
     * @return
     */
    public String readRequest() {
        String buff = "";
        String responseLine = "";
        try{
            while (!responseLine.contains("\r\n")){//get the top request line
                int c = reader.read();
                responseLine += (char) c;
            }

            while (!buff.contains("\r\n\r\n") && reader.available() > 0 ){//get the trest of the header lines
                int c = reader.read();         
                buff += (char) c;
            }

            

        } catch (SocketTimeoutException e){
            SendHttpResponse(null, 408, "Request Timeout");
            return "return";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.print(responseLine);
        System.out.println(buff);

        return responseLine;
    }


    /**
     * parses the url for the pathname of the object
     * @return
     */
    public String getPathName(String str){

        String[] arr = str.split(" ");

        String pathname = arr[1];

        if(pathname.equals("/")){
            pathname = "/index.html";
        }

        return pathname;
    }

    public Boolean checkFormat(String request){

        if(!request.contains("GET") || !request.contains("HTTP/1.1") || !request.contains("\r\n")){
            return false;
        }

        return true;
    }

    void SendHttpResponse(File file, int statuscode, String statusphrase){
        String responseStr;
        try {
            if(statuscode == 200){
                responseStr = "HTTP/1.1 " + statuscode + " " + statusphrase + "\r\n" + 
                                    "Date: " + ServerUtils.getCurrentDate() + "\r\n" + 
                                    "Last-Modified: " + ServerUtils.getLastModified(file) + "\r\n" +
                                    "Content-Length: " + ServerUtils.getContentLength(file) + "\r\n" +
                                    "Content-Type: " + ServerUtils.getContentType(file) + "\r\n" +
                                    "Server: DavidCloud" + "\r\n" + 
                                    "Connection: close" + "\r\n\r\n";
            }else{
                responseStr = "HTTP/1.1 " + statuscode + " " + statusphrase + "\r\n" + 
                              "Date: " + ServerUtils.getCurrentDate() + "\r\n" + 
                              "Server: DavidCloud" + "\r\n" + 
                              "Connection: close" + "\r\n\r\n";
            }

            System.out.println(responseStr);

            byte[] response;        
            response = responseStr.getBytes("US-ASCII");

            writer.write(response);
            writer.flush();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public void SendFileContentsToClient(String fileName){
        try{
            int bytesRead;
			FileInputStream fileInputStream = new FileInputStream(fileName); //stream for outfile

			//write the contents to the server
			while ((bytesRead = fileInputStream.read(buffer)) != -1 ){ //read from file
				writer.write(buffer, 0, bytesRead); //write to client
				writer.flush();
			}

			fileInputStream.close(); //close file
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    }

