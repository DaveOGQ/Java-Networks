import java.io.*;
import java.net.*;
import java.util.*;

/**
 * A Simple Echo Server
 * CPSC 441
 */

public class TCPServer {

	public static void main(String[] args) {

		String s;
		Scanner inputStream;
		PrintWriter outputStream;
		ServerSocket serverSocket;

		try {
			// Listen on port 8888
			serverSocket = new ServerSocket(8888);

			Socket socket = serverSocket.accept();
			// Connected to client
			outputStream = new PrintWriter(new DataOutputStream(
					socket.getOutputStream()));
			inputStream = new Scanner(new InputStreamReader(
					socket.getInputStream()));

			// Respond to messages from the client
			while (true) {
				s = inputStream.nextLine();
				System.out.println(s);

				// exit if message from client is "bye"
				if (s.equalsIgnoreCase("bye")) {
					outputStream.println("bye");
					outputStream.flush();
					break;
				}

				outputStream.println(s);
				outputStream.flush();
			}

			inputStream.close();
			outputStream.close();
		}
		catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
}
