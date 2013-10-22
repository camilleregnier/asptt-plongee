package com.asptt.plongee.resa.wicket.page.admin.adherent;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.AdherentDataProvider;
import com.asptt.plongee.resa.model.AdherentDataView;
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

public class GererAdherents extends TemplatePage {

    private static ModalWindow modalModif;
    private static ModalWindow modalSupp;
    private static ModalWindow modalPwd;
    private static List<Adherent> list;

    public static ModalWindow getModalModif() {
        return modalModif;
    }

    public static ModalWindow getModalSupp() {
        return modalSupp;
    }

    public static ModalWindow getModalPwd() {
        return modalPwd;
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
    public GererAdherents() {
        setPageTitle("Gerer les adherents");
        setOutputMarkupId(true);
        modalModif = new ModalWindow("modal2");
        modalModif.setTitle("This is modal window with panel content.");
        modalModif.setCookieName("modal-adherent");
        add(modalModif);

        modalSupp = new ModalWindow("modalSupp");
        modalSupp.setTitle("Confirmation");
        modalSupp.setResizable(false);
        modalSupp.setInitialWidth(30);
        modalSupp.setInitialHeight(15);
        modalSupp.setWidthUnit("em");
        modalSupp.setHeightUnit("em");
        modalSupp.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
        add(modalSupp);

        modalPwd = new ModalWindow("modalPwd");
        modalPwd.setTitle("Confirmation");
        modalPwd.setResizable(false);
        modalPwd.setInitialWidth(30);
        modalPwd.setInitialHeight(15);
        modalPwd.setWidthUnit("em");
        modalPwd.setHeightUnit("em");
        modalPwd.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
        add(modalPwd);

        List<Adherent> adherents = ResaSession.get().getAdherentService().rechercherAdherentsTous();

        DataView adherentsView = new AdherentDataView("adherentsView", new AdherentDataProvider(adherents), 10);
        // On construit la liste des adhérents (avec pagination)
        adherentsView.setOutputMarkupId(true);
        add(adherentsView);
        add(new PagingNavigator("navigator", adherentsView));

        add(new GererAdherentForm("gererAdherentForm"));

    }

    public static void replaceModalWindowModif(AjaxRequestTarget target, IModel<Adherent> adherent) {
        modalModif.setContent(new GererAdherentsModifPanel(modalModif.getContentId(), adherent));
        modalModif.setTitle("Modifiez les informations \u00e0 mettre \u00e0 jour");
        modalModif.setUseInitialHeight(true);

        // Pour éviter le message de disparition de la fenetre lors de la validation
        target.appendJavascript("Wicket.Window.unloadConfirmation  = false;");
    }

    public static void replaceModalWindowSupp(AjaxRequestTarget target, IModel<Adherent> adherent) {
        modalSupp.setContent(new GererAdherentsConfSuppPanel(modalSupp.getContentId(), adherent));
        modalSupp.setTitle("Supprimez un adherent");
        modalSupp.setUseInitialHeight(true);

        // Pour éviter le message de disparition de la fenetre lors de la validation
        target.appendJavascript("Wicket.Window.unloadConfirmation  = false;");
    }

    public static void replaceModalWindowPwd(AjaxRequestTarget target, IModel<Adherent> adherent) {
        modalPwd.setContent(new GererAdherentsConfReInitPwdPanel(modalPwd.getContentId(), adherent));
        modalPwd.setTitle("Réinitialisation du mot de passe d'un adhérent");
        modalPwd.setUseInitialHeight(true);

        // Pour éviter le message de disparition de la fenetre lors de la validation
        target.appendJavascript("Wicket.Window.unloadConfirmation  = false;");
    }
}