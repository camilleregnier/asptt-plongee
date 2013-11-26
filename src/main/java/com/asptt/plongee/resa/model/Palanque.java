/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asptt.plongee.resa.model;

import com.asptt.plongee.resa.wicket.page.admin.fichesecurite.ChoiceRenderPlongeur;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 *
 * @author ecus6396
 */
public class Palanque implements Serializable {

    private static final long serialVersionUID = -8569233748603210161L;
    private Integer id;
    private int numero;
    private Date datePlongee;
    private ChoiceRenderPlongeur guide;
    private ChoiceRenderPlongeur plongeur1;
    private ChoiceRenderPlongeur plongeur2;
    private ChoiceRenderPlongeur plongeur3;
    private ChoiceRenderPlongeur plongeur4;
    private int profondeurMaxPrevue;
    private int profondeurMaxRea;
    private int dureeTotalePrevue;
    private int dureeTotaleRea;
    private int palier3m;
    private int palier6m;
    private int palier9m;
    private int palierProfond;
    private int heureSortie;
    private int minuteSortie;
    private Calendar calendarHeureSortie;
    private ChoiceRenderPlongeur selectedPlongeur;
    private List<ChoiceRenderPlongeur> listPlongeursAInscrire;
    private List<ChoiceRenderPlongeur> listPlongeursPalanque;

    public Palanque(Map<String, String> plongeursAInscrireMap, Date datePlongee) {
        this.datePlongee = datePlongee;
        listPlongeursAInscrire = new ArrayList<ChoiceRenderPlongeur>();
        for (Map.Entry<String, String> e : plongeursAInscrireMap.entrySet()) {
            ChoiceRenderPlongeur sp = new ChoiceRenderPlongeur(e.getKey(), e.getValue());
            listPlongeursAInscrire.add(sp);
        }
    }

    public ChoiceRenderPlongeur getSelectedPlongeur() {
        return selectedPlongeur;
    }

    public void setSelectedPlongeur(ChoiceRenderPlongeur selectedPlongeur) {
        this.selectedPlongeur = selectedPlongeur;
    }

    public List<ChoiceRenderPlongeur> getListPlongeursAInscrire() {
        return listPlongeursAInscrire;
    }

    public void setListPlongeursAInscrire(List<ChoiceRenderPlongeur> listPlongeursPalanque) {
        this.listPlongeursAInscrire = listPlongeursPalanque;
    }

    public List<ChoiceRenderPlongeur> getListPlongeursPalanque() {
        return listPlongeursPalanque;
    }
    
    public void setListPlongeursPalanque(List<ChoiceRenderPlongeur> listPlongeursPalanque) {
        this.listPlongeursPalanque = listPlongeursPalanque;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public ChoiceRenderPlongeur getGuide() {
        return guide;
    }

    public void setGuide(ChoiceRenderPlongeur guide) {
        this.guide = guide;
    }

    public ChoiceRenderPlongeur getPlongeur1() {
        return plongeur1;
    }

    public void setPlongeur1(ChoiceRenderPlongeur plongeur1) {
        this.plongeur1 = plongeur1;
    }

    public ChoiceRenderPlongeur getPlongeur2() {
        return plongeur2;
    }

    public void setPlongeur2(ChoiceRenderPlongeur plongeur2) {
        this.plongeur2 = plongeur2;
    }

    public ChoiceRenderPlongeur getPlongeur3() {
        return plongeur3;
    }

    public void setPlongeur3(ChoiceRenderPlongeur plongeur3) {
        this.plongeur3 = plongeur3;
    }

    public ChoiceRenderPlongeur getPlongeur4() {
        return plongeur4;
    }

    public void setPlongeur4(ChoiceRenderPlongeur plongeur4) {
        this.plongeur4 = plongeur4;
    }

    public int getProfondeurMaxPrevue() {
        return profondeurMaxPrevue;
    }

    public void setProfondeurMaxPrevue(int profondeurMaxPrevue) {
        this.profondeurMaxPrevue = profondeurMaxPrevue;
    }

    public int getProfondeurMaxRea() {
        return profondeurMaxRea;
    }

    public void setProfondeurMaxRea(int profondeurMaxRea) {
        this.profondeurMaxRea = profondeurMaxRea;
    }

    public int getDureeTotalePrevue() {
        return dureeTotalePrevue;
    }

    public void setDureeTotalePrevue(int dureeTotalePrevue) {
        this.dureeTotalePrevue = dureeTotalePrevue;
    }

    public int getDureeTotaleRea() {
        return dureeTotaleRea;
    }

    public void setDureeTotaleRea(int dureeTotaleRea) {
        this.dureeTotaleRea = dureeTotaleRea;
    }

    public int getPalier3m() {
        return palier3m;
    }

    public void setPalier3m(int palier3m) {
        this.palier3m = palier3m;
    }

    public int getPalier6m() {
        return palier6m;
    }

    public void setPalier6m(int palier6m) {
        this.palier6m = palier6m;
    }

    public int getPalier9m() {
        return palier9m;
    }

    public void setPalier9m(int palier9m) {
        this.palier9m = palier9m;
    }

    public int getPalierProfond() {
        return palierProfond;
    }

    public void setPalierProfond(int palierProfond) {
        this.palierProfond = palierProfond;
    }

    public int getHeureSortie() {
        return heureSortie;
    }

    public void setHeureSortie(int heureSortie) {
        this.heureSortie = heureSortie;
    }

    public int getMinuteSortie() {
        return minuteSortie;
    }

    public void setMinuteSortie(int minuteSortie) {
        this.minuteSortie = minuteSortie;
    }

    public Calendar getCalendarHeureSortie() {
        return calendarHeureSortie;
    }

    public void setCalendarHeureSortie(Calendar calendarHeureSortie) {
        this.calendarHeureSortie = calendarHeureSortie;
    }

    public void setCalendarHeureSortie(int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        cal.setTime(datePlongee);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        this.calendarHeureSortie = cal;
    }

    public String getNomGuide() {
        if (null == getGuide()) {
            return null;
        } else {
            return getGuide().getValue();
        }
    }

    public String getIdGuide() {
        if (null == getGuide()) {
            return null;
        } else {
            return getGuide().getKey();
        }
    }

    public String getNomPlongeur1() {
        if (null == getPlongeur1()) {
            return null;
        } else {
            return getPlongeur1().getValue();
        }
    }

    public String getIdPlongeur1() {
        if (null == getPlongeur1()) {
            return null;
        } else {
            return getPlongeur1().getKey();
        }
    }

    public String getNomPlongeur2() {
        if (null == getPlongeur2()) {
            return null;
        } else {
            return getPlongeur2().getValue();
        }
    }

    public String getIdPlongeur2() {
        if (null == getPlongeur2()) {
            return null;
        } else {
            return getPlongeur2().getKey();
        }
    }

    public String getNomPlongeur3() {
        if (null == getPlongeur3()) {
            return null;
        } else {
            return getPlongeur3().getValue();
        }
    }

    public String getIdPlongeur3() {
        if (null == getPlongeur3()) {
            return null;
        } else {
            return getPlongeur3().getKey();
        }
    }

    public String getNomPlongeur4() {
        if (null == getPlongeur4()) {
            return null;
        } else {
            return getPlongeur4().getValue();
        }
    }

    public String getIdPlongeur4() {
        if (null == getPlongeur4()) {
            return null;
        } else {
            return getPlongeur4().getKey();
        }
    }
}
