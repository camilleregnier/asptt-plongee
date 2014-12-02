package com.asptt.plongee.resa.model;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

@SuppressWarnings("serial")
public class ContactUrgentDataProvider implements IDataProvider<ContactUrgent> {
	
	List<ContactUrgent> contactUrgents;
	
	public ContactUrgentDataProvider (List<ContactUrgent> contactUrgents){
		this.contactUrgents = contactUrgents;
	}

	@Override
	public Iterator<ContactUrgent> iterator(int first, int count) {
		return contactUrgents.subList(first, first+count).iterator();
	}

	@Override
	public int size() {
		return contactUrgents.size();
	}

	@Override
	public void detach() {
		// TODO Auto-generated method stub
	}

	@Override
	public IModel<ContactUrgent> model(ContactUrgent contactUrgent) {
		return new DetachableContactUrgentModel(contactUrgent);
	}

}
