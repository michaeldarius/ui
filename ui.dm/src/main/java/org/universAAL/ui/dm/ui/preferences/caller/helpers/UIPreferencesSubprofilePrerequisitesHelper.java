/*******************************************************************************
 * Copyright 2013 Ericsson Nikola Tesla d.d.
 *
 * See the NOTICE file distributed with this work for additional 
 * information regarding copyright ownership
 *	
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *	
 * http://www.apache.org/licenses/LICENSE-2.0
 *	
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.universAAL.ui.dm.ui.preferences.caller.helpers;

import java.util.Iterator;
import java.util.List;

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.DefaultServiceCaller;
import org.universAAL.middleware.service.ServiceRequest;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.middleware.service.owls.process.ProcessOutput;
import org.universAAL.ontology.profile.Profilable;
import org.universAAL.ontology.profile.User;
import org.universAAL.ontology.profile.UserProfile;
import org.universAAL.ontology.profile.service.ProfilingService;

/**
 * Communicates with Profiling Server and acts as a helper when initializing
 * {@link UIPreferencesSubprofile} for a {@link User}
 * 
 * @author eandgrg
 * 
 */
public class UIPreferencesSubprofilePrerequisitesHelper {
	public static ModuleContext mc = null;
	public static final String NAMESPACE = "http://ontology.ent.hr/UIPreferencesSubprofilePrerequisitesHelper#";

	public static final String OUTPUT_USERS = NAMESPACE + "OUT_USERS";
	public static final String OUTPUT_USER = NAMESPACE + "OUT_USER";
	public static final String OUTPUT_GETPROFILE = NAMESPACE + "OUTPUT_GETPROFILE";

	public DefaultServiceCaller sc;

	public UIPreferencesSubprofilePrerequisitesHelper(ModuleContext mc) {
		UIPreferencesSubprofilePrerequisitesHelper.mc = mc;
		sc = new DefaultServiceCaller(mc);
	}

	/**
	 * @param user
	 *            {@link User}
	 * @return if the {@link User} existed and was obtained from the Profiling
	 *         Server return true, false otherwise
	 */
	// TODO currently not used
	public boolean getUserSucceeded(Resource user) {
		ServiceRequest req = new ServiceRequest(new ProfilingService(), null);
		req.addValueFilter(new String[] { ProfilingService.PROP_CONTROLS }, user);
		req.addRequiredOutput(OUTPUT_USER, new String[] { ProfilingService.PROP_CONTROLS });

		ServiceResponse resp = sc.call(req);
		if (resp.getCallStatus() == CallStatus.succeeded) {
			Object out = getReturnValue(resp.getOutputs(), OUTPUT_USER);
			if (out != null) {
				LogUtils.logDebug(mc, this.getClass(), "getUserSucceeded",
						new Object[] { "User: " + user.getURI() + " obtained from Profiling server (so it exists)" },
						null);
				return true;
			} else {
				return false;
			}
		} else {
			LogUtils.logDebug(mc, this.getClass(), "getUserSucceeded",
					new Object[] { "Call for User: " + user.getURI() + " not succeeded or User does not exist." },
					null);
			return false;
		}
	}

	/**
	 * @param user
	 *            {@link User}
	 * @return
	 */
	// TODO currently not used
	public String getUserAsString(Resource user) {
		ServiceRequest req = new ServiceRequest(new ProfilingService(), null);
		req.addValueFilter(new String[] { ProfilingService.PROP_CONTROLS }, user);
		req.addRequiredOutput(OUTPUT_USER, new String[] { ProfilingService.PROP_CONTROLS });

		ServiceResponse resp = sc.call(req);
		if (resp.getCallStatus() == CallStatus.succeeded) {
			Object out = getReturnValue(resp.getOutputs(), OUTPUT_USER);
			if (out != null) {
				return out.toString();
			} else {

				return "nothing";
			}
		} else {
			return resp.getCallStatus().name();
		}
	}

	/**
	 * 
	 * @param user
	 *            {@link User}
	 * @return {@link User} obtained from Profiling server or null otherwise
	 */
	public User getUser(Resource user) {
		ServiceRequest req = new ServiceRequest(new ProfilingService(), null);
		req.addValueFilter(new String[] { ProfilingService.PROP_CONTROLS }, user);
		req.addRequiredOutput(OUTPUT_USER, new String[] { ProfilingService.PROP_CONTROLS });

		ServiceResponse resp = sc.call(req);
		if (resp.getCallStatus() == CallStatus.succeeded) {

			Object out = getReturnValue(resp.getOutputs(), OUTPUT_USER);
			if (out != null) {
				LogUtils.logDebug(mc, this.getClass(), "getUser",
						new Object[] { "User: ", user.getURI(), " retrieved from Profiling Server." }, null);
				return (User) out;
			} else {
				LogUtils.logDebug(mc, this.getClass(), "getUser",
						new Object[] { "User not found in Profiling Server." }, null);
				return null;
			}
		} else {
			LogUtils.logDebug(mc, this.getClass(), "getUser",
					new Object[] { "Call for obtaining user did not succeed." }, null);
			return null;
		}
	}

	/**
	 * @param user
	 *            {@link User}
	 * @return if the {@link User} was successfully added in Profiling Server
	 *         return true, false otherwise
	 */
	public boolean addUserSucceeded(User user) {
		ServiceRequest sr = new ServiceRequest(new ProfilingService(), null);
		sr.addAddEffect(new String[] { ProfilingService.PROP_CONTROLS }, user);

		ServiceResponse res = sc.call(sr);
		if (res.getCallStatus() == CallStatus.succeeded) {
			LogUtils.logDebug(mc, this.getClass(), "addUserSucceeded",
					new Object[] { "New user: ", user.getURI(), " added." }, null);
			return true;
		} else {
			LogUtils.logDebug(mc, this.getClass(), "addUserSucceeded",
					new Object[] { "Call for adding a user to Profiling server did not succeed." }, null);
			return false;
		}
	}

	/**
	 * @param user
	 *            {@link User}
	 * @return {@link UserProfile} for given {@link User} as String
	 */
	public String getProfileForUserAsString(User user) {
		ServiceRequest req = new ServiceRequest(new ProfilingService(), null);
		req.addValueFilter(new String[] { ProfilingService.PROP_CONTROLS }, user);
		req.addRequiredOutput(OUTPUT_GETPROFILE,
				new String[] { ProfilingService.PROP_CONTROLS, Profilable.PROP_HAS_PROFILE });

		ServiceResponse resp = sc.call(req);
		if (resp.getCallStatus() == CallStatus.succeeded) {
			Object out = getReturnValue(resp.getOutputs(), OUTPUT_GETPROFILE);
			if (out != null) {
				return out.toString();
			} else {
				return "nothing";
			}
		} else {
			return resp.getCallStatus().name();
		}
	}

	/**
	 * @param user
	 *            {@link User}
	 * @return {@link UserProfile} for given {@link User}
	 */
	public UserProfile getProfileForUser(User user) {
		ServiceRequest req = new ServiceRequest(new ProfilingService(), null);
		req.addValueFilter(new String[] { ProfilingService.PROP_CONTROLS }, user);
		req.addRequiredOutput(OUTPUT_GETPROFILE,
				new String[] { ProfilingService.PROP_CONTROLS, Profilable.PROP_HAS_PROFILE });

		ServiceResponse resp = sc.call(req);
		if (resp.getCallStatus() == CallStatus.succeeded) {
			try {
				List userProfileList = resp.getOutput(OUTPUT_GETPROFILE, true);

				if (userProfileList == null || userProfileList.size() == 0) {
					LogUtils.logInfo(mc, this.getClass(), "getProfileForUser",
							new Object[] { "There are no user profiles for user: " + user.getURI() }, null);
					return null;
				}
				// just return 1st
				UserProfile up = (UserProfile) userProfileList.get(0);
				return up;

			} catch (Exception e) {
				LogUtils.logError(mc, this.getClass(), "getProfileForUser",
						new Object[] { "got exception", e.getMessage() }, e);
				return null;
			}
		} else {
			LogUtils.logWarn(mc, this.getClass(), "getProfileForUser", new Object[] { "callstatus is not succeeded" },
					null);
			return null;
		}
	}

	/**
	 * @param user
	 *            {@link User}
	 * @return true if {@link UserProfile} for given {@link User} was obtained,
	 *         false otherwise
	 */
	public boolean getProfileForUserSucceeded(User user) {
		ServiceRequest req = new ServiceRequest(new ProfilingService(), null);
		req.addValueFilter(new String[] { ProfilingService.PROP_CONTROLS }, user);
		req.addRequiredOutput(OUTPUT_GETPROFILE,
				new String[] { ProfilingService.PROP_CONTROLS, Profilable.PROP_HAS_PROFILE });

		ServiceResponse resp = sc.call(req);
		if (resp.getCallStatus() == CallStatus.succeeded) {
			Object out = getReturnValue(resp.getOutputs(), OUTPUT_GETPROFILE);
			if (out != null) {
				LogUtils.logDebug(mc, this.getClass(), "getProfileForUserSucceeded",
						new Object[] { "UserProfile obtained for user " + user.getURI() }, null);
				return true;
			} else {
				return false;
			}
		} else {
			LogUtils.logDebug(mc, this.getClass(), "getProfileForUserSucceeded",
					new Object[] { "Call for UserProfile for user: " + user.getURI()
							+ " not succeeded or UserProfile for this user does not exist." },
					null);
			return false;
		}
	}

	/**
	 * @param user
	 *            {@link User}
	 * @param userProfile
	 *            {@link UserProfile}
	 * @return true if {@link UserProfile} for given {@link User} was added to
	 *         Profiling Server, false otherwise
	 */
	public boolean addUserProfileToUser(User user, UserProfile userProfile) {
		ServiceRequest sr = new ServiceRequest(new ProfilingService(), null);
		sr.addValueFilter(new String[] { ProfilingService.PROP_CONTROLS }, user);
		sr.addAddEffect(new String[] { ProfilingService.PROP_CONTROLS, Profilable.PROP_HAS_PROFILE }, userProfile);

		ServiceResponse res = sc.call(sr);
		if (res.getCallStatus() == CallStatus.succeeded) {
			LogUtils.logDebug(mc, this.getClass(), "addUserProfileToUser",
					new Object[] { "UserProfile: " + userProfile.getURI() + " for user ", user.getURI(), " added." },
					null);
			return true;
		} else {
			LogUtils.logDebug(mc, this.getClass(), "addUserProfileToUser",
					new Object[] { "call status: not succeeded" }, null);
			return false;
		}
	}

	/**
	 * 
	 * @param outputs
	 * @param expectedOutput
	 * @return value as an Object
	 */
	public static final Object getReturnValue(List outputs, String expectedOutput) {
		Object returnValue = null;
		if (!(outputs == null)) {
			for (Iterator i = outputs.iterator(); i.hasNext();) {
				ProcessOutput output = (ProcessOutput) i.next();
				if (output.getURI().equals(expectedOutput))
					if (returnValue == null)
						returnValue = output.getParameterValue();
			}
		}
		return returnValue;
	}

	public User[] getUsers() {
		ServiceRequest sr = new ServiceRequest(new ProfilingService(), null);
		sr.addTypeFilter(new String[] { ProfilingService.PROP_CONTROLS }, User.MY_URI);
		sr.addRequiredOutput(OUTPUT_USERS, new String[] { ProfilingService.PROP_CONTROLS });

		ServiceResponse res = sc.call(sr);
		if (res.getCallStatus() == CallStatus.succeeded) {
			try {
				List userList = res.getOutput(OUTPUT_USERS, true);

				if (userList == null || userList.size() == 0) {
					LogUtils.logInfo(mc, this.getClass(), "getUsers", new Object[] { "there are no users" }, null);
					return null;
				}

				User[] users = (User[]) userList.toArray(new User[userList.size()]);

				return users;

			} catch (Exception e) {
				LogUtils.logError(mc, this.getClass(), "getUsers", new Object[] { "Got exception", e.getMessage() }, e);
				return null;
			}
		} else {
			LogUtils.logWarn(mc, this.getClass(), "getUsers", new Object[] { "Callstatus is not succeeded" }, null);
			return null;
		}
	}

	public static void logUsers(User[] users) {
		if (users == null)
			return;

		String s = "\n------------ registered users: -----------\n";
		for (int i = 0; i < users.length; i++) {
			s += "User #" + i + "\n";
			s += users[i].toStringRecursive();
		}
		LogUtils.logDebug(mc, UIPreferencesSubprofilePrerequisitesHelper.class, "logUsers", new Object[] { s }, null);
	}
}
