package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class AppEmprunt {
private static final Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) throws InterruptedException {
        int port = 0;
        boolean flag = false;

        // Parse command line arguments
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-p")) {
                flag = true;
                try {
                    port = Integer.parseInt(args[++i]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid port number: " + args[i]);
                    System.exit(1);
                }
            }
        }

        if (!flag) {
            System.err.println("Usage: java Main -p <port_number>");
            System.exit(1);
        }

        String host = "127.0.0.1";
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 1000);
            System.out.println("Port " + port + " on host " + host + " is open");
            
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), true);
			
            boolean continuer = true;
            do {
            	continuer = reservationPrompt(socketIn, socketOut); 	
            } while (continuer);
            
            System.out.println("Fermeture de la connexion avec le serveur de Réservation...");
            socket.close();
            System.exit(0);
            
            
        } catch (IOException e) {
            System.err.println("Le port " + port + " avec le host " + host + " sont fermés ou non atteignables...");
        }
    }
	
	public static boolean reservationPrompt(BufferedReader socketIn, PrintWriter socketOut) throws IOException {
		
		
		return false;
	}
}
