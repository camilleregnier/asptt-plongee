package com.asptt.plongee.resa.model;

import org.apache.wicket.model.LoadableDetachableModel;

import com.asptt.plongee.resa.service.AdherentService;
import com.asptt.plongee.resa.service.PlongeeService;
import com.asptt.plongee.resa.service.impl.PlongeeServiceImpl;

public class DetachableAdherentModel extends LoadableDetachableModel<Adherent> {
	private final String id;
	private AdherentService adherentService;
	
	public DetachableAdherentModel(AdherentService adherentService, String id) {
		super();
		this.id = id;
		this.adherentService = adherentService;
	}

	@Override
	protected Adherent load() {
		return adherentService.rechercherAdherentParIdentifiant(id);
	}

}
