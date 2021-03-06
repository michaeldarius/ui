package org.universAAL.ui.gui.bluesteelLAF.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.universAAL.ui.gui.swing.bluesteelLAF.support.GradientLAF;
import org.universAAL.ui.gui.swing.bluesteelLAF.support.KickerButton;
import org.universAAL.ui.gui.swing.bluesteelLAF.support.SubmitButton;
import org.universAAL.ui.gui.swing.bluesteelLAF.support.SystemButton;
import org.universAAL.ui.gui.swing.bluesteelLAF.support.collapsable.SystemCollapse;
import org.universAAL.ui.gui.swing.bluesteelLAF.support.pager.MainMenuPager;
import org.universAAL.ui.handler.gui.swing.defaultLookAndFeel.Layout.BorderedScrolPaneLayout;
import org.universAAL.ui.handler.gui.swing.defaultLookAndFeel.Layout.VerticalFlowLayout;

public class CollapsableTest extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private GradientLAF contentPane;
	private SystemCollapse collapsable;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CollapsableTest frame = new CollapsableTest();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CollapsableTest() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new GradientLAF();
		// contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		collapsable = new SystemCollapse();
		collapsable.setHelpStrings("show", "hide");
		// collapsable = new JXCollapsiblePane();
		// collapsable.setScrollableTracksViewportHeight(false);
		// collapsable.setMinimumSize(new Dimension(0, 8));
		// collapsable.setLayout(new FlowLayout());
		// collapsable.setBackground(Color.RED);
		((SystemCollapse) collapsable).setBackgroundColor(Color.red);

		contentPane.add(collapsable, BorderLayout.SOUTH);

		JButton btnHome = // new JButton("Home");
				new SystemButton("Home", null);
		btnHome.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				contentPane.fadeIn();

			}
		});
		collapsable.add(btnHome);

		JButton btnPendingMessages =
				// new JButton("Pending Messages");
				new SystemButton("Pending Messages", null);
		collapsable.add(btnPendingMessages);

		JButton btnPendingDialogs = // new JButton("Exit");
				new SystemButton("Exit", null);
		btnPendingDialogs.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				contentPane.fadeOut();
			}
		});
		collapsable.add(btnPendingDialogs);

		MainMenuPager panel_1 = new MainMenuPager();
		panel_1.setHelpStrings("next", "previous", "jump to page {0}", "{0}/{1}");
		contentPane.add(panel_1, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		VerticalFlowLayout vfl = new VerticalFlowLayout(VerticalFlowLayout.TOP, 10, 10);
		vfl.setMaximizeOtherDimension(true);
		panel.setLayout(vfl);
		JScrollPane sp = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setLayout(new BorderedScrolPaneLayout());
		contentPane.add(sp, BorderLayout.EAST);

		for (int i = 0; i < 10; i++) {
			String l = "";
			for (int j = 0; j < i; j++) {
				l += Integer.toString(j);
			}
			JButton jb
			// = new JButton("Submit " + l);
					= new SubmitButton("Submit " + l, null);
			panel.add(jb);
		}

		for (int i = 0; i < 25; i++) {
			panel_1.add(
					// new JButton("Service " + Integer.toString(i))
					new KickerButton("Service " + Integer.toString(i), null));
		}
	}

}
