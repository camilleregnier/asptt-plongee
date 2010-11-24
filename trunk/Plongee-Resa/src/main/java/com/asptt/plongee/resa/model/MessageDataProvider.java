package com.asptt.plongee.resa.model;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import com.asptt.plongee.resa.exception.TechnicalException;

@SuppressWarnings("serial")
public class MessageDataProvider implements IDataProvider<Message> {

	List<Message> messages;
	

	public MessageDataProvider (List<Message> messages) {
		this.messages = messages;
	}

	@Override
	public Iterator<Message> iterator(int first, int count){
			return messages.subList(first, first+count).iterator();
	}

	@Override
	public int size() {
		return messages.size();
	}

	@Override
	public void detach() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IModel<Message> model(Message message) throws TechnicalException{
		return new DetachableMessageModel(message);
	}


}
