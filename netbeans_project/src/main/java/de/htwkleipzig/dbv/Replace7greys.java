package de.htwkleipzig.dbv;

import ij.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/** This a prototype ImageJ plugin. */
public class Replace7greys implements PlugInFilter {
    
    int areas = 7;
    
    @Override
    public int setup(String arg, ImagePlus image) {
        return DOES_8G;
    }
    @Override
    public void run(ImageProcessor ip) {
        int width = ip.getWidth();
        int height = ip.getHeight();
        int cpix = 0;
        int cmaxpix = ((width * height) / areas)+1;
        int newc = 0;
        for (int c = 0; c < 256; c++) {
            for (int w = 0; w < width; w++) {
                for (int h = 0; h < height; h++) {
                    if (ip.get(w, h) == c) {
                        cpix ++;
                        ip.set(w, h, newc * 255 / (areas - 1));
                        if (cpix == cmaxpix) {
                            cpix = 0;
                            newc++;
                        }
                    }
                }
            }
        }
    }
}

