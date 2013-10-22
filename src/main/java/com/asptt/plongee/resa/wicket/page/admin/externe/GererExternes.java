package com.asptt.plongee.resa.wicket.page.admin.externe;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.AdherentDataProvider;
import com.asptt.plongee.resa.model.ExterneDataView;
import com.asptt.plongee.resa.wicket.ResaSession;
import com.asptt.plongee.resa.wicket.page.TemplatePage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

public class GererExternes extends TemplatePage {

    private static ModalWindow modalModif;
    private static ModalWindow modalSupp;
    private static List<Adherent> list;

    public static ModalWindow getModalModif() {
        return modalModif;
    }

    public static ModalWindow getModalSupp() {
        return modalSupp;
    }

    public static List<Adherent> getMatchingAdherents(String search) {
        if (Strings.isEmpty(search)) {
            List<Adherent> emptyList = Collections.emptyList();
            return emptyList;
        }

        if (list == null) {
            list = ResaSession.get().getAdherentService()
                    .rechercherAdherentsActifs();
        }

        ArrayList<Adherent> newList = new ArrayList<Adherent>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getNom().startsWith(search.toUpperCase())) {
                newList.add(list.get(i));
            }
        }
        return newList;
    }

    @SuppressWarnings("serial")
    public GererExternes() {
        setPageTitle("Gerer les externes");
        setOutputMarkupId(true);
        modalModif = new ModalWindow("modalModif");
        modalModif.setTitle("This is modal window with panel content.");
        modalModif.setCookieName("modal-adherent");
        add(modalModif);

        modalSupp = new ModalWindow("modalSupp");
        modalSupp.setTitle("Confirmation");
        modalSupp.setResizable(true);
        modalSupp.setInitialWidth(450);
        modalSupp.setInitialHeight(300);
        modalSupp.setWidthUnit("px");
        modalSupp.setHeightUnit("px");
        modalSupp.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
        add(modalSupp);

        List<Adherent> externes = ResaSession.get().getAdherentService().rechercherExternes();

        DataView externesView = new ExterneDataView("externesView", new AdherentDataProvider(externes), 10);
        // On construit la liste des adhérents (avec pagination)
        externesView.setOutputMarkupId(true);
        add(externesView);
        add(new PagingNavigator("navigator", externesView));

        add(new GererExterneForm("gererExterneForm"));

    }

    public static void replaceModalWindowModif(AjaxRequestTarget target, IModel<Adherent> externe) {
        modalModif.setContent(new ExterneModifPanel(modalModif.getContentId(), externe));
        modalModif.setTitle("Modifiez les informations \u00e0 mettre \u00e0 jour");
        modalModif.setUseInitialHeight(true);

        // Pour éviter le message de disparition de la fenetre lors de la validation
        target.appendJavascript("Wicket.Window.unloadConfirmation  = false;");
    }

    public static void replaceModalWindowSupp(AjaxRequestTarget target, IModel<Adherent> externe) {
        modalSupp.setContent(new GererExternesConfSuppPanel(modalSupp.getContentId(), externe));
        modalSupp.setTitle("Supprimez un externe");
        modalSupp.setUseInitialHeight(true);

        // Pour éviter le message de disparition de la fenetre lors de la validation
        target.appendJavascript("Wicket.Window.unloadConfirmation  = false;");
    }

}