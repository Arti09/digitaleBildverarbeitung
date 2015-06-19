package de.htwkleipzig.dbv;

import ij.*;
import ij.gui.GenericDialog;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/** This a prototype ImageJ plugin. */
public class RotateXdegree implements PlugInFilter {
        
    @Override
    public int setup(String arg, ImagePlus image) {
        return DOES_8G;
    }
    @Override
    public void run(ImageProcessor ip) {
        double degrees = 90;
    
        GenericDialog gd = new GenericDialog("Rotate picture");
        gd.addNumericField("Degree: ", 90, 0);
        gd.showDialog();
        if (gd.wasCanceled()) return;
        degrees = gd.getNextNumber();
                
        ip.rotate(degrees);
    }
}

