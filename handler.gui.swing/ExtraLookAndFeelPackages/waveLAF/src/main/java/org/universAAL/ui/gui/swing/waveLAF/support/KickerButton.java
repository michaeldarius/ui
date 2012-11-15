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
package org.universAAL.ui.gui.swing.waveLAF.support;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.SoftBevelBorder;

import org.universAAL.ui.gui.swing.waveLAF.ColorLAF;

public class KickerButton extends JButton implements MouseListener, ComponentListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color ligth = new Color (56,142,143);
	private Color dark	= new Color (75,183,185);
	private Color white= new Color (255,255,255);
	private Icon icon;
	private Integer lastRun = new Integer(0);
	
    public KickerButton(String text, Icon icon) {
        super(text);
        this.icon = icon;
        SoftBevelBorder raisedBorder = new SoftBevelBorder(SoftBevelBorder.RAISED,  ligth, dark);
      
        setBorder(raisedBorder);
        setBackground(new Color(55, 142, 143));
        setForeground(white);
        setFont(ColorLAF.getbold());
        setOpaque(true);
        addMouseListener(this);
        addComponentListener(this);
        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setUI(ui);       
        
    }
    
    private void resetIcon() {
        if (icon != null){ 
        	Dimension d = getSize();
        	int square = Math.min(d.width, d.height - ColorLAF.SEPARATOR_SPACE );
    	    Image img = ((ImageIcon) icon).getImage();  
    	    Image newimg = img.getScaledInstance( square, square,  java.awt.Image.SCALE_SMOOTH );  
    	    setIcon(new ImageIcon( newimg ));
    	}
    }
    
    protected void paintComponent(Graphics g) {
    	   
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        //ButtonModel m = getModel();

        Paint oldPaint = g2.getPaint();
      
        RoundRectangle2D.Float r2d = new RoundRectangle2D.Float(0,0,getWidth(),getHeight()-1,17,17);
            g2.clip(r2d);
            g2.setPaint(new GradientPaint(0.0f, 0.0f, ligth,
                    0.0f, getHeight(), dark));
            g2.fillRect(0,0,getWidth(),getHeight());

            g2.setStroke(new BasicStroke(4f));
            g2.setPaint(new GradientPaint(0.0f, 0.0f, ligth,
                    0.0f, getHeight(), dark));
            g2.drawRoundRect(0, 0, getWidth()-1 , getHeight() -1, 15, 15);

        g2.setPaint(oldPaint);
        super.paintComponent(g);
    }
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void mouseEntered(MouseEvent e) {
		setForeground(white);
		setBackground(ligth);
		setOpaque(true);
		
	}
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void componentResized(ComponentEvent e) {
//		if (System.currentTimeMillis() - lastResize  > 100) {
//			
//			lastResize = System.currentTimeMillis();
//		}		
		new Thread( new ResizeTask()).start();
	}
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	class ResizeTask implements Runnable {
	
		Integer myID;
		
		ResizeTask(){
			synchronized (lastRun) {
				myID = ++lastRun;
			}
		}
		
		public void run() {
			try {
				Thread.sleep(GradientLAF.MS_ANIMATION + 10);
			} catch (InterruptedException e) {	}
			if (myID == lastRun) {
				KickerButton.this.resetIcon();
				lastRun = 0;
			}
		}
	}
    
}
