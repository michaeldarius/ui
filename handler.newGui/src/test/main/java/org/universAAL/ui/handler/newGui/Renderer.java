/*******************************************************************************
 * Copyright 2011 Universidad Politécnica de Madrid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.universAAL.ui.handler.newGui;

import java.util.Properties;


import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.input.InputPublisher;
import org.universAAL.middleware.io.owl.AccessImpairment;
import org.universAAL.middleware.io.rdf.Form;
import org.universAAL.middleware.output.OutputEvent;
import org.universAAL.middleware.output.OutputSubscriber;
import org.universAAL.middleware.owl.supply.AbsLocation;
import org.universAAL.ontology.location.Location;
import org.universAAL.ontology.profile.User;
import org.universAAL.ui.handler.newGui.formManagement.FormManager;
import org.universAAL.ui.handler.newGui.formManagement.QueuedFormManager;
import org.universAAL.ui.handler.newGui.formManagement.SimpleFormManager;

/**
 * Coordinator Class for Swing GUI Handler.
 *
 * @author <a href="mailto:amedrano@lst.tfo.upm.es>amedrano</a>
 *
 */
public class Renderer extends Thread {

    /**
     * Singleton instance for the renderer.
     *
     *  as future feature we may want to have several
     *  instances of the Swing GUI Handler, when there
     *  are more than 1 display.
     */
    private static Renderer singleton=null;

    /**
     * The specific {@link InputPublisher}
     * instance for Swing GUI Handler.
     */
    public IPublisher ipublisher=null;

    /**
     * The specific {@link OutputSubscriber}
     * instance for Swing GUI Handler.
     */
    public OSubscriber osubscriber=null;

    /**
     * uAAL {@link ModuleContext} to make uAAL operations
     */
    static ModuleContext moduleContext = null;

    /**
     * The configuration properties read from the file.
     */
    private static Properties fileProp;

    /**
     * Form Logic Manager. it will decide
     * which Form to show when.
     */
    private FormManager fm;

    /**
     * Default User, for when there is no user
     * logged in.
     * @see Renderer#DEMO_MODE
     */
    private static String DEFAULT_USER="saied";

    /**
     * The Key value for the demo mode configuration property.
     * demo mode will disable login and will use default
     * user.
     * Default: demo.mode=true.
     * @see Renderer#fileProp
     * @see Renderer#DEFAULT_USER
     */
    private static String DEMO_MODE="demo.mode";

    /**
     * The Key value for the location configuration property.
     * this location is used when publishing input events.
     * Default: gui.location=Unknown
     * @see Renderer#fileProp
     */
    private static String GUI_LOCATION="gui.location";

    /**
     * The Key value for the Form manager selection
     * configuration property.
     * this will select between the available {@link FormManager}s
     * Default: queued.forms=false
     * @see Renderer#fm
     * @see QueuedFormManager
     * @see SimpleFormManager
     */
    private static String QUEUE_MODE="queued.forms";

    /**
     * Constructor, using Singleton pattern:
     * only Renderer Class can create an instance,
     * to help contribute to the singleton pattern.
     * @see Renderer#getInstance()
     */
    private Renderer() {
        ipublisher=new IPublisher(Renderer.moduleContext);
        osubscriber=new OSubscriber(Renderer.moduleContext);
        loadProperties();
        ModelMapper.updateLAF();
        if (Boolean.parseBoolean(getProerty(QUEUE_MODE))) {
            fm = new QueuedFormManager();
        }
        else {
            fm = new SimpleFormManager();
        }
    }

    /**
     * load configuration properties from a file, setting the
     * default for those which are not defined.
     * @see Renderer#fileProp
     */
    private void loadProperties() {
        fileProp = new Properties();
        fileProp.put(DEMO_MODE, "true");
        fileProp.put(ModelMapper.LAFPackageProperty, ModelMapper.DefaultLAFPackage);
        fileProp.put(GUI_LOCATION,"Unkown");
        fileProp.put(QUEUE_MODE, "false");
        /*
         * TODO try to load from file, if not create file from defaults.
         */
    }

    /**
     * Access method to the singleton Object
     * @return
     *         singleton instance
     */
    public static Renderer getInstance() {
        if (singleton == null && moduleContext != null) {
            singleton = new Renderer();
            singleton.start();
        }
        return singleton;
    }

    /**
     * get the {@link Renderer#moduleContext}
     * @return
     */
    public static ModuleContext getModuleContext() {
        return moduleContext;
    }

    /**
     * set the {@link Renderer#moduleContext}.
     * To be used only by Activator Class.
     */
    public static void setModuleContext(ModuleContext moduleContext) {
        Renderer.moduleContext = moduleContext;
    }

    /**
     * Main, Top Level Renderer Logic.
     */
    public void run() {
        /*
         * TODO
         *     Does it really need to be a Thread?
         * TODO
         *     if login required
         *      Display Login Form (with which L&F?)
         *      authenticate user
         *  create user instance
         */
        User u = new User(DEFAULT_USER);
        setCurrentUser(u);
    }

    /**
     *  Terminate current dialog
     *  and all pending dialogs.
     */
    public void finish() {
        /*
         *  should there be some feedback to the application - DM?
         */
        fm.flush();
        ipublisher.close();
        osubscriber.close();
    }

    /**
     * Access to the property file.
     * @param string
     *         Key of property to access
     * @return
     *         String Value of the property
     * @see Renderer#fileProp
     */
    public static String getProerty(String string) {
        try {
            return (String) fileProp.get(string);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * get the {@link FormManager} being used
     * useful to access the current output event
     * and current form.
     * @return {@link Renderer#fm}
     */
    public FormManager getFormManagement() {
        return fm;        
    }

    /**
     * Gets the form being displayed right now
     * @return
     */
    public Form getCurrentForm() {
        return fm.getCurrentDialog().getDialogForm();
    }

    /**
     * Get the logged in user, the one that is in theory
     * receiving and manipulating the dialogs.
     * 
     * @return
     *         ontlogical representation of the user.
     */
    public User getCurrentUser() {
        return ipublisher.getCurrentUser();
    }

    /**
     * Set the user that has just authenticated.
     * @param user
     */
    void setCurrentUser(User user) {
        ipublisher.setCurrentUser(user);
        osubscriber.userAuthenticated(user);
        ipublisher.requestMainMenu();
    }

    /**
     * Check if impairment is listed as present impairments for the
     * current user and form.
     * @param impariment
     *         the {@link AccessImpairment} to be checked
     * @return
     *         true is impairment is present in the current Dialog event.
     * @see AccessImpairment
     * @see OutputEvent
     */
    public boolean hasImpairment(AccessImpairment impariment) {
        AccessImpairment[] imp = fm.getCurrentDialog().getImpairments();
        int i = 0;
        while (i<imp.length && imp[i]!= impariment)
            i++;
        return i!=imp.length;
    }

    /**
     * Get the Language that should be used
     * @return
     *         the two-letter representation of the language-
     */
    public String getLanguage() {
        return fm.getCurrentDialog().getDialogLanguage().getDisplayVariant();
    }

    /**
     * Get the location where the handler is displaying the dialogs.
     * @return
     *         location of the handler's display
     */
    public AbsLocation whereAmI() {
        /*
         *  Read Location from properties
         *  TODO other location process?
         */
        return new Location(getProerty(GUI_LOCATION));
    }
}