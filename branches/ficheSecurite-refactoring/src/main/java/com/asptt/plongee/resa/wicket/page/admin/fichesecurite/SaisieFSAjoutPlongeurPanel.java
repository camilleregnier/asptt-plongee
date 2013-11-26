/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asptt.plongee.resa.wicket.page.admin.fichesecurite;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.mail.PlongeeMail;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.FicheSecurite;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.wicket.ResaSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.objectautocomplete.AutoCompletionChoicesProvider;
import org.wicketstuff.objectautocomplete.ObjectAutoCompleteBuilder;
import org.wicketstuff.objectautocomplete.ObjectAutoCompleteField;
import org.wicketstuff.objectautocomplete.ObjectAutoCompleteRenderer;

/**
 *
 * @author ecus6396
 */
public class SaisieFSAjoutPlongeurPanel extends Panel {

    private List<Adherent> list;
    private final Logger logger = Logger.getLogger(getClass());
    FeedbackPanel feedback = new FeedbackPanel("feedback");
    private FicheSecurite fs;
    private Plongee plongee;
        
    
    public SaisieFSAjoutPlongeurPanel(String id) {
        super(id);
        //Récupération de la fiche de securite dans la session
        fs = ResaSession.get().getFicheSecurite();
        //init de la plongée
        this.plongee = fs.getPlongee();
        //le feedback panel pour les message d'erreur
        feedback.setOutputMarkupId(true);
        add(feedback);
        
        add(new PlongeursForm("plongeursForm"));
    }

    public List<Adherent> getMatchingAdherents(String search) {
        if (Strings.isEmpty(search)) {
            List<Adherent> emptyList = Collections.emptyList();
            return emptyList;
        }

        if (list == null) {
            List l_ext = ResaSession.get().getAdherentService()
                    .rechercherExternes();
            list = ResaSession.get().getAdherentService()
                    .rechercherAdherentsActifs();
            list.addAll(l_ext);
        }

        ArrayList<Adherent> newList = new ArrayList<Adherent>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getNom().startsWith(search.toUpperCase())) {
                newList.add(list.get(i));
            }
        }
        return newList;
    }

    class PlongeursForm extends Form {

        private static final long serialVersionUID = 5529259540931669786L;
        ObjectAutoCompleteField<Adherent, String> autocompleteField;

        public PlongeursForm(String id) {
            super(id);

            AutoCompletionChoicesProvider<Adherent> provider = new AutoCompletionChoicesProvider<Adherent>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Iterator<Adherent> getChoices(String input) {
                    return getMatchingAdherents(input).iterator();
                }
            };

            ObjectAutoCompleteRenderer<Adherent> renderer = new ObjectAutoCompleteRenderer<Adherent>() {
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

            add(new AjaxButton("valider") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
//                        FicheSecurite fs = (FicheSecurite) form.getModelObject();
                    // Récupération du plongeur avec une requete en base
                    Adherent plongeur = ResaSession.get().getAdherentService().rechercherAdherentParIdentifiant(autocompleteField.getConvertedInput());
                    try {
                        // Inscription du plongeur
                        ResaSession.get().getPlongeeService().inscrireAdherent(plongee, plongeur, PlongeeMail.PAS_DE_MAIL);
                        //On met à jour la plongée dans le fiche de securite
                        plongee = ResaSession.get().getPlongeeService().rechercherPlongeeParId(plongee.getId());
                        fs.setPlongee(plongee);
                        //On met à jour la fiche de securite dans la session
                        ResaSession.get().setFicheSecurite(fs);
                        //retour sur la page de saisie de la fiche de securite
                        setResponsePage(new SaisieFicheSecurite());
                    } catch (ResaException ex) {
                        logger.warn("Pas glop" + ex.getMessage());
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(feedback);
                }
            });

            add(new Link("cancel") {
                @Override
                public void onClick() {
                    setResponsePage(new SaisieFicheSecurite());
                }
            });

        }
    }
}
