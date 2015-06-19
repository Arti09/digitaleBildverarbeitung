package de.htwkleipzig.dbv;

import ij.*;
import ij.gui.GenericDialog;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;
import java.awt.Color;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/** This a prototype ImageJ plugin. */
public class RedSquares implements PlugInFilter {
     
    int width_n_height = 10;
            
    @Override
    public int setup(String arg, ImagePlus image) {
        return DOES_RGB;
    }
    
    @Override
    public void run(ImageProcessor ip) {    
        int width = ip.getWidth();
        int height = ip.getHeight();
        ip.setColor(Color.red);
        for (int w = 0; w < width; w+=width_n_height) {
            ip.drawLine(w, 0, w, height);
        }
        for (int h = 0; h < height; h+=width_n_height) {
            ip.drawLine(0, h, width, h);
        }
    }
}

