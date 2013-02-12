package com.asptt.plongee.resa.model;

import org.apache.wicket.model.LoadableDetachableModel;

public class DetachableContactUrgentModel extends LoadableDetachableModel<ContactUrgent> {

	private static final long serialVersionUID = 8521845330656989309L;
	private ContactUrgent contactUrgent;
	
	public DetachableContactUrgentModel(ContactUrgent contactUrgent) {
		super();
		this.contactUrgent = contactUrgent;
	}

	@Override
	protected ContactUrgent load() {
		return contactUrgent;
	}

}
