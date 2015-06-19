package de.htwkleipzig.dbv.project;

import ij.*;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.process.ColorProcessor;
import java.awt.*;

public class SignDetection implements PlugInFilter {

    ImagePlus imp;
    
    int SHIFTMASK_RED = 16;
    int SHIFTMASK_BLUE = 0;
    int SHIFTMASK_GREEN = 8;
    int PIXELCOLOR_WHITE = (255 << 16) + (255 << 8) + 255;

    @Override
    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        return DOES_RGB + DOES_STACKS;
    }
    
    /***
     * Search for given color inside of a pixel, the color needs to be proportional higher than other colors
     * @param bitmask - Shiftvalue for the Bitmask
     * @param mask_factor - Proportional factor
     * @return a new Image
     */
    ImageProcessor segementByColor (int bitmask, double mask_factor) {
        int width = imp.getWidth();
        int height = imp.getHeight();
        
        ImageProcessor ip = new ColorProcessor(width, height);
        ColorProcessor old_ip = (ColorProcessor) imp.getProcessor();
        int[] pixels = (int[])ip.getPixels();
        int i = 0;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int pixel = old_ip.getPixel(w, h);
                double mask = (double)(((pixel >> 16) & 0xff) + ((pixel >> 8) & 0xff) + (pixel & 0xff)) / mask_factor;
                if (mask == 0) mask = 1;
                if (((double)((old_ip.getPixel(w, h) >> bitmask) & 0xff) / mask) > 1.0) {
                    pixels[i++] = PIXELCOLOR_WHITE;
                } else {
                    pixels[i++] = 0;
                }
            }
        }
        return ip;
    }
    
    /***
     * Searches for a sign form inside of the image and return it's coordinates, width and height
     * @param ipp - Image
     * @param min_seg - minimum sign width / height
     * @param max_seg - maximum sign width / height
     * @return int[4] {x, y, width, height}
     */
    private int[] searchForSignForm(ImageProcessor ipp, int min_seg, int max_seg) {
        double box_fillup_percentage = 0.75;
        int height = ipp.getHeight();
        int width = ipp.getWidth();
        int checks = 0;
        //Build an area x,y
        for (int seg_x = max_seg; seg_x >= min_seg; seg_x--) {
            for (int seg_y = max_seg; seg_y >= min_seg; seg_y--) {
                //Run over the complete image x,y
                for (int img_x = 0; img_x < width - seg_x; img_x ++) {
                    for (int img_y = 0; img_y < height - seg_y; img_y ++) {
                        /********************
                         * Replace this through the new function to find a sign form
                         * For 
                         ********************/
                        int counter = 0;
                        checks++;
                        for (int x = img_x; x < seg_x + img_x; x ++) {
                            for (int y = img_y; y < seg_y + img_y; y ++) {
                                if (ipp.getPixel(x, y) == PIXELCOLOR_WHITE) {
                                    counter ++;
                                }
                            }
                        }
                        if (counter > (((double)seg_x * box_fillup_percentage)* ((double)seg_y * box_fillup_percentage))) {
                            GenericDialog gd = new GenericDialog("New Image");
                            gd.addMessage("counter = " + counter);
                            gd.addMessage("control = " + (((double)seg_x * box_fillup_percentage)* ((double)seg_y * box_fillup_percentage)));
                            gd.addMessage("checks = " + checks);
                            gd.showDialog();
                            return new int[] {img_x, img_y, seg_x, seg_y}; 
                        }
                        if (checks % 10000 == 0) IJ.showStatus("Checks " + checks);
                        /********************
                         * Replace until here
                         * to break the circle return int{x,y,widht,height}
                         ********************/
                    }
                }
            }    
        }
        return new int[]{-1,-1,-1,-1};
    }
    
    @Override
    public void run(ImageProcessor ip) {
        // Values
        double mask_factor = 2.0;
        String color = "RED";
        
        int min_seg = 10;
        int max_seg = 150;
        
        // Build Input Panel for Values
        GenericDialog gd = new GenericDialog("New Image");
        gd.addNumericField("Color Segmentation - Mask Factor: ", mask_factor, 2);
        gd.addChoice("Color Segmentation - Color: ", new String[]{"RED", "BLUE", "GREEN"}, color);
        gd.addNumericField("Picture Segmentation - Minimum: ", min_seg, 0);
        gd.addNumericField("Picture Segmentation - Maximum: ", max_seg, 0);
        gd.showDialog();
        if (gd.wasCanceled()) return;
        // Read the values into variables
        mask_factor = gd.getNextNumber();
        color = gd.getNextChoice();
        min_seg = (int)gd.getNextNumber();
        max_seg = (int)gd.getNextNumber();
        
        // Segment the picture by selected color
        ImageProcessor ipp = segementByColor("RED".equals(color) ? SHIFTMASK_RED : "BLUE".equals(color) ? SHIFTMASK_BLUE : SHIFTMASK_GREEN, mask_factor);
        // Find the coordinates of the sign form
        int[] coordinates = searchForSignForm(ipp, min_seg, max_seg);
        
        // Draw found area into the image
        ipp.setColor(Color.BLUE);
        ipp.drawRect(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
        
        // Show image
	new ImagePlus("segmented by color - " + color, ipp).show();
        
        /****************************************
         * Old code from Artem
         ****************************************/
        /*
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
        //imp.updateAndDraw();
    }
}
