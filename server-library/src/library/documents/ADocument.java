package library.documents;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import library.data.Abonne;

class LibererTimer extends TimerTask {
	private Document doc;
	protected Date limit;
	
	public LibererTimer(Document doc, Date limit) {
		this.doc = doc;
		this.limit = limit;
	}
	
	@Override
	public void run() {
		synchronized (doc) {
			doc.notifyAll();
			((ADocument) doc).taskLiberer = null;
			this.doc.retour();
		}
	}
	
	public String toString() {
		System.err.println(this.limit);
		return this.limit.toString();
	}
	
}

public abstract class ADocument implements Document {
	private int id;
	private String nom;
	private Abonne emprunteur;
	private Abonne reserveur;
	
	private static Timer timer;
	protected LibererTimer taskLiberer;
	
	static {
		timer = new Timer();
	}
	
	public ADocument(int id, String nom) {
		this.id = id;
		this.nom = nom;
	}
	
	@Override
	public int numero() {
		return id;
	}

	@Override
	public Abonne emprunteur() {
		return this.emprunteur;
	}

	@Override
	public Abonne reserveur() {
		return this.reserveur;
	}

	@Override
	public void reservationPour(Abonne ab) {
		assert ab != null && this.reserveur == null && this.emprunteur == null && !ab.estBanni();
		synchronized(this) {
			Calendar cal = Calendar.getInstance();
			//cal.add(Calendar.HOUR_OF_DAY, 2);
			cal.add(Calendar.SECOND, 20);
			Date dateLimite = new Date(cal.getTimeInMillis());;
			
			this.taskLiberer = new LibererTimer(this, dateLimite);
			timer.schedule(taskLiberer, dateLimite);
			this.reserveur = ab;
		}
	}

	@Override
	public void empruntPar(Abonne ab) {
		assert ab != null && this.reserveur == ab && this.emprunteur == null && !ab.estBanni();
		synchronized(this) {
			this.emprunteur = ab;
			this.reserveur = null;
			
			if (taskLiberer != null)
				taskLiberer.cancel(); // stop task 2h car emprunté
		}
		
	}

	@Override
	public void retour() {
		this.emprunteur = null;
		this.reserveur = null;
	}

	public String getNom() {
		return nom;
	}
	
	public String getLimit() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'à' HH:mm:ss");
        String formattedDate = formatter.format(this.taskLiberer.limit);
		return formattedDate;
	}
	
	public boolean limitBientotAtteinte() {
		if (this.taskLiberer != null)
			return this.taskLiberer.limit.getTime() - System.currentTimeMillis() <= 30 * 1000; // -= 30 secondes
		return false;
	}

}
