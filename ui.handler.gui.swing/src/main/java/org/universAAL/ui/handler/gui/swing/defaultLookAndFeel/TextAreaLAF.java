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
package org.universAAL.ui.handler.gui.swing.defaultLookAndFeel;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.universAAL.middleware.ui.rdf.TextArea;
import org.universAAL.ui.handler.gui.swing.Renderer;
import org.universAAL.ui.handler.gui.swing.model.FormControl.TextAreaModel;

/**
 * @author pabril
 *
 */
public class TextAreaLAF extends TextAreaModel {

	/**
	 * {@link JScrollPane} around the {@link JTextArea};
	 */
	JScrollPane sp;

	/**
	 * Enveloped {@link JComponent}
	 */
	JComponent ejc;

	private ColorLAF color;

	/**
	 * Constructor.
	 *
	 * @param control
	 *            the {@link TextArea} which to model.
	 */
	public TextAreaLAF(TextArea control, Renderer render) {
		super(control, render);
		color = ((Init) render.getInitLAF()).getColorLAF();
	}

	/** {@inheritDoc} */
	public void update() {
		jc = (JComponent) (jc == sp ? ejc : jc);
		super.update();
		String initialValue = (String) fc.getValue();
		JTextArea ta = (JTextArea) jc;
		ta.setRows(10);
		ta.setColumns(15);
		ta.getAccessibleContext().setAccessibleName(initialValue);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		sp.setFocusable(true);
		sp.getAccessibleContext().setAccessibleName(initialValue);
		ta.setFont(color.getplain());
		ta.setForeground(color.getfont());
	}

	/** {@inheritDoc} */
	public JComponent getNewComponent() {
		ejc = super.getNewComponent();
		sp = new JScrollPane(ejc);
		return sp;
	}

	/** {@inheritDoc} */
	public void updateAsMissing() {
		JLabel l = getLabelModel().getComponent();
		l.setForeground(color.getAlert());
	}

}
