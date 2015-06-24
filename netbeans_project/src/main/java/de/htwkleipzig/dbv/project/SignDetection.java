package de.htwkleipzig.dbv.project;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class SignDetection implements PlugInFilter {

	ImagePlus imp;

	@Override
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB + DOES_STACKS;
	}

	/***
	 * Search for given color inside of a pixel, the color needs to be
	 * proportional higher than other colors
	 * 
	 * @param bitmask
	 *            - Shiftvalue for the Bitmask
	 * @param mask_factor
	 *            - Proportional factor
	 * @return a new Image
	 */
	ImageProcessor segementByColor(int bitmask, double mask_factor) {
		int width = imp.getWidth();
		int height = imp.getHeight();

		ImageProcessor ip = new ColorProcessor(width, height);
		ColorProcessor old_ip = (ColorProcessor) imp.getProcessor();
		int[] pixels = (int[]) ip.getPixels();
		int i = 0;
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				int pixel = old_ip.getPixel(w, h);
				double mask = (double) (((pixel >> 16) & 0xff)
						+ ((pixel >> 8) & 0xff) + (pixel & 0xff))
						/ mask_factor;
				if (mask == 0)
					mask = 1;
				if (((double) ((old_ip.getPixel(w, h) >> bitmask) & 0xff) / mask) > 1.0) {
					pixels[i++] = Glob.PIXELCOLOR_WHITE;
				} else {
					pixels[i++] = 0;
				}
			}
		}
		return ip;
	}

	private int getDetectedForm(ImageProcessor ipp, ImageArea ia) {
		/********************
		 * Replace this through the new function to find a sign form For
		 ********************/
		// throw new UnsupportedOperationException("Not supported yet."); //To
		// change body of generated methods, choose Tools | Templates.
		/********************
		 * Replace until here to break the circle return int{x,y,widht,height}
		 ********************/
		return 0;
	}

	/***
	 * Searches for a sign form inside of the image and return it's coordinates,
	 * width and height
	 * 
	 * @param ipp
	 *            - Image
	 * @param min_seg
	 *            - minimum sign width / height
	 * @param max_seg
	 *            - maximum sign width / height
	 * @return int[4] {x, y, width, height}
	 */
	@Deprecated
	private int[] searchForSignForm(ImageProcessor ipp, int min_seg, int max_seg) {
		double box_fillup_percentage = 0.75;
		int height = ipp.getHeight();
		int width = ipp.getWidth();
		int checks = 0;
		// Build an area x,y
		for (int seg_x = max_seg; seg_x >= min_seg; seg_x--) {
			for (int seg_y = max_seg; seg_y >= min_seg; seg_y--) {
				// Run over the complete image x,y
				for (int img_x = 0; img_x < width - seg_x; img_x++) {
					for (int img_y = 0; img_y < height - seg_y; img_y++) {
						/********************
						 * Replace this through the new function to find a sign
						 * form For
						 ********************/
						int counter = 0;
						checks++;
						for (int x = img_x; x < seg_x + img_x; x++) {
							for (int y = img_y; y < seg_y + img_y; y++) {
								if (ipp.getPixel(x, y) == Glob.PIXELCOLOR_WHITE) {
									counter++;
								}
							}
						}
						if (counter > (((double) seg_x * box_fillup_percentage) * ((double) seg_y * box_fillup_percentage))) {
							GenericDialog gd = new GenericDialog("New Image");
							gd.addMessage("counter = " + counter);
							gd.addMessage("control = "
									+ (((double) seg_x * box_fillup_percentage) * ((double) seg_y * box_fillup_percentage)));
							gd.addMessage("checks = " + checks);
							gd.showDialog();
							return new int[] { img_x, img_y, seg_x, seg_y };
						}
						if (checks % 10000 == 0)
							IJ.showStatus("Checks " + checks);
						/********************
						 * Replace until here to break the circle return
						 * int{x,y,widht,height}
						 ********************/
					}
				}
			}
		}
		return new int[] { -1, -1, -1, -1 };
	}

	@Override
	public void run(ImageProcessor ip) {
		// Values
		double mask_factor = 2.0;
		String color = "RED";

		int min_seg = 30;

		// Build Input Panel for Values
		GenericDialog gd = new GenericDialog("New Image");
		gd.addNumericField("Color Segmentation - Mask Factor: ", mask_factor, 2);
		gd.addChoice("Color Segmentation - Color: ", new String[] {
				SegmentedColor.RED.toString(), SegmentedColor.BLUE.toString(),
				SegmentedColor.GREEN.toString() }, color);
		gd.addNumericField("Picture Segmentation - Min pixel in a group: ",
				min_seg, 0);
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		// Read the values into variables
		mask_factor = gd.getNextNumber();
		color = gd.getNextChoice();
		min_seg = (int) gd.getNextNumber();

		// Segment the picture by selected color
		ImageProcessor ipp = segementByColor(SegmentedColor.RED.toString()
				.equals(color) ? Glob.SHIFTMASK_RED : SegmentedColor.BLUE
				.toString().equals(color) ? Glob.SHIFTMASK_BLUE
				: Glob.SHIFTMASK_GREEN, mask_factor);
		// Find the coordinates of the sign form
		ArrayList<ImageArea> alia = new ArrayList<>();
		ipp = AreaDetection
				.findAreas(ipp, min_seg, alia, Glob.PIXELCOLOR_WHITE);

		// int[] coordinates = searchForSignForm(ipp, min_seg, max_seg);

		// Draw found area into the image
		ipp.setColor(Color.RED);

		PlotComparator comparator = new PlotComparator(ipp,
				SegmentedColor.valueOf(color));
		List<DetectebleSigns> detectedSigns = new ArrayList<DetectebleSigns>();
		for (ImageArea ia : alia) {
			int form = getDetectedForm(ipp, ia);
			form = 3;
			DetectebleSigns sign = comparator.comparePlot(ia, form);
			if (sign.compareTo(DetectebleSigns.NOTHING) != 0) {
				detectedSigns.add(sign);
			}
			ipp.drawRect(ia.xl, ia.yl, ia.xh - ia.xl + 1, ia.yh - ia.yl + 1);
		}

		// Show image
		new ImagePlus("segmented by color - " + color, ipp).show();
	}
}
