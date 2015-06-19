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

public class Circles implements PlugIn {
      
	public void run(String arg) {
		long start = System.currentTimeMillis();
		int w = 400, h = 400;
		ImageProcessor ip = new ColorProcessor(w, h);
                int width = 10;
                
                GenericDialog gd = new GenericDialog("New Image");
                gd.addNumericField("Width: ", width, 0);
                gd.showDialog();
                if (gd.wasCanceled()) return;
                width = (int)gd.getNextNumber();
                
                for (int r = 0; r < w*2; r++) {
                    if (r % (width * 2) >= width) {
                        ip.setColor(((255<<24)|(255<<16)|(255<<8)|255));
                        ip.drawOval((h-r)/2, (w-r)/2, r, r);
                    } else {
                        ip.setColor((255<<24));
                        ip.drawOval((h-r)/2,(w-r)/2, r, r);
                    }
                }
		new ImagePlus("CirclesImage", ip).show();
		IJ.showStatus(""+(System.currentTimeMillis()-start));
	 }
}
