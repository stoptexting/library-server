package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            	continuer = empruntPrompt(socketIn, socketOut); 	
            } while (continuer);
            
            System.out.println("Fermeture de la connexion avec le serveur d'Emprunt...");
            socket.close();
            System.exit(0);
            
            
        } catch (IOException e) {
        	System.err.println("La combinaison " + host + ":" + port + " est fermée ou non atteignable...");
        }
    }
	
	private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public static boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
	
	public static boolean empruntPrompt(BufferedReader socketIn, PrintWriter socketOut) throws IOException {
				// initial handshake avec le serveur pour le catalogue
				String response, line;
		        
		        boolean abonneConfirme = false; boolean documentConfirme = false;
		        
		        // Afficher Ascii
		        response = socketIn.readLine();
		    	System.out.println(decoded(response));
		    	
		        // Afficher le catalogue et le récupérer, initial handshake
		        //response = socketIn.readLine();
		    	//System.out.println(decoded(response));
		    	
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
		        	
		    		System.out.print("Voulez-vous bien emprunter le document nommé (oui/non) : " + document + "? : ");
		    		String docOK = sc.nextLine().toLowerCase();
		    		if (docOK.equals("oui")) {
		    			documentConfirme = true;
		    			socketOut.println("OK");
		    		} else {
		    			documentConfirme = false;
		    			socketOut.println("KO");
		    		}
		    	}
		    	
		    	while (true) {
					socketOut.println("emprunter:" + abonneID + ":" + docID);
					response = socketIn.readLine();
					if (response.equals("OK") || response.equals("KO_BANNI")) {
						response = socketIn.readLine();
						System.out.println("[client] " + response);
						break;
					} else if (response.equals("KO_NOTIFICATION")) {
						response = socketIn.readLine();
						System.out.println("[client] " + response);
						System.out.print("Souhaitez-vous recevoir une notification par mail lors de son retour ? (oui/non) : ");
						
						String choix = "";
						while (!choix.equals("oui") && !choix.equals("non")) {
							choix = sc.nextLine().toLowerCase();
						}
						
						String email = "";
						if (choix.equals("oui")) {
							socketOut.println("notifier");
							
							while (!AppEmprunt.isValidEmail(email)) {
								System.out.print("Sur quel email voulez-vous recevoir la notification ? (ex:bernard@site.com) :");
								email = sc.nextLine();
							}
							socketOut.println(email);
						} else {
							socketOut.println("ne_pas_notifier");
						}
						
						break;
					}
				}
		    	
		    	System.out.print("Faire un nouvel emprunt ? (oui/non) : ");
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
