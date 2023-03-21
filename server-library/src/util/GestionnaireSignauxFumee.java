package util;

import java.util.Properties;
import java.util.Timer;

import javax.mail.Session;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import library.data.Abonne;
import library.data.Data;
import library.documents.Document;

class NotificationRetour implements Runnable {
	private String email;
	private Document doc;
	
	public NotificationRetour(String email, Document doc) {
		this.email = email;
		this.doc = doc;
	}
	
	@Override
	public void run() {
		synchronized(doc) {	
			try {
				System.err.println("En attente du retour : la notification par mail pour l'email " + this.email + " et le document " + doc);
				doc.wait();
				boolean notifSuccess = GestionnaireSignauxFumee.sendNotification(email, doc);
				if (notifSuccess)
					System.err.println("Notification envoyée avec succès");
				else
					System.err.println("Erreur lors de l'envoi de la notification");
			} catch (InterruptedException e) {
				System.err.println("Problème avec la notification par mail pour l'email " + this.email + " et le document " + doc);
				e.printStackTrace();
			}
		}
		
		
	}
	
}

public class GestionnaireSignauxFumee {
	private static Timer timer;
	
	static {
		GestionnaireSignauxFumee.timer = new Timer();
	}
	
	private GestionnaireSignauxFumee() {}
	
	public static void notifierRetour(String email, Document doc) {
		new Thread(new NotificationRetour(email, doc)).start();
	}
	
	public static void debannir(Abonne abo) {
		System.err.println(abo + " a été debanni!");
		abo.setDateFinBannissement(null);
	}
	
	public static boolean sendNotification(String email, Document doc) {
		System.err.println("[Notification] Envoi de la notification pour le document " + doc + " à l'abonné " + email);
		// Recipient's email ID needs to be mentioned.
		String to = email;

	      // Sender's email ID needs to be mentioned
		String from = "notification@library.fr";

	      // Assuming you are sending email from localhost
		String host = "localhost";
	
	      // Get system properties
		Properties properties = System.getProperties();
	
	      // Setup mail server
	      properties.setProperty("mail.smtp.host", host);
	
	      // Get the default Session object.
	      Session session = Session.getDefaultInstance(properties);
	
	      try {
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);
	
	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));
	
	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
	
	         // Set Subject: header field
	         message.setSubject("[Notification] " + doc + " a été retourné !");
	
	         // Now set the actual message
	         message.setText("Réservez rapidement " + doc + " avant qu'il ne soit trop tard, que le premier gagne !\n\n - L'équipe de la Bibliothèque (143 Avenue de Versailles)\n\n\n**Ce message a été envoyé automatiquement, merci de ne pas y répondre!**");
	
	         // Send message
	         Transport.send(message);
	         return true;
	      } catch (MessagingException mex) {
	         mex.printStackTrace();
	         return false;
	      }
	}
	
}
