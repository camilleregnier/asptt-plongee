package com.asptt.plongee.resa.model;

import org.apache.wicket.model.LoadableDetachableModel;

import com.asptt.plongee.resa.service.PlongeeService;
import com.asptt.plongee.resa.service.impl.PlongeeServiceImpl;

public class DetachablePlongeeModel extends LoadableDetachableModel<Plongee> {
	private final Integer id;
	private PlongeeService plongeeService;


	
	public DetachablePlongeeModel(PlongeeService plongeeService, Integer id) {
		super();
		this.id = id;
		this.plongeeService = plongeeService;
	}

	@Override
	protected Plongee load() {
		// TODO Auto-generated method stub
		return plongeeService.rechercherPlongeeParId(id);
	}

}
