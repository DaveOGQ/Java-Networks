

/**
 * WebServer Class
 * 
 * Implements a multi-threaded web server
 * supporting non-persistent connections.
 *
 */


import java.util.*;
import java.util.logging.*;
import java.util.concurrent.*;


public class WebServer extends Thread {
	// global logger object, configures in the driver class
	private static final Logger logger = Logger.getLogger("WebServer");

	private boolean shutdown = false; // shutdown flag

	
    /**
     * Constructor to initialize the web server
     * 
     * @param port 	Server port at which the web server listens > 1024
	 * @param root	Server's root file directory
	 * @param timeout	Idle connection timeout in milli-seconds
     * 
     */
	public WebServer(int port, String root, int timeout);

	
    /**
	 * Main method in the web server thread.
	 * The web server remains in listening mode 
	 * and accepts connection requests from clients 
	 * until it receives the shutdown signal.
	 * 
     */
	public void run();
	

    /**
     * Signals the web server to shutdown.
	 *
     */
	public void shutdown() {
		shutdown = true;
	}
	
}
