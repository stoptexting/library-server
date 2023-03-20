package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;

public class AppRetour {
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
            	continuer = retourPrompt(socketIn, socketOut); 	
            } while (continuer);
            
            System.out.println("Fermeture de la connexion avec le serveur d'Emprunt...");
            socket.close();
            System.exit(0);
            
            
        } catch (IOException e) {
            System.err.println("La combinaison " + host + ":" + port + " est fermée ou non atteignable...");
        }
    }
	
	public static boolean retourPrompt(BufferedReader socketIn, PrintWriter socketOut) throws IOException {
				// initial handshake avec le serveur pour le catalogue
				String response, line;
		        
		        boolean documentConfirme = false;
		        
		        // Afficher Ascii
		        response = socketIn.readLine();
		    	System.out.println(decoded(response));
		    	
		        // Afficher le catalogue et le récupérer, initial handshake
		        //response = socketIn.readLine();
		    	//System.out.println(decoded(response));
		    	
		    	int docID = 999;
		    	
		    	while (!documentConfirme) {
		    		System.out.print("Entrez le n° du document à retourner : ");
		        	docID = Integer.parseInt(sc.nextLine());
		        	
		        	// on demande de récup l'id du document avec docID
		        	socketOut.println("doc:" + docID);
		        	// on récupère la réponse du serveur
		        	String document = socketIn.readLine();
		        	
		        	if (document.equals("not found")) {
		        		System.out.println("Il n'existe aucun document à ce numéro! Réessayez...");
		        		continue;
		        	}
		        	
		    		System.out.print("Voulez-vous bien retourner le document nommé (oui/non) : " + document + "? : ");
		    		String docOK = sc.nextLine().toLowerCase();
		    		if (docOK.equals("oui")) {
		    			documentConfirme = true;
		    			socketOut.println("OK");
		    		} else {
		    			documentConfirme = false;
		    			socketOut.println("KO");
		    		}
		    	}
		    	
		    	// debut retour
		    	if ((documentConfirme) && (docID != 999)) {
		    		// on effectue le retour
					socketOut.println("retourner:" + docID);
		        	response = socketIn.readLine();
		        	System.out.println("[client] " + response);
		    	}
		    	// fin retour
		    	
		    	System.out.print("Faire un nouveau retour ? (oui/non) : ");
				line = sc.nextLine().toLowerCase();

		    	if (line.equals("oui")) {
		    		socketOut.println("continue");
					return true;
		    	} else {
		    		socketOut.println("stop");
		        	return false;
		    	}
	}
	
	private static String decoded(String fromServer) {
	      byte[] decodedBytes = Base64.getDecoder().decode(fromServer);
	      return new String(decodedBytes);
	}
}
