package com.asptt.plongee.resa.ui.web.wicket.page;

import org.apache.wicket.markup.html.WebPage;

import com.asptt.plongee.resa.ui.web.wicket.ResaSession;

public abstract class TemplatePage extends WebPage {

	public ResaSession getResaSession(){
		return (ResaSession) getSession();
	}
}
