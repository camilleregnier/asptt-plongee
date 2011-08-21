package com.asptt.plongee.resa.model;

import java.util.Comparator;

public class AdherentComparatorNom implements Comparator<Adherent> {

	@Override
	public int compare(Adherent o1, Adherent o2) {
		return o1.getNom().compareTo(o2.getNom());
	}

}
