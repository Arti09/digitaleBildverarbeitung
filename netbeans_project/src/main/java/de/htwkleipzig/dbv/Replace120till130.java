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
public class Replace120till130 implements PlugInFilter {
    @Override
    public int setup(String arg, ImagePlus image) {
        return DOES_8G;
    }
    @Override
    public void run(ImageProcessor ip) {
        int width = ip.getWidth();
        int height = ip.getHeight();
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                int p = ip.get(w, h);
                if (p >= 120 && p <= 130) {
                    ip.set(w, h, 0);
                }
            }
        }
    }
}

