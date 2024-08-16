import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class HTTP {
    
    /**
     * Fromats the HTTP GET Request then sends it 
     * @param writer Output stream from the socket
     * @param pathname pathname of object to get
     * @param host host where the object is stored
     */
    public String SendRequest(BufferedOutputStream writer, String pathname, String host){

        String requestStr = "GET /" + pathname + " HTTP/1.1\r\n" +
                             "Host: " + host + "\r\n" +
                             "Connection: close\r\n\r\n";      

        
        
        byte[] request;
        try {
            request = requestStr.getBytes("US-ASCII");

            writer.write(request);
            writer.flush();
            return requestStr;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * reads character by cahracter until the entire header has been read
     * @param reader  Input Stream from the socket
     * @return
     */
    public String readHeader(BufferedInputStream reader) {
        String buff = "";
        try{
            while (!buff.contains("\r\n\r\n")){
                int c = reader.read();
                buff += (char) c;
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return buff;
    }

    /**
     * checks the header and returns true is the status code is 200
     * @param Header The header string containing the status code
     * @return
     */
    public boolean checkCode(String Header){
        if (Header.contains("200 OK")){
            return true;
        }

        return false;
    }

}
