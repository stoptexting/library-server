package library.servers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;

import library.services.Service;

public class Serveur implements Runnable {
	protected final int PORT_NUMBER;
	protected final Class<? extends Service> service;
	
	public Serveur(int PORT_NUMBER, Class<? extends Service> service) {
		this.PORT_NUMBER = PORT_NUMBER;
		this.service = service;
	}

	@SuppressWarnings("resource")
	@Override
	public void run() {
		System.out.println("[" + this.service.getSimpleName() + "] Service server launched on port : " + this.PORT_NUMBER);
		
		try {
			ServerSocket serverSocket = new ServerSocket(this.PORT_NUMBER);
			
			while(true) {
				System.out.println("[" + this.service.getSimpleName() + "] Waiting for a client to connect to port " + this.PORT_NUMBER);
				Socket socket = serverSocket.accept();
				System.out.println("[" + this.service.getSimpleName() + "] Client connected to port : " + this.PORT_NUMBER);
				new Thread(service.getDeclaredConstructor(Socket.class).newInstance(socket)).start();
			}

		}
		
		catch (IOException e) {
			System.err.println("[" + this.service + "] server on port " + this.PORT_NUMBER + " just crashed!");
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
