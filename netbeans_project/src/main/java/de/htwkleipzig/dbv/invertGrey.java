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
public class invertGrey implements PlugInFilter {
    @Override
    public int setup(String arg, ImagePlus image) {
        return DOES_ALL;
    }
    @Override
    public void run(ImageProcessor ip) {
        ip.invert();
    }
}

