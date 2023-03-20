package library.data;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;

public class Abonne {
	private int id;
	private String nom;
	private Date dateNaissance;
	private Date dateFinBannissement;
	
	public Abonne(int id, String nom, Date dateNaissance, Date dateFinBannissement) {
		this.id = id;
		this.nom = nom;
		this.dateNaissance = dateNaissance;
		this.dateFinBannissement = dateFinBannissement;
	}
	
	
	public boolean estBanni() {
		return (dateFinBannissement != null);
	}
	
	public boolean estAdulte() {
		LocalDate now = LocalDate.now();
		LocalDate datenaissance = this.dateNaissance.toLocalDate();
		Period period = Period.between(datenaissance, now);
		int age = period.getYears();
		return age >= 16;
	}

	public int getId() {
		return id;
	}

	public String getNom() {
		return nom;
	}
	
	public String toString() {
		return this.nom + "(id=" + this.id + ")";
	}

	public void setDateFinBannissement(Date date) {
		this.dateFinBannissement = date;
	}
	
	public String getDateFinBannissement() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'à' HH:mm:ss");
        String formattedDate = formatter.format(this.dateFinBannissement);
        System.err.println(this.dateFinBannissement);
		return formattedDate;
	}
}
