package library.data;

import java.sql.Date;
import java.util.HashMap;

import library.documents.DVD;
import library.documents.Document;

public class Data {
	private static HashMap<Integer, Document> documents;
	private static HashMap<Integer, Abonne> abonnes;
	
	static {
		Data.documents = new HashMap<>();
		Data.abonnes = new HashMap<>();
		// init de la base de données, chargement des abonnés et documents
		
		// stub artificiel
		Data.documents.put(1, new DVD(1, "Iron Man", false));
		Data.documents.put(2, new DVD(2, "Iron Man 2", false));
		Data.documents.put(3, new DVD(3, "Iron Man 3", true)); // pour adulte
		
		Data.abonnes.put(1, new Abonne(1, "Jean BERNARD", Date.valueOf("1999-01-01"), null));
		Data.abonnes.put(2, new Abonne(2, "Titouan BERNARD", Date.valueOf("2010-01-01"), null));
	}
	
	public static Abonne getAbonne(int aboID) {
		return Data.abonnes.get(aboID);
	}
	
	public static Document getDocument(int docID) {
		return Data.documents.get(docID);
	}
	
	public HashMap<Integer, Abonne> getAbonnes() {
		return abonnes;
	}
	public HashMap<Integer, Document> getDocuments() {
		return documents;
	}
}
