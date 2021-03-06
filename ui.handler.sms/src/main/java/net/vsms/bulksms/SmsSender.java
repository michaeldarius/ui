/*******************************************************************************
 * Copyright 2012 Ericsson Nikola Tesla d.d.
 * Copyright 2013 Universidad Politécnica de Madrid
 * Copyright 2013 Fraunhofer-Gesellschaft - Institute for Computer Graphics Research
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
package net.vsms.bulksms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Properties;

import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.ui.handler.sms.ISMSSender;
import org.universAAL.ui.handler.sms.SMSActivator;

/**
 * Responsible for sending given sms message via designated server to desired
 * number.
 *
 * @author eandgrg
 *
 */
public final class SmsSender implements ISMSSender {

	private File confHome;
	private String username;
	private String password;
	private String server;
	private boolean doSend;

	/**
	 * using singleton pattern
	 */
	public SmsSender() {
		confHome = SMSActivator.getModuleContext().getConfigHome();
		Properties prop = new Properties();
		try {
			InputStream in = new FileInputStream(new File(confHome, "account.properties"));

			prop.load(in);
		} catch (IOException e) {
			LogUtils.logWarn(SMSActivator.getModuleContext(), getClass(), "constructor",
					new Object[] { "Configuration file for sms handler not found > using default values " }, null);
		}
		username = prop.getProperty("user", "uAAL");
		password = prop.getProperty("pass", "universaal");
		server = prop.getProperty("server", "http://bulksms.vsms.net:5567/eapi/submission/send_sms/2/2.0");
		doSend = Boolean.parseBoolean(prop.getProperty("send.enable", "true"));
	}

	/**
	 * Unicode support (so that e.g. arabic and chinese messages can be
	 * correctly represented)
	 *
	 * @param s
	 *            text
	 * @return unicode representation of a message
	 */
	public static String stringToHex(final String s) {
		char[] chars = s.toCharArray();
		String next;
		StringBuffer output = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			next = Integer.toHexString((int) chars[i]);
			// Unfortunately, toHexString doesn't pad with zeroes, so:
			for (int j = 0; j < (4 - next.length()); j++) {
				output.append("0");
			}
			output.append(next);
		}
		return output.toString();
	}

	/**
	 * Helper method to decipher bulksms server's response on send sms.
	 *
	 * @param result
	 *            string containing status from bulksms server (e.g.
	 *            25|FAILED_USERCREDITS|342762992)
	 * @return bulksms server status on send sms
	 */
	protected String getBulkSMSsendStatus(final String result) {
		StringBuffer status = new StringBuffer();
		if (result.contains("IN_PROGRESS")) {
			status.append("Sent to server...");
		}
		if (result.contains("11")) {
			status.append("Delivered to mobile");
		}
		if (result.contains("22")) {
			status.append("Internal fatal error");
		}
		if (result.contains("23")) {
			status.append("Authentication failure");
		}
		if (result.contains("24")) {
			status.append("Data validation failed");
		}
		if (result.contains("25")) {
			status.append("You do not have sufficient credits");
		}
		if (result.contains("26")) {
			status.append("Upstream credits not available");
		}
		if (result.contains("27")) {
			status.append("You have exceeded your daily quota");
		}
		if (result.contains("28")) {
			status.append("Upstream quota exceeded");
		}
		if (result.contains("29")) {
			status.append("Message sending cancelled");
		}
		if (result.contains("31")) {
			status.append("Unroutable");
		}
		if (result.contains("32")) {
			status.append("Blocked (probably because of a recipient's complaint against you)");
		}
		if (result.contains("33")) {
			status.append("Failed: censored");
		}
		if (result.contains("50")) {
			status.append("Delivery failed - generic failure");
		}
		if (result.contains("51")) {
			status.append("Delivery to phone failed");
		}
		if (result.contains("52")) {
			status.append("Delivery to network failed");
		}
		if (result.contains("53")) {
			status.append("Message expired");
		}
		if (result.contains("54")) {
			status.append("Failed on remote network");
		}
		if (result.contains("56")) {
			status.append("Failed: remotely censored");
		}
		if (result.contains("57")) {
			status.append("Failed due to fault on handset (e.g. SIM full)");
		}
		if (result.contains("64")) {
			status.append("Queued for retry after temporary failure delivering, due to fault on handset (transient)");
		}
		if (result.contains("70")) {
			status.append("Unknown upstream status");
		}
		return status.toString();
	}

	/** {@ inheritDoc} */
	public void sendMessage(final String number, final String text) {
		try {
			// Construct data
			String data = "";
			data += "username=" + URLEncoder.encode(username, "ISO-8859-1");
			data += "&password=" + URLEncoder.encode(password, "ISO-8859-1");
			data += "&message=" + stringToHex(text);
			data += "&dca=16bit";
			data += "&want_report=1";
			data += "&msisdn=" + number;

			// example request:
			// http://bulksms.vsms.net:5567/eapi/submission/send_sms/2/2.0?username=universAAL&password=
			// universaal&message=test sms handler message&msisdn=385913653092
			LogUtils.logInfo(SMSActivator.getModuleContext(), getClass(), "sendMessage()",
					new Object[] { "Call url to send sms (message in hex): " + server + "?" + data }, null);

			// Send data
			if (doSend) {

				URL url = new URL(server);
				URLConnection conn = url.openConnection();
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "ISO-8859-1");
				wr.write(data);
				wr.flush();

				// Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "ISO-8859-1"));
				String line;
				StringBuffer result = new StringBuffer("");

				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				LogUtils.logInfo(SMSActivator.getModuleContext(), getClass(), "sendMessage()",
						new Object[] { "Result from server:" + result }, null);

				LogUtils.logInfo(SMSActivator.getModuleContext(), getClass(), "sendMessage()",
						new Object[] { "Result status from server:" + getBulkSMSsendStatus(new String(result)) }, null);
				wr.close();
				rd.close();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
