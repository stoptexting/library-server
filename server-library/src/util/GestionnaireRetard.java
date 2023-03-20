package util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import library.data.Abonne;
import library.documents.Document;

class RetardTask extends TimerTask {
	private Abonne abo;
	private Date dateLimite;
	
	public RetardTask(Abonne abo, Date dateLimite) {
		this.abo = abo;
		this.dateLimite = dateLimite;
	}
	@Override
	public void run() {
		GestionnaireBannissement.bannir(abo);
		System.err.println(abo + " a été banni de la tribu suite à un retard!");
	}
	
	public String toString() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'à' HH:mm:ss");
        String formattedDate = formatter.format(this.dateLimite);
		return formattedDate;
	}
	
}


public class GestionnaireRetard {
	private static HashMap<Document, TimerTask> tasks;
	private static Timer timer;
	
	static {
		GestionnaireRetard.timer = new Timer();
		GestionnaireRetard.tasks = new HashMap<>();
	}
	
	private GestionnaireRetard() {}

	public static void emprunterAvecRetard(Document doc, Abonne abo) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 12);
		Date dateLimite = new Date(cal.getTimeInMillis());;
		
		TimerTask timeout = new RetardTask(abo, dateLimite);
		timer.schedule(timeout, dateLimite); // 2 semaines
		GestionnaireRetard.tasks.put(doc, timeout);	
		doc.empruntPar(abo);
	}
	
	public static void retour(Document doc) {
		TimerTask task = GestionnaireRetard.tasks.get(doc);
		if (task != null) {
			task.cancel();
			GestionnaireRetard.tasks.remove(doc);
		}
		doc.retour();
	}
	
	public static String getDateLimiteRetour(Document doc) {
        TimerTask task = GestionnaireRetard.tasks.get(doc);
		if (task != null) {
			return task.toString();
		}
		return "{document non emprunté}";
	}
}
