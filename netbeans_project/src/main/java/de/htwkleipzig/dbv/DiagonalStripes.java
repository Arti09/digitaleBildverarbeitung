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

public class DiagonalStripes implements PlugIn {
    
	public void run(String arg) {
		long start = System.currentTimeMillis();
		int w = 400, h = 400;
                int width = 10;
                
                GenericDialog gd = new GenericDialog("New Image");
                gd.addNumericField("Width: ", width, 0);
                gd.showDialog();
                if (gd.wasCanceled()) return;
                width = (int)gd.getNextNumber();
                
		ImageProcessor ip = new ColorProcessor(w, h);
		int[] pixels = (int[])ip.getPixels();
		int i = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
                                if ((x+y) % (width * 2) >= width) {
                                    pixels[i++] = (255 << 24);
                                } else {
                                    pixels[i++] = (255 << 24) | (255 << 16) | (255 << 8) | (255 << 0);
                                }
			}
		}
		new ImagePlus("DiagonalImage", ip).show();
		IJ.showStatus(""+(System.currentTimeMillis()-start));
	 }
}
