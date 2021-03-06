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
package org.universAAL.ui.gui.swing.bluesteelLAF.support;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Icon;

import org.universAAL.ui.gui.swing.bluesteelLAF.ColorLAF;

public class SubmitButton extends RoundedGradientButton {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	// border color definition
	private static final Color LIGHT = new Color(204, 204, 204);
	private static final Color DARK = new Color(173, 173, 173);
	private static final Color BG = new Color(173, 173, 173);
	private static final Color textSubmit = new Color(8, 68, 92);

	public SubmitButton(String text, Icon icon) {
		super(text, LIGHT, BG);
		if (icon != null) {
			setIcon(icon);
			Dimension d = getPreferredSize();
			int square = Math.min(d.width, d.height) - 6;
			scaleIcon(square, square);
		}
		bLight = LIGHT;
		bDark = DARK;
		setBackground(BG);
		setForeground(textSubmit);
	}

	@Override
	public Dimension getPreferredSize() {
		// set preferred size according to text
		Dimension sPref = super.getPreferredSize();
		int buttonWidth = 3 * ColorLAF.SEPARATOR_SPACE;
		int buttonHeight = 1 * ColorLAF.SEPARATOR_SPACE;
		return new Dimension(Math.max(buttonWidth, sPref.width), buttonHeight);
	}
}
