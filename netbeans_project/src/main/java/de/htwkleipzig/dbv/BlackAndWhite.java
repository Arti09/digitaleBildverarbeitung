package de.htwkleipzig.dbv;

import ij.*;
import ij.process.*;
import ij.plugin.PlugIn;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class BlackAndWhite implements PlugIn {
      
	public void run(String arg) {
		long start = System.currentTimeMillis();
		int w = 400, h = 400;
		ImageProcessor ip = new ColorProcessor(w, h);
		int[] pixels = (int[])ip.getPixels();
		int i = 0;
                int k = 0;
                int l = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
                                if (k % 2 == 0) {
                                    pixels[i++] = (255 << 24);
                                } else {
                                    pixels[i++] = (255 << 24) | (255 << 16) | (255 << 8) | (255);
                                }
			}
                        if (k == l) {
                            k++;
                            l = 0;
                        } else {
                            l++;
                        }
		}
                for (int pixel = 0; pixel < pixels.length/2; pixel++) {
                    int temp = pixels[pixel];
                    pixels[pixel] = pixels[pixels.length-pixel-1];
                    pixels[pixels.length-pixel-1] = temp;
                }
		new ImagePlus("BuWImage", ip).show();
		IJ.showStatus(""+(System.currentTimeMillis()-start));
	 }
}
