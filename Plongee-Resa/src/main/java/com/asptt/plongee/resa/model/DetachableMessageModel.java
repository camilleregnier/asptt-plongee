package com.asptt.plongee.resa.model;

import org.apache.wicket.model.LoadableDetachableModel;

import com.asptt.plongee.resa.exception.TechnicalException;

public class DetachableMessageModel extends LoadableDetachableModel<Message> {

	private static final long serialVersionUID = 7356020767941072700L;
	private Message message;

	
	public DetachableMessageModel(Message message) {
		super();
		this.message = message;
	}

	@Override
	protected Message load()  throws TechnicalException{
		return message;
	}

}
