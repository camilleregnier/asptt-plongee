package com.asptt.plongee.resa.model;

import org.apache.wicket.model.LoadableDetachableModel;

import com.asptt.plongee.resa.service.AdherentService;
import com.asptt.plongee.resa.service.PlongeeService;
import com.asptt.plongee.resa.service.impl.PlongeeServiceImpl;

public class DetachableAdherentModel extends LoadableDetachableModel<Adherent> {
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
