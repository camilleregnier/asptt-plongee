package com.asptt.plongee.resa.wicket.page;

import com.asptt.plongee.resa.exception.TechnicalException;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Page affichant une erreur technique.
 */
public class ErreurTechniquePage extends TemplatePage {

    public ErreurTechniquePage(TechnicalException e) {
        super();
        add(new Label("msgTech", "ERREUR TECHNIQUE :" + e.getKey()));
    }
}
