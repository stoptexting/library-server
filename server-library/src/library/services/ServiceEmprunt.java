package library.services;

import java.net.Socket;

import library.data.Abonne;
import library.data.Data;
import library.documents.ADocument;
import library.documents.Document;
import util.ResponseWithTimeOut;

public class ServiceEmprunt extends Service {

	public ServiceEmprunt(Socket socket) {
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
			System.out.println("["+ this + "] Fin de connexion avec le client " + + this.socket.getPort() + "... (timeout)");
		}
		
	}
	
	public boolean reserverDocument() throws Exception {
		int aboID = 999; int docID = 999;
		boolean abonneConfirme = false; boolean documentConfirme = false;
		
		// handshake initial
		System.out.println("["+ this + "] Envoi du catalogue au client " + this.socket.getPort());
		this.socketOut.println(borneEmpruntAscii());
		//this.socketOut.println(Data.getCatalogueEncoded()); // catalogue formaté + encoded en base 64
				
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
	        	
	        	if ((line = ResponseWithTimeOut.response(socketIn, socket)).equals("OK")) {
	        		documentConfirme = true;
	        		docID = doc.numero();
	        	}
	        }
		}
		
		if ((documentConfirme && abonneConfirme) && (aboID != 999 && docID != 999)) {
			line = ResponseWithTimeOut.response(socketIn, socket);
			String[] parts = line.split(":");
	        String command = parts[0];
	        int aboIDConfirm = Integer.parseInt(parts[1]);
	        int docIDConfirm = Integer.parseInt(parts[2]);
	        
	        if (command.equals("emprunter")) {
	        	if (aboIDConfirm == aboID && docIDConfirm == docID) {
	        		if (doc.reserveur() == abo || (doc.reserveur() == null && doc.emprunteur() == null)) {
	        			doc.empruntPar(abo);
	        			socketOut.println("Le document " + doc + " a bien été emprunté avec succès pour l'abonné " + abo);
	        		} else {
	        			socketOut.println("Echec lors de l'emprunt : le document demandé est déjà " + (doc.emprunteur() != null ? "emprunté" : (doc.reserveur() != null ? "réservé jusqu'au " + ((ADocument) doc).getLimit() : "lebron james") + " par un autre abonné.")); // timertask
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
	
	public static boolean empruntable(Document doc) {
		return doc.emprunteur() == null;
	}
	
	public static String borneEmpruntAscii() {
		String ascii = "ICBfX19fICAgICAgICAgICAgICAgICAgICAgICAgICBfX19fXyAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBfICAgCiB8IF9fICkgIF9fXyAgXyBfXyBfIF9fICAgX19fICB8IF9fX198XyBfXyBfX18gIF8gX18gIF8gX18gXyAgIF8gXyBfXyB8IHxfIAogfCAgXyBcIC8gXyBcfCAnX198ICdfIFwgLyBfIFwgfCAgX3wgfCAnXyBgIF8gXHwgJ18gXHwgJ19ffCB8IHwgfCAnXyBcfCBfX3wKIHwgfF8pIHwgKF8pIHwgfCAgfCB8IHwgfCAgX18vIHwgfF9fX3wgfCB8IHwgfCB8IHxfKSB8IHwgIHwgfF98IHwgfCB8IHwgfF8gCiB8X19fXy8gXF9fXy98X3wgIHxffCB8X3xcX19ffCB8X19fX198X3wgfF98IHxffCAuX18vfF98ICAgXF9fLF98X3wgfF98XF9ffAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHxffCAgICAgICAgICAgICAgICAgICAgICA=";
		return ascii;
	}
	
	@Override
	public String toString() {
		return "service-emprunt";
	}

}
