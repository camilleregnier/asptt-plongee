/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asptt.plongee.resa.wicket.page.fichesecurite;

/**
 *
 * @author ecus6396
 */
public class ChoiceRenderPlongeur {

    private String key;
    private String Value;

    public ChoiceRenderPlongeur(String key, String Value) {
        this.key = key;
        this.Value = Value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String Value) {
        this.Value = Value;
    }
}
