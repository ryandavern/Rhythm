/**
 *  Disclaimer
 *  This project was created by Ryan Davern.
 *  Start Date: 30/03/2016.
 *  
 *  Copyright (C) 2017 Ryan Davern - All Rights Reserved.
 *  You may not use, distribute, monetize or modify this code under the terms of the Copyright Act 1994.
 *  You may use the compiled program, which can be downloaded at https://www.beatplaylist.com/. Any modified versions or versions uploaded to a different website is against TOS (https://www.beatplaylist.com/terms).
 *  
 *  For more information on the Copyright Act 1994, please visit http://www.legislation.govt.nz/act/public/1994/0143/latest/DLM345634.html.
 */

package com.beatplaylist;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class Test2 {
	public static void main(final String args[]) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(300, 300);
		f.setLocationRelativeTo(null);

		f.setUndecorated(true);
		f.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);

		JPanel panel = new JPanel();
		panel.setBackground(java.awt.Color.white);
		f.setContentPane(panel);

		MetalLookAndFeel.setCurrentTheme(new MyDefaultMetalTheme());
		try {
			UIManager.setLookAndFeel(new MetalLookAndFeel());
		} catch (Exception e) {
			e.printStackTrace();
		}

		SwingUtilities.updateComponentTreeUI(f);

		f.setVisible(true);
	}
}

class MyDefaultMetalTheme extends DefaultMetalTheme {
	public ColorUIResource getWindowTitleInactiveBackground() {
		return new ColorUIResource(java.awt.Color.GREEN);
	}

	public ColorUIResource getWindowTitleBackground() {
		return new ColorUIResource(java.awt.Color.GREEN);
	}

	public ColorUIResource getPrimaryControlHighlight() {
		return new ColorUIResource(java.awt.Color.GREEN);
	}

	public ColorUIResource getPrimaryControlDarkShadow() {
		return new ColorUIResource(java.awt.Color.GREEN);
	}

	public ColorUIResource getPrimaryControl() {
		return new ColorUIResource(java.awt.Color.GREEN);
	}

	public ColorUIResource getControlHighlight() {
		return new ColorUIResource(java.awt.Color.GREEN);
	}

	public ColorUIResource getControlDarkShadow() {
		return new ColorUIResource(java.awt.Color.GREEN);
	}

	public ColorUIResource getControl() {
		return new ColorUIResource(java.awt.Color.GREEN);
	}
}