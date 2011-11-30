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
package org.universAAL.ui.full.test.forms;

import java.io.IOException;
import java.util.Locale;

import org.universAAL.middleware.input.InputEvent;
import org.universAAL.middleware.io.owl.PrivacyLevel;
import org.universAAL.middleware.io.rdf.Form;
import org.universAAL.middleware.io.rdf.Label;
import org.universAAL.middleware.io.rdf.SubdialogTrigger;
import org.universAAL.middleware.output.OutputEvent;
import org.universAAL.middleware.owl.supply.LevelRating;
import org.universAAL.ui.full.test.AbstractForm;
import org.universAAL.ui.full.test.ISubscriber;
import org.universAAL.ui.full.test.osgi.Activator;

/**
 * @author amedrano
 *
 */
public class A1Main implements AbstractForm {

	/* (non-Javadoc)
	 * @see org.universAAL.ui.full.test.InputListener#getDialog()
	 */
	public Form getDialog() {
		Form f = Form.newDialog("universAAL Handler Certification Test", (String) null);
		addSubDialogTriggers(f);
		//listenTo(f.getDialogID());
		//TODO add text in IOControls
		return f;
	}

	/* (non-Javadoc)
	 * @see org.universAAL.ui.full.test.InputListener#handleEvent(org.universAAL.middleware.input.InputEvent)
	 */
	public void handleEvent(InputEvent ie) {
		//super.handleEvent(ie);
		String[] cNames=null;
		try {
			 cNames = ISubscriber.getClassNamesFromPackage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (cNames!= null) {
			int i = 0;
			while (i < cNames.length
					&& !cNames[i].equals(ie.getSubmissionID()))
					i++;
			if (i!=cNames.length) {
				/*
				 *  a submit has been pressed
				 *  Generate and publish an output event
				 */
				OutputEvent e = new OutputEvent(ie.getUser(),
						ISubscriber.getAbstractForm(cNames[i]).getDialog(),
						LevelRating.low,
						Locale.ENGLISH,
						PrivacyLevel.insensible);
				Activator.noutput.publish(e);
			}
		}
	}

	public String getSubDialogTriggerDisplay() {
		return "Main";
	}
	
	static public void addSubDialogTriggers(Form f) {
		String[] cNames=null;
		try {
			 cNames = ISubscriber.getClassNamesFromPackage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (cNames!= null) {
			for (int i = 0; i<cNames.length;i++) {
				addSubDialogTrigger(f, cNames[i]);
			}
		}
	}

	static private void addSubDialogTrigger(Form f,  String cName) {
			new SubdialogTrigger(f.getSubmits(), 
				new Label(ISubscriber.getAbstractForm(cName)
						.getSubDialogTriggerDisplay(), null),
				cName);		
	}


}

