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
package org.universAAL.ui.gui.swing.bluesteelLAF;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.universAAL.middleware.ui.rdf.Select;
import org.universAAL.middleware.ui.rdf.Select1;
import org.universAAL.ui.handler.gui.swing.Renderer;
import org.universAAL.ui.handler.gui.swing.model.FormControl.Select1Model;

/**
 * @author pabril
 *
 */
public class Select1LAF extends Select1Model {

	private Select1Model wrap;

	/**
	 * Constructor.
	 *
	 * @param control
	 *            the {@link Select1} which to model.
	 */
	public Select1LAF(Select1 control, Renderer render) {
		super(control, render);
	}

	@Override
	public void updateAsMissing() {
		JLabel l;
		if (wrap != null) {
			l = wrap.getLabelModel().getComponent();
		} else {
			l = getLabelModel().getComponent();
		}
		l.setForeground(Init.getInstance(getRenderer()).getColorLAF().getAlert());
		l.setText(getAlertString());
	}

	public JComponent getNewComponent() {
		if (!((Select) fc).isMultilevel() && ((Select) fc).getChoices().length <= 6) {
			wrap = new Select1RadioButtonLAF((Select1) fc, getRenderer());
			return wrap.getComponent();
		} else {
			return super.getNewComponent();
		}
	}

	/** {@inheritDoc} */
	public void update() {
		if (wrap != null) {
			wrap.update();
			needsLabel = wrap.needsLabel();
		} else {
			super.update();
		}
	}

}
