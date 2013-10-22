package com.asptt.plongee.resa.wicket.page;

import com.asptt.plongee.resa.wicket.ResaSession;
import com.asptt.plongee.resa.wicket.page.admin.adherent.CreerAdherent;
import com.asptt.plongee.resa.wicket.page.admin.adherent.GererAdherents;
import com.asptt.plongee.resa.wicket.page.admin.externe.GererExternes;
import com.asptt.plongee.resa.wicket.page.admin.message.CreerMessage;
import com.asptt.plongee.resa.wicket.page.admin.message.GererMessage;
import com.asptt.plongee.resa.wicket.page.admin.plongee.AnnulerPlongee;
import com.asptt.plongee.resa.wicket.page.admin.plongee.CreerPlongee;
import com.asptt.plongee.resa.wicket.page.admin.plongee.GererPlongeeAOuvrirOne;
import com.asptt.plongee.resa.wicket.page.admin.plongee.attente.GererListeAttenteOne;
import com.asptt.plongee.resa.wicket.page.consultation.ConsulterPlongees;
import com.asptt.plongee.resa.wicket.page.consultation.InfoAdherent;
import com.asptt.plongee.resa.wicket.page.fichesecurite.SaisieDate;
import com.asptt.plongee.resa.wicket.page.inscription.DeInscriptionPlongeePage;
import com.asptt.plongee.resa.wicket.page.inscription.DesinscriptionFilleulPage;
import com.asptt.plongee.resa.wicket.page.inscription.InscriptionFilleulPlongeePage;
import com.asptt.plongee.resa.wicket.page.inscription.InscriptionPlongeePage;
import com.asptt.plongee.resa.wicket.page.secretariat.DesInscriptionPlongeePage;
import com.asptt.plongee.resa.wicket.page.secretariat.InscriptionAdherentPlongeePage;
import com.asptt.plongee.resa.wicket.page.secretariat.InscriptionExterieurPlongeePage;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.PropertyModel;

public abstract class TemplatePage extends WebPage {

    /**
     * title of the current page.
     */
    private String pageTitle = "ASPTT Marseille Plongee";
    MarkupContainer userMenus = new MarkupContainer("menuUser") {
        @Override
        public boolean isVisible() {
            return ResaSession.get().getRoles().hasRole("USER");
        }
    };
    MarkupContainer secretariatMenus = new MarkupContainer("menuSecretariat") {
        @Override
        public boolean isVisible() {
            return ResaSession.get().getRoles().hasRole("SECRETARIAT");
        }
    };
    MarkupContainer adminMenus = new MarkupContainer("menuAdmin") {
        @Override
        public boolean isVisible() {
            return ResaSession.get().getRoles().hasRole("ADMIN");
        }
    };

    public TemplatePage() {
        super();
        add(new Label("title", new PropertyModel(this, "pageTitle")));
        //menu ADHERENT
        userMenus.add(addConsultLink());
//		resaPlongee.add(addReservLink());
//		userMenus.add(resaPlongee);
        userMenus.add(addReservLink());
        userMenus.add(addDeInscrireLink());
        userMenus.add(addInscrireFilleulLink());
        userMenus.add(addDesinscrireFilleulLink());
        userMenus.add(addInfoLink());
        add(userMenus);
        //menu ADMIN
        adminMenus.add(addCreerPlongeeLink());
        adminMenus.add(addAnnulerPlongeeLink());
        adminMenus.add(addOuvrirPlongeeLink());
        adminMenus.add(addListeAttentePlongeeLink());
        adminMenus.add(addCreerAdherentLink());
        adminMenus.add(addGererAdherentLink());
        adminMenus.add(addGererExterneLink());
        adminMenus.add(addGererMessageLink());
        adminMenus.add(addCreerMessageLink());
        adminMenus.add(addGererFichesSecuriteLink());
        add(adminMenus);
        //menu SECRETARIAT
        secretariatMenus.add(addInscrireAdherentLink());
        secretariatMenus.add(addInscrireExterneLink());
        secretariatMenus.add(addSecretDeInscrirePlongeeLink());
        secretariatMenus.add(addSecretConsultPlongeeLink());
        add(secretariatMenus);
    }

    private Link addConsultLink() {
        Link link = new Link("consultPlongee") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(ConsulterPlongees.class);
            }
        };
        add(link);
        return link;
    }

    private Link addInscrireFilleulLink() {
        Link link = new Link("inscrireFilleul") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(new InscriptionFilleulPlongeePage(ResaSession.get().getAdherent()));
            }
        };
        add(link);
        return link;
    }

    private Link addDesinscrireFilleulLink() {
        Link link = new Link("desinscrireFilleul") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(new DesinscriptionFilleulPage());
            }
        };
        add(link);
        return link;
    }

    private Link addInfoLink() {
        Link link = new Link("infoAdherent") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(InfoAdherent.class);
            }
        };
        add(link);
        return link;
    }

    private Link addReservLink() {
        Link link = new Link("reservPlongee") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(InscriptionPlongeePage.class);
            }
        };
        //add(link);
        return link;
    }

    private Link addDeInscrireLink() {
        Link link = new Link("deInscrirePlongee") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(DeInscriptionPlongeePage.class);
            }
        };
        //add(link);
        return link;
    }

    private Link addCreerPlongeeLink() {
        Link link = new Link("creerPlongee") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(CreerPlongee.class);
            }
        };
        //add(link);
        return link;
    }

    private Link addAnnulerPlongeeLink() {
        Link link = new Link("annulerPlongee") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(AnnulerPlongee.class);
            }
        };
        add(link);
        return link;
    }

    private Link addOuvrirPlongeeLink() {
        Link link = new Link("ouvrirPlongee") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(GererPlongeeAOuvrirOne.class);
            }
        };
        add(link);
        return link;
    }

    private Link addListeAttentePlongeeLink() {
        Link link = new Link("listeAttentePlongee") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(GererListeAttenteOne.class);
            }
        };
        add(link);
        return link;
    }

    private Link addCreerAdherentLink() {
        Link link = new Link("creerAdherent") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(CreerAdherent.class);
            }
        };
        add(link);
        return link;
    }

    private Link addGererAdherentLink() {
        Link link = new Link("gererAdherent") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(GererAdherents.class);
            }
        };
        add(link);
        return link;
    }

    private Link addGererExterneLink() {
        Link link = new Link("gererExterne") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(GererExternes.class);
            }
        };
        add(link);
        return link;
    }

    private Link addGererMessageLink() {
        Link link = new Link("gererMessage") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(GererMessage.class);
            }
        };
        add(link);
        return link;
    }

    private Link addCreerMessageLink() {
        Link link = new Link("creerMessage") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(CreerMessage.class);
            }
        };
        add(link);
        return link;
    }

    private Link addGererFichesSecuriteLink() {
        Link link = new Link("gererFichesSecurite") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(SaisieDate.class);
            }
        };
        add(link);
        return link;
    }

    private Link addInscrireAdherentLink() {
        Link link = new Link("inscrireAdherent") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(InscriptionAdherentPlongeePage.class);
            }
        };
        add(link);
        return link;
    }

    private Link addInscrireExterneLink() {
        Link link = new Link("inscrireExterne") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(InscriptionExterieurPlongeePage.class);
            }
        };
        add(link);
        return link;
    }

    private Link addSecretDeInscrirePlongeeLink() {
        Link link = new Link("secretDeInscrirePlongee") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(DesInscriptionPlongeePage.class);
            }
        };
        add(link);
        return link;
    }

    private Link addSecretConsultPlongeeLink() {
        Link link = new Link("secretConsultPlongee") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(ConsulterPlongees.class);
            }
        };
        add(link);
        return link;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }
}
