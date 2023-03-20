package library.documents;

import java.text.SimpleDateFormat;
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
		this.doc.retour();
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
	private LibererTimer taskLiberer;
	
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
		assert ab != null && this.reserveur == null && this.emprunteur == null;
		synchronized(this) {
			this.taskLiberer = new LibererTimer(this, new Date(System.currentTimeMillis() + 60*60*1000*2));
			timer.schedule(taskLiberer, 60 * 60 * 1000 * 2);
			this.reserveur = ab;
		}
	}

	@Override
	public void empruntPar(Abonne ab) {
		assert ab != null && this.reserveur == ab && this.emprunteur == null;
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

}
