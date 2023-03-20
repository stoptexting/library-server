package util;

import java.sql.Date;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import library.data.Abonne;

class DebanissementTask extends TimerTask {
	private Abonne abo;
	
	public DebanissementTask(Abonne abo) {
		this.abo = abo;
	}
	@Override
	public void run() {
		GestionnaireBannissement.debannir(abo);
	}
	
}


public class GestionnaireBannissement {
	private static Timer timer;
	
	static {
		GestionnaireBannissement.timer = new Timer();
	}
	
	private GestionnaireBannissement() {}
	
	public static void bannir(Abonne abo) {
		TimerTask unban = new DebanissementTask(abo);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 30);
		Date dateUnban = new Date(cal.getTimeInMillis());;
		
		timer.schedule(unban, dateUnban); // 1 mois
		abo.setDateFinBannissement(dateUnban);
		System.err.println(abo.getDateFinBannissement() + " BRUHBRUHBRUHBRUHBRUHBRUHBRUHBRUHBRUHBRUHBRUHBRUHBRUHBRUHBRUH");
	}
	
	public static void debannir(Abonne abo) {
		System.err.println(abo + " a été debanni!");
		abo.setDateFinBannissement(null);
	}
	
}
