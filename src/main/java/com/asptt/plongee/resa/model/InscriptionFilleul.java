package com.asptt.plongee.resa.model;

public class InscriptionFilleul {
	
	private Adherent filleul;
	private Plongee plongeeInscrit;
	
	public InscriptionFilleul() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InscriptionFilleul(Adherent filleul, Plongee plongeeInscrit) {
		super();
		this.filleul = filleul;
		this.plongeeInscrit = plongeeInscrit;
	}

	public Adherent getFilleul() {
		return filleul;
	}

	public void setFilleul(Adherent filleul) {
		this.filleul = filleul;
	}

	public Plongee getPlongeeInscrit() {
		return plongeeInscrit;
	}

	public void setPlongeeInscrit(Plongee plongeeInscrit) {
		this.plongeeInscrit = plongeeInscrit;
	}

}
