package com.asptt.plongee.resa.model;

import org.apache.wicket.model.LoadableDetachableModel;

import com.asptt.plongee.resa.service.AdherentService;
import com.asptt.plongee.resa.service.PlongeeService;
import com.asptt.plongee.resa.service.impl.PlongeeServiceImpl;

public class DetachableAdherentModel extends LoadableDetachableModel<Adherent> {
	private final String id;
	private AdherentService adherentService;
	private Adherent adherent;
	
	public DetachableAdherentModel(AdherentService adherentService, String id, Adherent adherent) {
		super();
		this.id = id;
		this.adherentService = adherentService;
		this.adherent = adherent;
	}

	@Override
	protected Adherent load() {
		if(null != adherent){
			return adherent;
		} else {
			return adherentService.rechercherAdherentParIdentifiantTous(id);
		}
	}

}
