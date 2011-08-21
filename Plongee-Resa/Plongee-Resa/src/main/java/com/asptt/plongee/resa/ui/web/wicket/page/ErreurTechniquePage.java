package com.asptt.plongee.resa.ui.web.wicket.page;

import org.apache.wicket.markup.html.basic.Label;

import com.asptt.plongee.resa.exception.TechnicalException;



/**
 * Page affichant une erreur technique.
 */
public class ErreurTechniquePage extends TemplatePage {

	public ErreurTechniquePage(TechnicalException e ) {
		super();
	    add(new Label("msgTech","ERREUR TECHNIQUE :"+e.getKey()));
	}

}
