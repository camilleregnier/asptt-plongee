package com.asptt.plongee.resa.model;

import org.apache.wicket.model.LoadableDetachableModel;

import com.asptt.plongee.resa.exception.TechnicalException;

public class DetachableFilleulPlongeeModel extends LoadableDetachableModel<InscriptionFilleul> {

	private static final long serialVersionUID = 7356020767941072700L;
	private InscriptionFilleul filleul;

	
	public DetachableFilleulPlongeeModel(InscriptionFilleul filleul) {
		super();
		this.filleul = filleul;
	}

	@Override
	protected InscriptionFilleul load()  throws TechnicalException{
		return filleul;
	}

}
