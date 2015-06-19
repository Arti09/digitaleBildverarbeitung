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

public class CirclesDecreasingWidth implements PlugIn {
      
        @Override
	public void run(String arg) {
		long start = System.currentTimeMillis();
		int w = 400, h = 400;
                int maxwidth = 10;
                
                GenericDialog gd = new GenericDialog("New Image");
                gd.addNumericField("Width: ", maxwidth, 0);
                gd.showDialog();
                if (gd.wasCanceled()) return;
                maxwidth = (int)gd.getNextNumber();
                
		ImageProcessor ip = new ColorProcessor(w, h);
                int l = 0,k = 1;
                for (int r = w*2; r >= 0; r--) {
                    if (k % 2 == 0) {
                        ip.setColor(((255<<24)|(255<<16)|(255<<8)|255));
                        ip.drawOval((h-r)/2-h/2, (w-r)/2-w/2, r, r);
                    } else {
                        ip.setColor((255<<24));
                        ip.drawOval((h-r)/2-h/2,(w-r)/2-w/2, r, r);
                    }
                    if (k == l) {
                        k++;
                        l = 0;
                    } else {
                        l++;
                    }
                }
		new ImagePlus("CirclesImage", ip).show();
		IJ.showStatus(""+(System.currentTimeMillis()-start));
	 }
}
