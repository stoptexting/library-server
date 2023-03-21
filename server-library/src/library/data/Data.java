package library.data;

import java.sql.Statement;
import java.time.LocalDate;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Random;

import library.documents.ADocument;
import library.documents.DVD;
import library.documents.Document;
import util.GestionnaireSignauxFumee;

public class Data {
	private static HashMap<Integer, Document> documents;
	private static HashMap<Integer, Abonne> abonnes;
	
	
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
	
	public static String getCatalogueEncoded() {
        Formatter formatter = new Formatter();

        // Define column headers
        formatter.format("%-6s %-50s %-10s%n", "Numéro", "Titre", "Reservable");
        
        for (Document doc: documents.values()) {
            formatter.format("%-6d %-50s %-10s%n", doc.numero(), doc, (doc.reserveur() == null && doc.emprunteur() == null ? "Oui":"Non"));
        }
        
        
        String docs = formatter.toString();
        formatter.close();
        
        return Base64.getEncoder().encodeToString(docs.getBytes());
    }
	
	
	private static final String url = "jdbc:mysql://localhost:3306/library";
	private static final String user = "root";
	private static final String password = "";
	public static void init() {
		Data.documents = new HashMap<>();
		Data.abonnes = new HashMap<>();
		// init de la base de données, chargement des abonnés et documents
		
		// stub artificiel
		//Data.documents.put(1, new DVD(1, "Iron Man", false));
		//Data.documents.put(2, new DVD(2, "Iron Man 2", false));
		//Data.documents.put(3, new DVD(3, "Iron Man 3", true)); // pour adulte

		//Data.abonnes.put(1, new Abonne(1, "Jean BERNARD", Date.valueOf("1999-01-01"), null));
		//Data.abonnes.put(2, new Abonne(2, "Titouan BERNARD", Date.valueOf("2010-01-01"), null));
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			//Data.createDatabase();
			//Data.loadDatabase();
			Data.createAbonnes();
			Data.createDocuments();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	
	}
	
	private final static String DB_FILENAME = "database_creation.sql";
	
//	private static void createDatabase() {
//
//        try (Connection connection = DriverManager.getConnection(url, user, password)) {
//            String databaseName = "library";
//            String createDatabaseQuery = "CREATE DATABASE " + databaseName;
//
//            Statement statement = connection.createStatement();
//            statement.executeUpdate(createDatabaseQuery);
//
//            System.out.println("Database " + databaseName + " created successfully.");
//        } catch (SQLException e) {
//            System.err.println("Error creating database: " + e.getMessage());
//        }
//    }
//	
//	private static void loadDatabase() {
//		try {
//			 // Load the SQL file into a string
//	        BufferedReader br = new BufferedReader(new FileReader(DB_FILENAME));
//	        StringBuilder sb = new StringBuilder();
//	        String line;
//	        while ((line = br.readLine()) != null) {
//	            sb.append(line);
//	            sb.append("\n");
//	        }
//	        String sql = sb.toString();
//	        
//	        // Connect to the database and execute the SQL script
//	        Connection conn = DriverManager.getConnection(url, user, password);
//	        Statement stmt = conn.createStatement();
//	        stmt.execute(sql);
//	        
//	        // Clean up resources
//	        stmt.close();
//	        conn.close();
//	        br.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	public static void createDocuments() {
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, nom, type, adulte FROM DOCUMENT");

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String type = rs.getString("type");
                boolean adulte = rs.getBoolean("adulte");
                
                if ("DVD".equals(type)) {
                    DVD dvd = new DVD(id, nom, adulte);
                    Data.documents.put(id, dvd);
                } else {
                    //ADocument doc = new ADocument(id, nom);
                    //documents.put(id, doc);
                }
            }
            
            rs.close();
            conn.close();
            stmt.close();
            System.out.println("[mysql] Création des documents effectuée avec succès!");
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public static void createAbonnes() {
		try {
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ABONNE");

            while (rs.next()) {
            	int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String dateNaissance = rs.getString("dateNaissance");
                String dateBannissement = rs.getString("dateBannissement");
                
                Abonne abonne = new Abonne(id, nom, Date.valueOf(dateNaissance), (dateBannissement == null ? null : Date.valueOf(dateBannissement)));
                Data.abonnes.put(id, abonne);
            }
            
            rs.close();
            conn.close();
            stmt.close();
            System.out.println("[mysql] Création des abonnés effectuée avec succès!");
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
}
