/*
	Copyright 2014 Mario Pascucci <mpascucci@gmail.com>
	This file is part of JLDraw

	JLDraw is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	JLDraw is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with JLDraw.  If not, see <http://www.gnu.org/licenses/>.

*/


package jbrickconnedit;


import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import bricksnspace.ldraw3d.ProgressUpdater;

/**
 * @author mario
 *
 */
public class StatusIcon implements ProgressUpdater {
	
	private static final int MODE_BUSY = 0;
	private static final int ICON_BUSY = 0;
	private static final int ICON_READY = 1;
	//private int cycle = 0;
	private int mode;
	private ImageIcon[] icons;
	private JComponent widget;
	
	private StatusIcon(ImageIcon readyIcon, ImageIcon busyIcon) {
		
		icons = new ImageIcon[2];
		icons[ICON_BUSY] = busyIcon;
		icons[ICON_READY] = readyIcon;
		widget = new JLabel(readyIcon);
	}
	
	
	public static StatusIcon readyBusyIcon(ImageIcon readyIcon, ImageIcon busyIcon) {
		
		return new StatusIcon(readyIcon,busyIcon);
	}
	
	
	
	public JComponent getWidget() {
		return widget;
	}
	
	
	public void setBusy() {
		
		if (mode != MODE_BUSY) 
			return;
		if (widget instanceof JLabel)
			((JLabel)widget).setIcon(icons[ICON_BUSY]);
	}

	
	public void setReady() {
		
		if (mode != MODE_BUSY) 
			return;
		if (widget instanceof JLabel)
			((JLabel)widget).setIcon(icons[ICON_READY]);
	}

	
	
	public void setError() {
		
		if (widget instanceof JLabel)
			((JLabel)widget).setIcon(new ImageIcon(jbrickconn.class.getResource("images/error.png")));

	}
	
	

	@Override
	public void updateDone(int done, int total) {
		// ignored
	}


	@Override
	public void updateRemaining(int todo, int total) {
		// ignored
	}


	@Override
	public void updateDoing() {
		// ignored
	}

	
	@Override
	public void updateStart() {
		setBusy();
	}
	

	@Override
	public void updateComplete() {
		setReady();		
	}


	@Override
	public void updateIncomplete() {
		
		setError();
	}
	
}
