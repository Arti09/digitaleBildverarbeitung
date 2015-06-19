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
public class YellowPoints implements PlugInFilter {
     
    class point {
        public int x;
        public int y;
        
        point (int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    
    int around = 0;
    
    point array[] = {new point(10,10), new point(20,20), new point(40,40)};
        
    @Override
    public int setup(String arg, ImagePlus image) {
        return DOES_RGB;
    }
    
    @Override
    public void run(ImageProcessor ip) {    
        for (int a = 0; a < array.length; a++) {
            ip.setColor(Color.yellow);
            for (int x = array[a].x-around; x <= array[a].x + around; x++) {
                for (int y = array[a].y-around; y <= array[a].y + around; y++) {
                    ip.putPixel(x, y, new int[]{255,255,0});
                }
            }
            ip.drawString("" + a, array[a].x+5, array[a].y+2);
        }
    }
}

