package com.asptt.plongee.resa.model;

import org.apache.wicket.model.LoadableDetachableModel;

import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.service.PlongeeService;
import com.asptt.plongee.resa.service.impl.PlongeeServiceImpl;

public class DetachablePlongeeModel extends LoadableDetachableModel<Plongee> {
	private Plongee plongee;

	
	public DetachablePlongeeModel(Plongee plongee) {
		super();
		this.plongee = plongee;
	}

	@Override
	protected Plongee load()  throws TechnicalException{
		return plongee;
	}

}
