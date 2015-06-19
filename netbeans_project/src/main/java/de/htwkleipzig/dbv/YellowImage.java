package de.htwkleipzig.dbv;

import ij.*;
import ij.gui.GenericDialog;
import ij.process.*;
import ij.plugin.PlugIn;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/** This a prototype ImageJ plugin. */
public class YellowImage implements PlugIn {

	public void run(String arg) {
		long start = System.currentTimeMillis();
		int w = 400, h = 400;
                
                GenericDialog gd = new GenericDialog("New Image");
                gd.addNumericField("Width: ", w, 0);
                gd.addNumericField("Height: ", h, 0);
                gd.showDialog();
                if (gd.wasCanceled()) return;
                w = (int)gd.getNextNumber();
                h = (int)gd.getNextNumber();
                
		ImageProcessor ip = new ColorProcessor(w, h);
		int[] pixels = (int[])ip.getPixels();
		int i = 0;
		for (int y = 0; y < h; y++) {
			//int red = (y * 255) / (h - 1);
			for (int x = 0; x < w; x++) {
				//int blue = (x * 255) / (w - 1);
				pixels[i++] = (255 << 24) | (255 << 16) | (255 << 8);
			}
		}
		new ImagePlus("YellowImage", ip).show();
		IJ.showStatus(""+(System.currentTimeMillis()-start));
	 }

}

