package com.asptt.plongee.resa.ui.web.wicket.page;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.PropertyModel;

import com.asptt.plongee.resa.ui.web.wicket.ResaSession;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.AnnulerPlongee;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.CreerAdherent;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.CreerMessage;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.CreerPlongee;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.GererAdherents;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.GererListeAttenteOne;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.GererMessage;
import com.asptt.plongee.resa.ui.web.wicket.page.admin.GererPlongeeAOuvrirOne;
import com.asptt.plongee.resa.ui.web.wicket.page.consultation.ConsulterPlongees;
import com.asptt.plongee.resa.ui.web.wicket.page.consultation.InfoAdherent;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.DeInscriptionPlongeePage;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.DesinscriptionFilleulPage;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.InscriptionFilleulPlongeePage;
import com.asptt.plongee.resa.ui.web.wicket.page.inscription.InscriptionPlongeePage;
import com.asptt.plongee.resa.ui.web.wicket.page.secretariat.DesInscriptionPlongeePage;
import com.asptt.plongee.resa.ui.web.wicket.page.secretariat.InscriptionAdherentPlongeePage;
import com.asptt.plongee.resa.ui.web.wicket.page.secretariat.InscriptionExterieurPlongeePage;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class TemplatePage extends WebPage {

    /**
     * title of the current page.
     */
    private String pageTitle = "ASPTT Marseille Plongee";
    MarkupContainer userMenus = new MarkupContainer("menuUser") {
        @Override
        public boolean isVisible() {
            return getResaSession().get().getRoles().hasRole("USER");
        }
    };
    MarkupContainer secretariatMenus = new MarkupContainer("menuSecretariat") {
        @Override
        public boolean isVisible() {
            return getResaSession().get().getRoles().hasRole("SECRETARIAT");
        }
    };
    MarkupContainer adminMenus = new MarkupContainer("menuAdmin") {
        @Override
        public boolean isVisible() {
            return getResaSession().get().getRoles().hasRole("ADMIN");
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
        adminMenus.add(addGererMessageLink());
        adminMenus.add(addCreerMessageLink());
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

            public void onClick() {
                setResponsePage(ConsulterPlongees.class);
            }
        };
        add(link);
        return link;
    }

    private Link addInscrireFilleulLink() {
        Link link = null;
        try {
            getResaSession().getPlongeeService().checkCertificatMedical(
                    getResaSession().getAdherent(), null);
            link = new Link("inscrireFilleul") {
                private static final long serialVersionUID = 1L;

                public void onClick() {
                    setResponsePage(new InscriptionFilleulPlongeePage(getResaSession().getAdherent()));
                }
            };
        } catch (TechnicalException ex) {
            Logger.getLogger(TemplatePage.class.getName()).log(Level.SEVERE, null, ex);
            link = new Link("inscrireFilleul") {
                private static final long serialVersionUID = 1L;

                public void onClick() {
                    setResponsePage(new AccueilPage());
                }
            };
        } catch (ResaException ex) {
            Logger.getLogger(TemplatePage.class.getName()).log(Level.SEVERE, null, ex);
            link = new Link("inscrireFilleul") {
                private static final long serialVersionUID = 1L;

                public void onClick() {
                    setResponsePage(new AccueilPage());
                }
            };
        }
        add(link);
        return link;
    }

    private Link addDesinscrireFilleulLink() {
        Link link = new Link("desinscrireFilleul") {
            private static final long serialVersionUID = 1L;

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

            public void onClick() {
                setResponsePage(GererAdherents.class);
            }
        };
        add(link);
        return link;
    }

    private Link addGererMessageLink() {
        Link link = new Link("gererMessage") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

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

            public void onClick() {
                setResponsePage(CreerMessage.class);
            }
        };
        add(link);
        return link;
    }

    private Link addInscrireAdherentLink() {
        Link link = new Link("inscrireAdherent") {
            //DefaultButtonImageResource def = new DefaultButtonImageResource("Cancel"){
            private static final long serialVersionUID = 1L;

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

            public void onClick() {
                setResponsePage(ConsulterPlongees.class);
            }
        };
        add(link);
        return link;
    }

    public ResaSession getResaSession() {
        return (ResaSession) getSession();
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }
}
