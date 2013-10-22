/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asptt.plongee.resa.wicket.page.admin.adherent;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.DetachableAdherentModel;
import com.asptt.plongee.resa.wicket.ResaSession;
import java.util.Iterator;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxSubmitButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.wicketstuff.objectautocomplete.AutoCompletionChoicesProvider;
import org.wicketstuff.objectautocomplete.ObjectAutoCompleteBuilder;
import org.wicketstuff.objectautocomplete.ObjectAutoCompleteField;
import org.wicketstuff.objectautocomplete.ObjectAutoCompleteRenderer;

/**
 *
 * @author ecus6396
 */
public class GererAdherentForm extends Form {

    private static final long serialVersionUID = 5529259540931669786L;
    ObjectAutoCompleteField<Adherent, String> autocompleteField;

    public GererAdherentForm(String id, IModel model) {
        super(id, model);
    }

    public GererAdherentForm(String id) {
        super(id);
        init();
    }

    private void init() {
        AutoCompletionChoicesProvider<Adherent> provider = new AutoCompletionChoicesProvider<Adherent>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Iterator<Adherent> getChoices(String input) {
                return GererAdherents.getMatchingAdherents(input).iterator();
            }
        };

        ObjectAutoCompleteRenderer<Adherent> renderer;
        renderer = new ObjectAutoCompleteRenderer<Adherent>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected String getIdValue(Adherent adherent) {
                return adherent.getNumeroLicense();
            }

            @Override
            protected String getTextValue(Adherent adherent) {
                String texteAffiche = adherent.getNom() + " " + adherent.getPrenom() + " " + (adherent.getPrerogative());
                return texteAffiche;
            }
        };

        ObjectAutoCompleteBuilder<Adherent, String> builder = new ObjectAutoCompleteBuilder<Adherent, String>(provider);
        builder.autoCompleteRenderer(renderer);
        builder.searchLinkText("Autre recherche");
        builder.width(200);


        autocompleteField = builder.build("numeroLicense", new Model<String>());
        final TextField<String> adherent = autocompleteField.getSearchTextField();
        adherent.setRequired(true);


        add(autocompleteField);

        add(new IndicatingAjaxSubmitButton("valider", this) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                IModel<Adherent> adh = new DetachableAdherentModel(ResaSession.get().getAdherentService().rechercherAdherentParIdentifiant(autocompleteField.getConvertedInput()));
                GererAdherents.replaceModalWindowModif(target, adh);
                GererAdherents.getModalModif().show(target);
            }
        });
    }
}
