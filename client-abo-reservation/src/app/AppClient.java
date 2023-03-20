package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class AppClient {
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
		// initial handshake avec le serveur pour le catalogue
		String response, line;
        
        boolean abonneConfirme = false; boolean documentConfirme = false;
        
        // Afficher le catalogue et le récupérer, initial handshake
        response = socketIn.readLine();
    	System.out.println(response);
    	
    	int abonneID = 999; int docID = 999;
    	
    	
    	while (!abonneConfirme) {
    		System.out.print("Entrez votre n° abonné : ");
        	abonneID = Integer.parseInt(sc.nextLine()); // bug si on met des lettres
        	//sc.nextLine();
        	
        	// on demande de récup le nom de l'abo avec l'id abonneID
        	socketOut.println("abo:" + abonneID);
        	// on récupère la réponse du serveur
        	String abonne = socketIn.readLine();

        	if (abonne.equals("not found")) {
        		System.out.println("Il n'existe aucun abonné à ce numéro! Réessayez...");
        		continue;
        	}
        	
    		System.out.print("Etes-vous bien (oui/non) " + abonne + "? : ");
    		String aboOK = sc.nextLine().toLowerCase();
    		if (aboOK.equals("oui")) {
    			abonneConfirme = true;
    			socketOut.println("OK");
    		} else {
    			abonneConfirme = false;
    			socketOut.println("KO");
    		}
    	}
    	
    	while (!documentConfirme) {
    		System.out.print("Entrez le n° du document : ");
        	docID = Integer.parseInt(sc.nextLine());
        	//sc.nextLine();
        	
        	// on demande de récup le nom de l'abo avec l'id abonneID
        	socketOut.println("doc:" + docID);
        	// on récupère la réponse du serveur
        	String document = socketIn.readLine();
        	
        	if (document.equals("not found")) {
        		System.out.println("Il n'existe aucun document à ce numéro! Réessayez...");
        		continue;
        	}
        	
    		System.out.print("Voulez-vous bien réserver le document nommé (oui/non) : " + document + "? : ");
    		String docOK = sc.nextLine().toLowerCase();
    		if (docOK.equals("oui")) {
    			documentConfirme = true;
    			socketOut.println("OK");
    		} else {
    			documentConfirme = false;
    			socketOut.println("KO");
    		}
    	}
    	
    	// debut reservation
    	if ((documentConfirme && abonneConfirme) && (abonneID != 999 && docID != 999)) {
    		// on effectue la réservation
			socketOut.println("reserver:" + abonneID + ":" + docID);
        	response = socketIn.readLine();
        	System.out.println("[client] " + response);
    	}
    	// fin finie
    	
    	System.out.print("Faire une nouvelle réservation ? (oui/non) : ");
		line = sc.nextLine().toLowerCase();

    	if (line.equals("oui")) {
    		socketOut.println("continue");
			return true;
    	} else {
    		socketOut.println("stop");
        	return false;
    	}
	}
}
