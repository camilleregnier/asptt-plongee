package com.asptt.plongee.resa.ui.web.wicket.page.inscription;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.InscritsPlongeeDataProvider;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.ui.web.wicket.page.TemplatePage;

public class InscriptionFailurePlongeePage extends TemplatePage {

	@SuppressWarnings("serial")
	public InscriptionFailurePlongeePage(Plongee plongee) {
		
		Adherent a = getResaSession().getAdherent();
	    add(new Label("hello","DESOLE, "+a.getPrenom()+" Inscription impossible sur cette plong\u00e9e"));
	}

}
