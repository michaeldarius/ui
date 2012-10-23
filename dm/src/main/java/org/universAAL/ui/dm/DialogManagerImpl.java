/*******************************************************************************
 * Copyright 2012 Universidad Politécnica de Madrid
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
package org.universAAL.ui.dm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.owl.supply.AbsLocation;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.service.ServiceCaller;
import org.universAAL.middleware.ui.DialogManager;
import org.universAAL.middleware.ui.UICaller;
import org.universAAL.middleware.ui.UIRequest;
import org.universAAL.middleware.ui.UIResponse;
import org.universAAL.ui.dm.osgi.DialogManagerActivator;

/**
 * The UICaller implements the interface
 * {@link org.universAAL.middleware.ui.DialogManager}.
 * 
 * @author mtazari
 * @author eandgrg
 * @author amedrano
 * @version 1.0
 * @created 26-sep-2012 13:03:50
 */
public class DialogManagerImpl extends UICaller implements DialogManager {

	/**
	 * Singleton instance.
	 */
	private static DialogManagerImpl singleton;

	/**
	 * Map of {@link UserDialogManager} delegates. Key is the user's URI.
	 */
	private Map<String, UserDialogManager> udmMap;

	/**
	 * Another {@link Map} to speed up
	 * {@link DialogManagerImpl#dialogFinished(String)},
	 * {@link DialogManagerImpl#getSuspendedDialog(String)}
	 * {@link DialogManagerImpl#suspendDialog(String)}
	 * {@link DialogManagerImpl#dialogAborted(String)} and
	 * {@link DialogManagerImpl#handleUIResponse(UIResponse)} methods.
	 */
	private Map<String, UserDialogManager> dialogIDMap;

	/**
	 * A mini Garbage collector for {@link DialogManagerImpl#dialogIDMap}
	 */
	private Timer gbSchedule;
	/**
	 * The bus reference to make calls to it.
	 */
	// private static UIBus bus;

	/**
	 * The {@link ModuleContext} reference.
	 */
	private static ModuleContext moduleContext;

	// private StoreProvider m_StoreProvider;

	/**
	 * Prefix for all DM calls (Submits).
	 */
	public static final String CALL_PREFIX = "urn:ui.dm:UICaller"; //$NON-NLS-1$

	/**
	 * Execution period for the {@link DialogManagerImpl#dialogIDMap} Garbage collector.
	 */
	private static final long GC_PERIOD = 600000; // 10 min

	private DialogManagerImpl(ModuleContext context) {
		super(context);
		moduleContext = context;
		udmMap = new TreeMap<String, UserDialogManager>();
		dialogIDMap = new HashMap<String, UserDialogManager>();
		gbSchedule = new Timer(true);
		gbSchedule.scheduleAtFixedRate(new DMGC(), GC_PERIOD, GC_PERIOD);
		// bus = (UIBus) context.getContainer()
		// .fetchSharedObject(context, UIBusImpl.busFetchParams);
	}

	/** {@inheritDoc} */
	public void finalize() throws Throwable {

	}

	/**
	 * This method is called by the UI bus and determines whether a dialog can
	 * be shown directly (e.g. by comparing the dialogs priority with the
	 * priority of a dialog that is currently shown). Additionally, it adds
	 * adaptation parameters.
	 * 
	 * @see org.universAAL.middleware.ui.DialogManager#checkNewDialog(UIRequest)
	 * @param request
	 *            The UI request containing a dialog.
	 * @return true, if the dialog can be shown directly.
	 */
	public boolean checkNewDialog(UIRequest request) {
		if (request != null) {
			String uURI = request.getAddressedUser().getURI();
			UserDialogManager udm = udmMap.get(uURI);
			if (udm == null) {
				udm = new UserDialogManager(request.getAddressedUser(),
						null);
				udmMap.put(uURI, udm);
			}
			dialogIDMap.put(request.getDialogID(), udm);
			return udm.checkNewDialog(request);
		}
		return false;
	}

	/**
	 * This method is called by the UI bus to inform the dialog manager that a
	 * dialog was successfully finished. The dialog manager can then show
	 * dialogs that were previously suspended.
	 * 
	 * @see org.universAAL.middleware.ui.DialogManager#dialogFinished(String)
	 * @param dialogID
	 *            ID of the dialog that is now finished.
	 */
	public void dialogFinished(String dialogID) {
		UserDialogManager udm = dialogIDMap.get(dialogID);
		if (udm != null) {
			udm.dialogFinished(dialogID);
			dialogIDMap.remove(dialogID);
		} else {
			getModuleContext().logError("UserDM Management",
					"Unable to locate UDM for dialog: " + dialogID, null);
		}
	}

	/** {@inheritDoc} */
	public void getMainMenu(Resource user, AbsLocation location) {
		if (user != null) {
			String uURI = user.getURI();
			if (!udmMap.containsKey(uURI)) {
				udmMap.put(uURI, new UserDialogManager(user, location));
			} else {
				udmMap.get(uURI).setCurrentUserLocation(location);
			}
			udmMap.get(uURI).getMainMenu(user, location);
		}
	}

	/**
	 * Get a suspended dialog. Removes the dialog from 'suspendedDialogs' and
	 * adds it to 'runningDialogs'.
	 * 
	 * @param dialogID
	 *            ID of the dialog.
	 */
	public UIRequest getSuspendedDialog(String dialogID) {
		UserDialogManager udm = dialogIDMap.get(dialogID);
		if (udm != null) {
			return udm.getSuspendedDialog(dialogID);
		} else {
			getModuleContext().logError("UserDM Management",
					"Unable to locate UDM for dialog: " + dialogID, null);
			return null;
		}
	}

	/**
	 * Suspend a dialog. This method is called by the UI bus and removes the
	 * given dialog from 'runningDialogs' and stores it in 'suspendedDialogs'.
	 * 
	 * @param dialogID
	 *            ID of the dialog.
	 */
	public void suspendDialog(String dialogID) {
		UserDialogManager udm = dialogIDMap.get(dialogID);
		if (udm != null) {
			udm.suspendDialog(dialogID);
		} else {
				getModuleContext().logError("UserDM Management",
						"Unable to locate UDM for dialog: " + dialogID, null);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void communicationChannelBroken() {
		// TODO Auto-generated method stub

	}

	/**
	 * This method is called when an event on the UI bus occurs indicating that
	 * a dialog was aborted.
	 * 
	 * @param dialogID
	 */
	@Override
	public void dialogAborted(String dialogID) {
		UserDialogManager udm = dialogIDMap.get(dialogID);
		if (udm != null) {
			udm.dialogAborted(dialogID);
			dialogIDMap.remove(dialogID);
		} else {
			getModuleContext().logError("UserDM Management",
					"Unable to locate UDM for dialog: " + dialogID, null);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void handleUIResponse(UIResponse response) {
		if (response == null) {
			DialogManagerImpl.getModuleContext().logError("handleUIResponse", "Null Response", null);
			return;
		}
		if (response.getSubmissionID() == null) {
			DialogManagerActivator.getModuleContext().logError("handleUIResponse", "sumission ID null!", null);
			return;
		}
		UserDialogManager udm = udmMap.get(response.getUser().getURI());
		if (udm != null) {
			udm.handleUIResponse(response);
		} else {
			getModuleContext().logError("UserDM Management",
					"Unable to locate UDM for " + response.getUser().getURI(), null);
		}
	}

	/**
	 * Create a Singleton Instance.
	 * 
	 * @param mc
	 */
	public static void createInstance(ModuleContext mc) {
		if (singleton == null) {
			singleton = new DialogManagerImpl(mc);
		}
	}

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static DialogManagerImpl getInstance() {
		return singleton;
	}

	/**
	 * @return
	 */
	public static ServiceCaller getServiceCaller() {
		return DialogManagerActivator.getServiceCaller();
	}

	/**
	 * The module context reference.
	 * 
	 * @returnThe module context reference.
	 */
	public static ModuleContext getModuleContext() {
		return moduleContext;
	}

	private class DMGC extends TimerTask {

		@Override
		public void run() {
			Set<String> remove = new HashSet<String>();
			for (String dID : dialogIDMap.keySet()) {
				UserDialogManager udm = dialogIDMap.get(dID);
				if (udm == null
						||( udm.getDialogPool().get(dID) == null
						&& udm.getMessagePool().get(dID) == null)) {
					remove.add(dID);
				}
			}
			for (String key : remove) {
				dialogIDMap.remove(key);
			}
		}

	}
}
