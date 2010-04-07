package com.asptt.plongee.resa.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Plongee implements Serializable {

	public static enum Type {matin, apres_midi, soir, nuit }
	
	private Integer id;
	private Type type;
	private NiveauAutonomie niveauMinimum;
	private int nbPlaces;
	private String niveauDP;
//	// statut : ouverte ou non
//	private boolean statut;
	private String lieux;
	private Date date;
	private Boolean ouvertureForcee;
	private Adherent dp;
	private Adherent pilote;
	private List<Adherent> participants;
	private List<Adherent> participantsEnAttente;

	
}
