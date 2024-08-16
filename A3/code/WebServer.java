

/**
 * WebServer Class
 * 
 * Implements a multi-threaded web server
 * supporting non-persistent connections.
 *
 */


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;


public class WebServer extends Thread {
	// global logger object, configures in the driver class
	private static final Logger logger = Logger.getLogger("WebServer");

	private boolean shutdown = false; // shutdown flag

	
	private String root;
	private int timeout;
	private int port;
	private ServerSocket serverSocket;
	private  ExecutorService pool;

	


    /**E
     * Constructor to initialize the web server
     * 
     * @param port 	Server port at which the web server listens > 1024
	 * @param root	Server's root file directory
	 * @param timeout	Idle connection timeout in milli-seconds
     * 
     */
	public WebServer(int port, String root, int timeout){
		this.pool = Executors.newFixedThreadPool(12);

		this.root = root;
		this.timeout = timeout;
		this.port = port;
	};

	
    /**
	 * Main method in the web server thread.
	 * The web server remains in listening mode 
	 * and accepts connection requests from clients 
	 * until it receives the shutdown signal.
	 * 
     */
	public void run(){

		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(100); 


			while(!shutdown){
				try{
					Socket socket = serverSocket.accept();

					System.out.println("Client IP: " +  socket.getRemoteSocketAddress() + ", Client Port: " + socket.getPort() + "\n");
					
					pool.execute(new Worker(root, socket, timeout));

				} catch (SocketTimeoutException e){
					// do nothing, this is OK
					// allows the process to check the shutdown flag
					// if not shutdown, it goes to listening mode again
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//////timeout optionnnn
		shutdownAndAwaitTermination(pool);


		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};
	
	public void shutdownAndAwaitTermination(ExecutorService pool) {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
			pool.shutdownNow(); // Cancel currently executing tasks
			// Wait a while for tasks to respond to being cancelled
			if (!pool.awaitTermination(10, TimeUnit.SECONDS))
				System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

    /**
     * Signals the web server to shutdown.
	 *
     */
	public void shutdown() {
		shutdown = true;
	}
	
}
