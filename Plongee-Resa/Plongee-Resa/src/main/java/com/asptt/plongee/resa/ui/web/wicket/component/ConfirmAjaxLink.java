package com.asptt.plongee.resa.ui.web.wicket.component;

import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.calldecorator.AjaxCallDecorator;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;

@SuppressWarnings("unchecked")
public abstract class ConfirmAjaxLink extends IndicatingAjaxLink {

	private String confirmMessage;

		private static final long serialVersionUID = 1389708787081345734L;

		public ConfirmAjaxLink(String id, String confirmMessage) {
	        super(id);
	        this.confirmMessage = confirmMessage;
	    }

	    @Override
	    protected IAjaxCallDecorator getAjaxCallDecorator() {
	        return new AjaxCallDecorator() {

				private static final long serialVersionUID = -9140948408987483828L;

				public CharSequence decorateScript(CharSequence script) {
	                return "if(!confirm('" + confirmMessage +"')) return false;" + script;
	            }
	        };
	    }

}
