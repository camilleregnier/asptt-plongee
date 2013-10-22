/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asptt.plongee.resa.util;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;

/**
 *
 * @author ecus6396
 */
public class FocusOnLoadBehavior extends AbstractBehavior
{
    private Component component;
 
    @Override
    public void bind( Component component )
    {
        this.component = component;
        component.setOutputMarkupId(true);
    }
 
    @Override
    public void renderHead( IHeaderResponse iHeaderResponse )
    {
        super.renderHead(iHeaderResponse);
        iHeaderResponse.renderOnLoadJavascript("document.getElementById('" + component.getMarkupId() + "').focus()");
    }
 
    @Override
    public boolean isTemporary()
    {
        // remove the behavior after component has been rendered      
        return true;
    }
}  
