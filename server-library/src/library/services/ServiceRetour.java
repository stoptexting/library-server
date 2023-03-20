package library.services;

import java.net.Socket;

import library.data.Abonne;
import library.data.Data;
import library.documents.Document;
import util.GestionnaireBannissement;
import util.GestionnaireRetard;
import util.ResponseWithTimeOut;

public class ServiceRetour extends Service {
	
	public ServiceRetour(Socket socket) {
		super(socket);
	}
	
	@Override
	public void run() {
		super.run();
		
		try {
			boolean continuer = true;
			while (continuer) {
				continuer = reserverDocument();
			}
		}
		
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("["+ this + "] Fin de connexion avec le client " + + this.socket.getPort() + "...");
		}
		
	}
	
	public boolean reserverDocument() throws Exception {
		int docID = 999;
		boolean documentConfirme = false;
		
		// handshake initial
		System.out.println("["+ this + "] Initialisation d'une borne de retour avec le client " + this.socket.getPort());
		this.socketOut.println(borneRetourAscii()); // Borne Retour Ascii Art
				
		String line;
		
		Document doc = null;

		while (!documentConfirme) {
			line = ResponseWithTimeOut.response(socketIn, socket);
			String[] parts = line.split(":");
	        String command = parts[0];
	        int id = Integer.parseInt(parts[1]);
	        
	        
	        if (command.equals("doc")) {
	        	doc = Data.getDocument(id);
	        	if (doc == null) {
	        		socketOut.println("not found");
	        		continue;
	        	} else {
	        		socketOut.println(doc);
	        	}
	        	
	        	if ((line = ResponseWithTimeOut.response(socketIn, socket)).equals("OK"))
	        		documentConfirme = true;
	        		docID = doc.numero();
	        	
	        }
		}
		
		
		if ((documentConfirme) && (docID != 999)) {
			line = ResponseWithTimeOut.response(socketIn, socket);
			String[] parts = line.split(":");
	        String command = parts[0];
	        int docIDConfirm = Integer.parseInt(parts[1]);
	        String etat = parts[2];
	        
	        if (command.equals("retourner")) {
	        	if (docIDConfirm == docID) {
	        		
	        		if (doc.emprunteur() != null && doc.reserveur() == null) {
	        			if (etat.equals("dégradé")) {
	        				GestionnaireBannissement.bannir(doc.emprunteur());
	        				socketOut.println("Le document a été dégradé : l'emprunteur a été banni de la tribu jusqu'au : " + (doc.emprunteur().getDateFinBannissement() != null ? doc.emprunteur().getDateFinBannissement() : "{non banni}"));
	        				GestionnaireRetard.retour(doc);
	        			} else {
	        				doc.retour();
	        				socketOut.println("Le document " + doc + " a été retourné avec succès!");
	        			}
	        			
	        		} else if (doc.emprunteur() == null && doc.reserveur() != null) {
	        			socketOut.println("Impossible de retourner le document, il n'est pas encore emprunté l'abonné qui l'a réservé!");
	        		} else {
	        			socketOut.println("Impossible de retourner le document, il n'est emprunté par aucun abonné !");
	        		}
	        	}
	        }
	        
	        line = socketIn.readLine();
    		if(line.equals("continue")) {
    			return true;
    		} else {
    			return false;
    		}
    	}
		
		if (this.socketIn.readLine().equals("continue"))
			return true; // continuer à réserver d'autres documents
		return false;
	}
	
	@Override
	public String toString() {
		return "service-reservation";
	}
	
	public static boolean empruntable(Document doc) {
		return doc.emprunteur() == null;
	}
	
	public static boolean reservable(Document doc, Abonne abo) {
		return doc.reserveur() == null && doc.emprunteur() == abo;
	}
	
	public static String borneRetourAscii() {
		String ascii = "ICBfX19fICAgICAgICAgICAgICAgICAgICAgICAgICBfX19fICAgICAgXyAgICAgICAgICAgICAgICAgICAKIHwgX18gKSAgX19fICBfIF9fIF8gX18gICBfX18gIHwgIF8gXCBfX198IHxfIF9fXyAgXyAgIF8gXyBfXyAKIHwgIF8gXCAvIF8gXHwgJ19ffCAnXyBcIC8gXyBcIHwgfF8pIC8gXyBcIF9fLyBfIFx8IHwgfCB8ICdfX3wKIHwgfF8pIHwgKF8pIHwgfCAgfCB8IHwgfCAgX18vIHwgIF8gPCAgX18vIHx8IChfKSB8IHxffCB8IHwgICAKIHxfX19fLyBcX19fL3xffCAgfF98IHxffFxfX198IHxffCBcX1xfX198XF9fXF9fXy8gXF9fLF98X3wgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICA=";
		return ascii;
	}
}
