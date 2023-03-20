package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

class Timeout extends TimerTask {
	private Socket socket;
	
	public Timeout(Socket socket) {
		this.socket = socket;
	}
	@Override
	public void run() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}


public class ResponseWithTimeOut {
	private static Timer timer;
	
	static {
		ResponseWithTimeOut.timer = new Timer();
	}
	
	private ResponseWithTimeOut() {}
	
	public static String response(BufferedReader socketIn, Socket socket) throws IOException {
		TimerTask timeout = new Timeout(socket);
		timer.schedule(timeout, 60 * 1000); // 1 min before timeout
		String response = socketIn.readLine();
		timeout.cancel();
		return response;
	}
	
	
}
