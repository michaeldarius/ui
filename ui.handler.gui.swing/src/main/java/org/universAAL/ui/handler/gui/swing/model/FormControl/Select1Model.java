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
package org.universAAL.ui.handler.gui.swing.model.FormControl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeSelectionModel;

import org.universAAL.middleware.ui.rdf.ChoiceItem;
import org.universAAL.middleware.ui.rdf.FormControl;
import org.universAAL.middleware.ui.rdf.Label;
import org.universAAL.middleware.ui.rdf.Select;
import org.universAAL.middleware.ui.rdf.Select1;
import org.universAAL.ui.handler.gui.swing.Renderer;
import org.universAAL.ui.handler.gui.swing.model.FormControl.support.TaskQueue;

/**
 *
 * @author <a href="mailto:amedrano@lst.tfo.upm.es">amedrano</a>
 * @see Select1
 */
public abstract class Select1Model extends SelectModel implements ActionListener {

	/**
	 * constructor.
	 *
	 * @param control
	 *            the {@link FormControl} which this model represents.
	 */
	public Select1Model(Select1 control, Renderer render) {
		super(control, render);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return either a {@link JTree} or a {@link JComboBox}
	 */
	public JComponent getNewComponent() {
		if (!((Select) fc).isMultilevel()) {
			Label[] items = ((Select1) fc).getChoices();
			JComboBox cb = new JComboBox(items);
			cb.addActionListener(this);
			return cb;
		} else {
			JTree jt = new JTree(new SelectionTreeModel());
			return jt;
		}
	}

	/**
	 * Update the {@link JComponent}
	 */
	public void update() {
		// XXX add icons to component!
		if (!((Select) fc).isMultilevel()) {
			Label[] items = ((Select1) fc).getChoices();
			JComboBox cb = (JComboBox) jc;
			for (int i = 0; i < items.length; i++) {
				if (((ChoiceItem) items[i]).getValue().equals(fc.getValue())) {
					cb.setSelectedIndex(i);
				}
			}
			cb.setEditable(true);
		} else {
			JTree jt = (JTree) jc;
			jt.setEditable(false);
			jt.setSelectionModel(new SingleTreeSelectionModel());
		}
		super.update();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValid() {
		// TODO check validity.
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void actionPerformed(final ActionEvent e) {
		TaskQueue.addTask(new Runnable() {
			public void run() {
				if (!((Select) fc).isMultilevel()) {
					int i = ((JComboBox) e.getSource()).getSelectedIndex();
					((Select1) fc).storeUserInput(((ChoiceItem) ((Select1) fc).getChoices()[i]).getValue());
				}
			}
		});

	}

	/**
	 * The selection model for, selecting only one element in a tree.
	 *
	 * @author amedrano
	 *
	 */
	private class SingleTreeSelectionModel extends DefaultTreeSelectionModel {
		private static final long serialVersionUID = 1L;
		// TODO Model the selection! and gather Tree input
	}
}
