package de.htwkleipzig.dbv.project;

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.process.ColorProcessor;
import ij.text.TextWindow;
import java.awt.*;

public class SignDetection implements PlugInFilter {

    ImagePlus imp;

    @Override
    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        return DOES_RGB + DOES_STACKS;
    }

    @Override
    public void run(ImageProcessor ip) {
        ColorProcessor processor = (ColorProcessor) imp.getProcessor();
        int[] pixels = (int[]) ip.getPixels();
        int width = ip.getWidth();
        int height = ip.getHeight();
        java.awt.Color color = new Color(134, 50, 42);
        int value = ((197 & 0xff) << 16) + ((99 & 0xff) << 8) + (88 & 0xff);
        int black = 0;

        StringBuffer sb = new StringBuffer();

        int i = 0;
        sb.append(i + "\t" + value + "\n");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelValue = processor.getColor(x, y);
                sb.append(i + "\t" + pixelValue + "\n");
                if (pixelValue.equals(color)) {
                    //pixels[i] = ((197 & 0xff)<<16)+((99 & 0xff)<<8) + (88 & 0xff);
                    processor.putPixel(x, y, new int[]{197, 99, 88});
                } else {
                    processor.putPixel(x, y, new int[]{0, 0, 0});
                }
                i++;
                //ip.putPixelValue(x,y, 0);
            }
        }
        TextWindow histo = new TextWindow("Histogram", "Value\tCount", sb.toString(), 300, 400);

        /*
         for(int y = 0; y < height; y++){
         for(int x = 0; x < width; x++){
         float pixelValue = ip.getPixelValue(x,y);
         if(pixelValue == value){
         ip.putPixelValue(x,y,value);
         }
         else {
         ip.putPixelValue(x,y,black);
         }
         }
         }
         */
        imp.updateAndDraw();
    }
}
