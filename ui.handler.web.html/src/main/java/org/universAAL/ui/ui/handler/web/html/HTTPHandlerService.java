/*******************************************************************************
 * Copyright 2013 Universidad Politécnica de Madrid
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

package org.universAAL.ui.ui.handler.web.html;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.ontology.profile.User;
import org.universAAL.ri.servicegateway.GatewayPort;

/**
 * @author amedrano
 *
 */
public class HTTPHandlerService extends GatewayPort {

	/**
	 * FileName for the main configuration File.
	 */
	public static final String CONF_FILENAME = "html";
	
	/**
	 * Property key for Location of the CSS to use
	 */
	public static final String CSS_LOCATION = "css.location";

	/**
	 * Property key for the location of the resources directory.
	 */
	public static final String RESOURCES_LOC = "resources.dir";
	
	/**
	 * Property key for the location of the servlet within the http container.
	 */
	public static final String SERVICE_URL = "service.relURL";
	
	/**
	 * Property key for the session timeout, time after which (and with no activity)
	 * the servlet will interpret the session as expired.
	 */
	public static final String TIMEOUT = "session.timeout";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    /**
     * The properties file.
     */
    private UpdatedPropertiesFile properties;
  

	/**
	 * The pool of <{@link User}, {@link HTMLUserGenerator}>.
	 */
	private Hashtable generatorPool;
	
	/**
	 * The pool of <{@link User}, {@link Watchdog}>, to keep all watch dogs leased.
	 */
	private Hashtable watchDogKennel;
	
	/**
     * Directory for configuration files.
     */
    private static String homeDir = "./"; 
    

	/**
	 * @param mcontext
	 */
	public HTTPHandlerService(ModuleContext mcontext, File prop) {
		super(mcontext);
		homeDir = prop.getParent();
		properties = new UpdatedPropertiesFile(prop) {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public String getComments() {
				return "UI Handler Web Properties";
			}
			
			protected void addDefaults(Properties defaults) {
				defaults.put(SERVICE_URL, "/universAAL");
		        defaults.put(RESOURCES_LOC, homeDir + "/" + "resources");
		        defaults.put(CSS_LOCATION, 
		        		ResourceMapper.cached(
		        		(String) properties.getProperty(RESOURCES_LOC),
		        		this.getClass().getClassLoader().getResource("default.css")));
		        defaults.put(TIMEOUT, "300000");
				
			}
		};
		//Load Properties
	    LogUtils.logDebug(getContext(), getClass(), 
	    		"Constructor",
	    		"loading properties");
	    try {
			properties.loadProperties();
		} catch (IOException e) {
			LogUtils.logError(getContext(), getClass(), "constructor",
					new String[] {"unable to read properties file"}, e);
		}
	    generatorPool = new Hashtable();
	    watchDogKennel = new Hashtable();
	}

	/** {@ inheritDoc}	 */
	public String dataDir() {
		return properties.getProperty(RESOURCES_LOC);
	}

	/** {@ inheritDoc}	 */
	public String url() {
		return properties.getProperty(SERVICE_URL);
	}

    /** {@ inheritDoc}	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (handleAuthorization(req, resp)) {
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("text/html");
			String authHeader = req.getHeader("Authorization");
			String[] userPass = getUserAndPass(authHeader);
			String user = (String) userURIs.get(userPass[0]);
			HTMLUserGenerator ug = getGenerator(user);
			// send the latest Available form for the user
			PrintWriter os = resp.getWriter();
			os.print(ug.getHTML());
			os.flush();
			os.close();
			//XXX use session?
		}
	}

	/**
	 * @param user
	 * @return
	 */
	private HTMLUserGenerator getGenerator(String user) {
		User u = (User) Resource.getResource(User.MY_URI, user);
		if (!generatorPool.contains(u)){
			generatorPool.put(u, new HTMLUserGenerator(this, u));
		}
		if (!watchDogKennel.contains(u)){
			watchDogKennel.put(u, new Watchdog(u));
		}
		else {
			((Watchdog)watchDogKennel.get(u)).liveForAnotherDay();
		}
		return (HTMLUserGenerator) generatorPool.get(u);
	}

	/** {@ inheritDoc}	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (handleAuthorization(req, resp)) {
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("text/html");
			String authHeader = req.getHeader("Authorization");
			String[] userPass = getUserAndPass(authHeader);
			String user = (String) userURIs.get(userPass[0]);
			HTMLUserGenerator ug = getGenerator(user);
			// gather input and send it to the bus if applicable
			ug.processInput(req.getParameterMap());
			// send the latest Available form for the user
//			PrintWriter os = resp.getWriter();
//			os.print(ug.getHTML());
//			os.flush();
//			os.close();
			//Redirect to Get
			resp.sendRedirect(url());
		}

	}
	
	public Properties getProperties(){
		return properties;
	}

	/**
	 * Get the configuration directory.
	 * @return the home directory (ends with "/")
	 */
	public static String getHomeDir() {
	    return homeDir;
	}
	
	private class Watchdog extends TimerTask{
		
		private User user;
		private Timer timer;

		/**
		 * 
		 */
		public Watchdog(User u) {
			user = u;
			reschedule();
		}
		private void reschedule(){
			timer = new Timer("Web session WatchDog for user " + user.getURI(), true);
			timer.schedule(this, Long.parseLong(properties.getProperty(TIMEOUT)));
		}
		
		public void liveForAnotherDay(){
			timer.cancel();
			reschedule();
		}
		
		/** {@ inheritDoc}	 */
		public void run() {
			if (generatorPool != null){
				HTMLUserGenerator ug = (HTMLUserGenerator)generatorPool.get(user);
				if (ug != null)
					ug.finish();
				generatorPool.remove(user);
			}
			timer.cancel();
			watchDogKennel.remove(user);
		}
		
	}

	/** {@ inheritDoc} */
	public boolean unregister() {
		watchDogKennel.clear();
		for (Iterator i = generatorPool.values().iterator(); i.hasNext();) {
			HTMLUserGenerator ug = (HTMLUserGenerator) i.next();
			ug.finish();
		}
		generatorPool.clear();
		return super.unregister();
	}
}