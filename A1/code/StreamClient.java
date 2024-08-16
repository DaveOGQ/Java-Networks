
/**
 * StreamClient Class
 * 
 * CPSC 441
 * Assignment 1
 *
 */


import java.util.logging.*;


public class StreamClient {

	private static final Logger logger = Logger.getLogger("StreamClient"); // global logger


	/**
	 * Constructor to initialize the class.
	 * 
	 * @param serverName	remote server name
	 * @param serverPort	remote server port number
	 * @param bufferSize	buffer size used for read/write
	 */
	public StreamClient(String serverName, int serverPort, int bufferSize);

	
	/**
	 * Compress the specified file via the remote server.
	 * 
	 * @param inName		name of the input file to be processed
	 * @param outName		name of the output file
	 */
	public void getService(int serviceCode, String inName, String outName);

}
