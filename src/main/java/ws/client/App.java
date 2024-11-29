package ws.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;

/**
 * Hello world!
 */
public class App {
	public static void main(String[] args) throws URISyntaxException {		
		WebSocketClient client = new EmptyClient(new URI("ws://10.100.70.60:8887"));
		client.connect();
	}
}
