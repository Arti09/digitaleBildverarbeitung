/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.htwkleipzig.dbv.project;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Ava-chan
 */
public class AreaDetection {
    
    static int groupcount = 0;
    
    public static void groupArea(ImageProcessor input, int[][] output, int height, int width, int x, int y, ImageArea ia, HashMap<Integer,Integer> group_map) {
        if (input.getPixel(x, y) == Glob.PIXELCOLOR_WHITE && output[x][y] == 0) {
            output[x][y] = ia.group_id;
            if (group_map.containsKey(ia.group_id))
                group_map.put(ia.group_id, group_map.get(ia.group_id)+1);
            else
                group_map.put(ia.group_id, 1);
            if (ia.xh < x) ia.xh = x;
            if (ia.xl > x) ia.xl = x;
            if (ia.yh < y) ia.yh = y;
            if (ia.yl > y) ia.yl = y;
            if (y != 0) {
                groupArea(input, output, height, width, x, y-1, ia, group_map);
            }
            if (y != height-1) {
                groupArea(input, output, height, width, x, y+1, ia, group_map);
            }
            if (x != 0) {
                groupArea(input, output, height, width, x-1, y, ia, group_map);
            }
            if (x != width-1) {
                groupArea(input, output, height, width, x+1, y, ia, group_map);
            }
        }
    }
    
    public static ImageProcessor findAreas(ImageProcessor input, int min_pixel_per_group, ArrayList<ImageArea> alia, int color) {
        int height = input.getHeight();
        int width = input.getWidth();
        HashMap<Integer, Integer> group_map = new HashMap<>();
        
        int output[][] = new int[width][height];
        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < height; y ++) {
                output[x][y] = 0;
            }
        }
        group_map.put(0, 0);
        groupcount = 0;
        
        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < height; y ++) {
                if (input.getPixel(x, y) == Glob.PIXELCOLOR_WHITE && output[x][y] == 0) {
                    ImageArea ia = new ImageArea();
                    ia.group_id = ++groupcount;
                    ia.xl = x;
                    ia.xh = x;
                    ia.yh = y;
                    ia.yl = y;
                    groupArea(input, output, height, width, x, y, ia, group_map);
                    if (group_map.get(ia.group_id) > min_pixel_per_group) {
                        alia.add(ia);
                    }
                }
            }
        }
        ImageProcessor return_image = new ColorProcessor(width, height);
        int pixels[] = (int[])return_image.getPixels();
        int i = 0;
        for (int y = 0; y < height; y ++) {
            for (int x = 0; x < width; x ++) {
                if (group_map.get(output[x][y]) > min_pixel_per_group) {
                    if (color != 0) 
                        pixels[i] = color;
                    else 
                        pixels[i] = output[x][y];
                } else {
                    pixels[i] = 0;
                }
                i++;
            }
        }
        return return_image;
    }
}

