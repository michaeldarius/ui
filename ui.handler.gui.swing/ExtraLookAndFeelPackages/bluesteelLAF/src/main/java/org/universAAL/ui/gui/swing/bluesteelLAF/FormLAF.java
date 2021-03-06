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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.universAAL.middleware.ui.UIRequest;
import org.universAAL.middleware.ui.rdf.Form;
import org.universAAL.ui.gui.swing.bluesteelLAF.support.GradientLAF;
import org.universAAL.ui.handler.gui.swing.Renderer;
import org.universAAL.ui.handler.gui.swing.defaultLookAndFeel.Layout.BorderedScrolPaneLayout;
import org.universAAL.ui.handler.gui.swing.model.FormModel;

/**
 * The Look and Feel for Forms
 *
 * @author <a href="mailto:amedrano@lst.tfo.upm.es">amedrano</a>
 * @author pabril
 * @see FormModel
 */
@SuppressWarnings("unused")
public class FormLAF extends FormModel implements ComponentListener {
	private static final Color GRAY = new Color(0, 51, 255);
	/**
	 * internal accounting for the frame being displayed.
	 */
	private GradientLAF frame = null;
	private JInternalFrame internalFrame;

	/**
	 * Constructor.
	 *
	 * @param f
	 *            {@link Form} which to model.
	 */
	public FormLAF(Form f, Renderer render) {
		super(f, render);
	}

	/**
	 * get the io panel wrapped in a scroll pane.
	 *
	 * @return the {@link FormModel#getIOPanel} wrapped in a {@link JScrollPane}
	 *         .
	 */
	protected JComponent getIOPanelScroll() {
		JComponent ioPanel = super.getIOPanel();
		ioPanel.setOpaque(false);
		if (!(ioPanel instanceof JPanel)) {
			return ioPanel;
		}
		JScrollPane sp = new JScrollPane(ioPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.getVerticalScrollBar().setPreferredSize(new Dimension(50, 50));
		sp.getHorizontalScrollBar().setPreferredSize(new Dimension(50, 50));
		sp.setOpaque(false);
		// sp.setBorder(null);
		sp.getViewport().setOpaque(false);
		return sp;
	}

	/**
	 * get the submit panel wrapped in a scroll pane.
	 *
	 * @return the {@link FormModel#getSubmitPanel} wrapped in a
	 *         {@link JScrollPane}.
	 */
	protected JScrollPane getSubmitPanelScroll(int depth) {
		JComponent submit = super.getSubmitPanel(depth);
		submit.setOpaque(false);
		// submit.setLayout(new FormLayout());
		JScrollPane sp = new JScrollPane(submit, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setLayout(new BorderedScrolPaneLayout());
		sp.getVerticalScrollBar().setPreferredSize(new Dimension(50, 50));
		sp.setOpaque(false);
		sp.setBorder(null);
		sp.getViewport().setOpaque(false);
		return sp;
	}

	/**
	 * get the submit panel Horizontally displayed wrapped in a scroll pane.
	 *
	 * @return the {@link FormModel#getSubmitPanel} wrapped in a
	 *         {@link JScrollPane}.
	 */
	protected JScrollPane getSubmitHorizontalPanelScroll(int depth) {
		JComponent submit = super.getSubmitPanel(depth);
		submit.setLayout(new FlowLayout());
		submit.setOpaque(false);
		// submit.setLayout(new FormLayout());
		JScrollPane sp = new JScrollPane(submit, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setLayout(new ScrollPaneLayout());
		sp.setOpaque(false);
		sp.setBorder(null);
		sp.getViewport().setOpaque(false);
		return sp;
	}

	/**
	 * generate the header panel.
	 *
	 * @return a pannel with universAAL icon in it.
	 */
	protected JPanel getHeader() {
		JPanel header = new JPanel();// new GradientLAF();
		header.setOpaque(false);
		JLabel route = new JLabel();
		route.setFont(Init.getInstance(getRenderer()).getColorLAF().getbold());
		String[] path = getTitlePath();
		StringBuffer r = new StringBuffer(path[0]);
		for (int i = 1; i < path.length; i++) {
			r.append(" / " + path[i]);
		}
		route.setText(r.toString());
		header.add(route);
		return (JPanel) header;
	}

	/**
	 * render the frame for the {@link Form}.
	 */
	public void showForm() {
		UIRequest rq = getRequest();
		if (rq != null) {
			((Init) getRenderer().getInitLAF()).processPrefs(rq);
		}
		JDesktopPane desktopPane = Init.getInstance(getRenderer()).getDesktop();
		synchronized (desktopPane) {
			if (frame == null) {
				frame = new GradientLAF();
				frame.setLayout(new BorderLayout());
				frame.setBorder(createFrameBorder());
				frame.getAccessibleContext().setAccessibleName(form.getTitle());
			}

			/*
			 * MESSAGES
			 */
			if (form.isMessage()) {
				JComponent io = getIOPanelScroll();
				io.getAccessibleContext().setAccessibleName(IO_NAME);
				// JScrollPane sub = new JScrollPane(super.getSubmitPanel());
				JScrollPane sub = getSubmitHorizontalPanelScroll(0);
				frame.add(io, BorderLayout.CENTER);
				frame.add(sub, BorderLayout.SOUTH);
				packAsInternalFrame();
			}

			/*
			 * MAIN MENU
			 */
			if (form.isSystemMenu()) {
				// frame.add(getHeader(), BorderLayout.NORTH);
				frame.add(getIOPanel(), BorderLayout.CENTER);
				frame.add(getSystemPanel(), BorderLayout.SOUTH);
			}

			/*
			 * DIALOG
			 */
			if (form.isStandardDialog()) {
				frame.add(getHeader(), BorderLayout.NORTH);
				JComponent io = getIOPanelScroll();
				io.getAccessibleContext().setAccessibleName(IO_NAME);
				JScrollPane sub = getSubmitPanelScroll(0);
				sub.getAccessibleContext().setAccessibleName(SUB_NAME);
				JComponent sys = getSystemPanel();
				sys.getAccessibleContext().setAccessibleName(SYS_NAME);
				frame.add(io, BorderLayout.CENTER);
				frame.add(sub, BorderLayout.EAST);
				frame.add(sys, BorderLayout.SOUTH);
			}

			/*
			 * SUBDIALOG
			 */
			if (form.isSubdialog()) {
				// subdialog in a JInternalFrame (it can overlay another frame
				// in the desktop)
				JScrollPane sub = getSubmitHorizontalPanelScroll(0);
				sub.getAccessibleContext().setAccessibleName(SUB_NAME);
				JPanel subpanel = new JPanel(new BorderLayout());
				subpanel.add(getIOPanelScroll(), BorderLayout.CENTER);
				// subpanel.setBorder(new ColorBorder(GRAY, 0, 12, 0, 12));
				// for (int i = super.getSubdialogLevel(); i > 1; i--) {
				// subpanel.add(getSubmitPanel(i), BorderLayout.EAST);
				// JPanel tempanel = new JPanel(new BorderLayout());
				// tempanel.add(subpanel, BorderLayout.CENTER);
				// subpanel = tempanel;
				// }
				frame.add(subpanel, BorderLayout.CENTER);
				frame.add(sub, BorderLayout.SOUTH);
				packAsInternalFrame();
			}

			if (internalFrame == null) {
				// add to the Desktop.
				desktopPane.add(frame);
				// ALL non-internal frames are maximized
				setFullScreen();
			} else {
				// internalFrames aren't resizable
				internalFrame.setResizable(false);
				internalFrame.pack();
				// add to the Desktop.

				desktopPane.add(internalFrame);
				// center in Desktop.
				Dimension desktopSize = desktopPane.getSize();
				Dimension jInternalFrameSize = internalFrame.getSize();
				internalFrame.setLocation((desktopSize.width - jInternalFrameSize.width) / 2,
						(desktopSize.height - jInternalFrameSize.height) / 2);
				internalFrame.setVisible(true);
			}

			frame.fadeIn();
			desktopPane.revalidate();
			desktopPane.repaint();
			desktopPane.getParent().addComponentListener(this);
		}
	}

	private void packAsInternalFrame() {
		internalFrame = new JInternalFrame(form.getTitle());
		internalFrame.setContentPane(frame);
	}

	private void setFullScreen() {
		frame.setSize(Init.getInstance(getRenderer()).getDesktop().getSize());
	}

	/** {@inheritDoc} */
	public void terminateDialog() {
		JDesktopPane desktopPane = Init.getInstance(getRenderer()).getDesktop();
		synchronized (desktopPane) {
			if (internalFrame != null) {
				frame.fadeOut();
				internalFrame.removeAll();
				desktopPane.remove(internalFrame);
				internalFrame.dispose();
				internalFrame = null;
			} else if (frame != null) {
				frame.fadeOut();
				frame.removeAll();
				// frame.dispose();
				desktopPane.remove(frame);
				desktopPane.removeAll();
			}
			desktopPane.revalidate();
			desktopPane.repaint();
			frame = null;
			// frame.getContentPane().removeAll();
		}
		desktopPane.getParent().removeComponentListener(this);
	}

	private Border createFrameBorder() {
		Border raisedbevel = BorderFactory.createRaisedBevelBorder();
		Border loweredbevel = BorderFactory.createLoweredBevelBorder();
		return BorderFactory.createCompoundBorder(raisedbevel, loweredbevel);
	}

	/** {@ inheritDoc} */
	public void componentResized(ComponentEvent e) {
		Component parent = SwingUtilities.getWindowAncestor(frame);
		if (internalFrame == null) {
			frame.setSize(frame.getParent().getSize());
			frame.revalidate();
		}
		if (((Init) getRenderer().getInitLAF()).isWindowed() && parent != null) {
			Rectangle r = parent.getBounds();
			getRenderer().setProperty(Init.WINDOWED_X, Integer.toString(r.x));
			getRenderer().setProperty(Init.WINDOWED_Y, Integer.toString(r.y));
			getRenderer().setProperty(Init.WINDOWED_WIDTH, Integer.toString(r.width));
			getRenderer().setProperty(Init.WINDOWED_HEIGHT, Integer.toString(r.height));
		}
	}

	/** {@ inheritDoc} */
	public void componentMoved(ComponentEvent e) {
	}

	/** {@ inheritDoc} */
	public void componentShown(ComponentEvent e) {
	}

	/** {@ inheritDoc} */
	public void componentHidden(ComponentEvent e) {
	}
}
