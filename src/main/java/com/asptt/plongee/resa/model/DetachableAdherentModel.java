package com.asptt.plongee.resa.model;

import org.apache.wicket.model.LoadableDetachableModel;

public class DetachableAdherentModel extends LoadableDetachableModel<Adherent> {

	private static final long serialVersionUID = 8521845330656989309L;
	private Adherent adherent;
	
	public DetachableAdherentModel(Adherent adherent) {
		super();
		this.adherent = adherent;
	}

	@Override
	protected Adherent load() {
		return adherent;
	}

}
