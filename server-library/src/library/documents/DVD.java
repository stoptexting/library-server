package library.documents;

import library.data.Abonne;
import util.GestionnaireBannissement;

public class DVD extends ADocument {
	private boolean adulte;
	
	public DVD(int id, String nom, boolean adulte) {
		super(id, nom);
		this.adulte = adulte;
	}
	
	public boolean aboEstAdulte(Abonne abo) {
		assert abo != null;
		return abo.estAdulte();
	}
	
	// boolean pour la confirmation
	@Override
	public void reservationPour(Abonne abo) {
		assert abo != null;
		synchronized(this) {
			if (adulte) {
				if (aboEstAdulte(abo))
					super.reservationPour(abo);
				return;
			}
			super.reservationPour(abo);
			return;
		}
	}
	
	@Override
	public String toString() {
		return this.getNom() + " (DVD)" + (adulte ? "(+16)" : "");
	}

}
