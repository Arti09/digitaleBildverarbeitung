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

public class Chess implements PlugIn {
      
	public void run(String arg) {
		int width = 10;
                int height = 10;
                GenericDialog gd = new GenericDialog("New Image");
                gd.addNumericField("Width: ", width, 0);
                gd.addNumericField("Height: ", height, 0);
                gd.showDialog();
                if (gd.wasCanceled()) return;
                width = (int)gd.getNextNumber();
                height = (int)gd.getNextNumber();
            
                long start = System.currentTimeMillis();
		int w = 400, h = 400;
		ImageProcessor ip = new ColorProcessor(w, h);
		int[] pixels = (int[])ip.getPixels();
                int i=0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
                                if (y % (height*2) >= height) {
                                    if (x % (width*2) >= width) {
                                        pixels[i++] = (255 << 24);
                                    } else {
                                        pixels[i++] = (255 << 24) | (255 << 16) | (255 << 8) | (255);
                                    }
                                } else {
                                    if (x % (width*2) >= width) {
                                        pixels[i++] = (255 << 24) | (255 << 16) | (255 << 8) | (255);
                                    } else {
                                        pixels[i++] = (255 << 24);
                                    }
                                }
			}
		}
                for (int pixel = 0; pixel < pixels.length/2; pixel++) {
                    int temp = pixels[pixel];
                    pixels[pixel] = pixels[pixels.length-pixel-1];
                    pixels[pixels.length-pixel-1] = temp;
                }
		new ImagePlus("ChessImage", ip).show();
		IJ.showStatus(""+(System.currentTimeMillis()-start));
	 }
}
