package library.services;

import java.net.Socket;

import library.data.Abonne;
import library.data.Data;
import library.documents.Document;
import util.ResponseWithTimeOut;

public class ServiceReservation extends Service {
	
	
	public ServiceReservation(Socket socket) {
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
			//e.printStackTrace();
			System.out.println("["+ this + "] Fin de connexion avec le client " + + this.socket.getPort() + "...");
		}
		
	}
	
	public boolean reserverDocument() throws Exception {
		int aboID = 999; int docID = 999;
		boolean abonneConfirme = false; boolean documentConfirme = false;
		
		// handshake initial
		System.out.println("["+ this + "] Envoi du catalogue au client " + this.socket.getPort());
		this.socketOut.println(borneEmpruntAscii()); // Borne Reservation
		this.socketOut.println(Data.getCatalogueEncoded()); // catalogue formaté + encoded en base 64
				
		String line;
		
		Abonne abo = null;
		Document doc = null;
		
		while (!abonneConfirme) {
			line = ResponseWithTimeOut.response(socketIn, socket);
			String[] parts = line.split(":");
	        String command = parts[0];
	        int id = Integer.parseInt(parts[1]);
	        
	        
	        if (command.equals("abo")) {
	        	abo = Data.getAbonne(id);
	        	if (abo == null) {
	        		socketOut.println("not found");
	        		continue;
	        	} else {
	        		socketOut.println(abo);
	        	}
	        
	        	if ((line = ResponseWithTimeOut.response(socketIn, socket)).equals("OK"))
	        		abonneConfirme = true;
	        		aboID = abo.getId();
	        }
		}
		
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
		
		
		if ((documentConfirme && abonneConfirme) && (aboID != 999 && docID != 999)) {
			line = ResponseWithTimeOut.response(socketIn, socket);
			String[] parts = line.split(":");
	        String command = parts[0];
	        int aboIDConfirm = Integer.parseInt(parts[1]);
	        int docIDConfirm = Integer.parseInt(parts[2]);
	        
	        if (command.equals("reserver")) {
	        	if (aboIDConfirm == aboID && docIDConfirm == docID) {
	        		if (doc.reserveur() == abo) {
	        			socketOut.println("Echec lors de la réservation : Vous avez déjà reservé le document !");
	        		} else if (doc.reserveur() != null || doc.emprunteur() != null) {
	        			socketOut.println("Echec lors de la réservation : Le document est déjà réservé par un autre abonné jusqu'à " + getLimitReservation(doc)); // timertask
	        		} else {
	        			doc.reservationPour(abo);
	        			
	        			if (doc.reserveur() == abo) {
	        	        	socketOut.println("Réservation effectuée avec succès : " + doc + " réservé pour l'abonné " + abo);
	        	        	System.out.println("Réservation effectuée avec succès");
	        	        } else {
	        	        	if (doc.getClass().getSimpleName().equals("DVD") && !abo.estAdulte()) {
	        	        		socketOut.println("Vous n'avez pas l'âge necessaire pour réserver ce type de document réservé aux adultes (-16)! ");
	        	        		System.err.println("[AGE NON REQUIS] Echec lors de la réservation du document : " + doc + " pour " + abo);
	        	        	} else {
	        	        		socketOut.println("Echec lors de la réservation effectuée: " + doc + " non réservé pour l'abonné " + abo);
	        	        		System.err.println("[RAISON INCONNUE] Echec lors de la réservation du document : " + doc + " pour " + abo);
	        	        	}
	        	        }
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
	
	public static String borneEmpruntAscii() {
		String ascii = "ICBfX19fICAgICAgICAgICAgICAgICAgICAgICAgICBfX19fICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBfICAgXyAgICAgICAgICAgICAKIHwgX18gKSAgX19fICBfIF9fIF8gX18gICBfX18gIHwgIF8gXCBfX18gIF9fXyAgX19fIF8gX19fXyAgIF9fX18gX3wgfF8oXykgX19fICBfIF9fICAKIHwgIF8gXCAvIF8gXHwgJ19ffCAnXyBcIC8gXyBcIHwgfF8pIC8gXyBcLyBfX3wvIF8gXCAnX19cIFwgLyAvIF9gIHwgX198IHwvIF8gXHwgJ18gXCAKIHwgfF8pIHwgKF8pIHwgfCAgfCB8IHwgfCAgX18vIHwgIF8gPCAgX18vXF9fIFwgIF9fLyB8ICAgXCBWIC8gKF98IHwgfF98IHwgKF8pIHwgfCB8IHwKIHxfX19fLyBcX19fL3xffCAgfF98IHxffFxfX198IHxffCBcX1xfX198fF9fXy9cX19ffF98ICAgIFxfLyBcX18sX3xcX198X3xcX19fL3xffCB8X3wK";
		return ascii;
	}
	
}
