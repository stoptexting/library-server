package library.data;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;

public class Abonne {
	private int id;
	private String nom;
	private Date dateNaissance;
	private Date dateBannissement;
	
	public Abonne(int id, String nom, Date dateNaissance, Date dateBannissement) {
		this.id = id;
		this.nom = nom;
		this.dateNaissance = dateNaissance;
		this.dateBannissement = dateBannissement;
	}
	
	
	public boolean estBanni() {
		return (dateBannissement != null);
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
}
