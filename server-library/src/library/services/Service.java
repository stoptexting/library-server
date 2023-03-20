package library.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import library.documents.ADocument;
import library.documents.Document;

public class Service implements Runnable {
	protected final Socket socket;
	protected BufferedReader socketIn;
	protected PrintWriter socketOut;
	
	public Service(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
        try {
			this.socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.socketOut = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        

	}
	
	@Override
	public String toString() {
		return "service";
	}
	
	public static String getLimitReservation(Document doc) {
		ADocument Adoc = (ADocument) doc; // 99.999% castable parce que chaque doc hérite de ADocument hors erreur
		return Adoc.getLimit();
	}

}
