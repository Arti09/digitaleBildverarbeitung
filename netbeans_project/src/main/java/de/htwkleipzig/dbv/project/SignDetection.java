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

	/**
	 * * Search for given color inside of a pixel, the color needs to be
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
				if (mask == 0) {
					mask = 1;
				}
				if (((double) ((old_ip.getPixel(w, h) >> bitmask) & 0xff) / mask) > 1.0) {
					pixels[i++] = Glob.PIXELCOLOR_WHITE;
				} else {
					pixels[i++] = 0;
				}
			}
		}
		return ip;
	}

	/**
	 * * Searches for a sign form inside of the image and return it's
	 * coordinates, width and height
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
						/**
						 * ****************** Replace this through the new
						 * function to find a sign form For
						 ******************* 
						 */
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
						if (checks % 10000 == 0) {
							IJ.showStatus("Checks " + checks);
						}
						/**
						 * ****************** Replace until here to break the
						 * circle return int{x,y,widht,height}
						 ******************* 
						 */
					}
				}
			}
		}
		return new int[] { -1, -1, -1, -1 };
	}

	@Override
	public void run(ImageProcessor ip) {
		long start = System.currentTimeMillis();

		// Values
		double mask_factor = 2.0;
		String color = "RED";

		int min_seg = 100;

		// Build Input Panel for Values
		GenericDialog gd = new GenericDialog("New Image");
		gd.addNumericField("Color Segmentation - Mask Factor: ", mask_factor, 2);
		gd.addChoice("Color Segmentation - Color: ", new String[] {
				SegmentedColor.RED.toString(), SegmentedColor.BLUE.toString(),
				SegmentedColor.GREEN.toString() }, color);
		gd.addNumericField("Picture Segmentation - Min pixel in a group: ",
				min_seg, 0);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return;
		}
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
		ArrayList<ImageArea> alia = new ArrayList<ImageArea>();
		ipp = AreaDetection
				.findAreas(ipp, min_seg, alia, Glob.PIXELCOLOR_WHITE);

		// Draw found area into the image
		PlotComparator comparator = new PlotComparator(ipp,
				SegmentedColor.valueOf(color));
		List<DetectebleSigns> detectedSigns = new ArrayList<DetectebleSigns>();
		for (ImageArea ia : alia) {
			int form = ShapeCheck.getshape(ipp, ia.xl, ia.yl,
					ia.xh - ia.xl + 1, ia.yh - ia.yl + 1);
			if (form != -1) {
				ipp.setColor(Color.BLUE);
				ipp.drawRect(ia.xl, ia.yl, ia.xh - ia.xl + 1, ia.yh - ia.yl + 1);
				// Detect a Sign
				DetectebleSigns sign = comparator.comparePlot(ia, form);
				// if (sign.compareTo(DetectebleSigns.NOTHING) == 0 && form ==
				// 1) {
				// sign = comparator.comparePlot(ia, 3);
				// }
				if (sign.compareTo(DetectebleSigns.NOTHING) != 0) {
					detectedSigns.add(sign);
					ipp.setColor(Color.RED);
					ipp.drawRect(ia.xl, ia.yl, ia.xh - ia.xl + 1, ia.yh - ia.yl
							+ 1);
					if (sign == DetectebleSigns.STOP_SIGN) {
						ipp.drawString("STOP-SIGN", ia.xl, ia.yl);
					}
					if (sign == DetectebleSigns.STRAIGHT) {
						ipp.drawString("STRAIGHT-SIGN", ia.xl, ia.yl);
					}
					if (sign == DetectebleSigns.CIRCLE) {
						ipp.drawString("CIRCLE-SIGN", ia.xl, ia.yl);
					}
					if (sign == DetectebleSigns.GIVEAWAY) {
						ipp.drawString("GIVEAWAY-SIGN", ia.xl, ia.yl);
					}
					if (sign == DetectebleSigns.CROSS) {
						ipp.drawString("CROSS-SIGN", ia.xl, ia.yl);
					}
					if (sign == DetectebleSigns.NO_OVERTAKING) {
						ipp.drawString("NO-OVERTAKING-SIGN", ia.xl, ia.yl);
					}
					if (sign == DetectebleSigns.ONEWAY) {
						ipp.drawString("ONEWAY-SIGN", ia.xl, ia.yl);
					}
					if (sign == DetectebleSigns.PARKING) {
						ipp.drawString("PARKING-SIGN", ia.xl, ia.yl);
					}
					if (sign == DetectebleSigns.PEDESTRIAN) {
						ipp.drawString("PEDESTRIAN-SIGN", ia.xl, ia.yl);
					}
					if (sign == DetectebleSigns.PRIORITY_SIGN) {
						ipp.drawString("PRIORITY-SIGN", ia.xl, ia.yl);
					}
					if (sign == DetectebleSigns.PROHIBITION) {
						ipp.drawString("PROHIBITION-SIGN", ia.xl, ia.yl);
					}
				} else {
					if (form == 0) {
						ipp.drawString("Kreis", ia.xl, ia.yl);
					} else if (form == 1) {
						ipp.drawString("Dreieck", ia.xl, ia.yl);
					} else if (form == 3) {
						ipp.drawString("Achteck", ia.xl, ia.yl);
					} else {
						ipp.drawString("Viereck", ia.xl, ia.yl);
					}

				}
			}
		}
		// Show image
		new ImagePlus("segmented by color - " + color, ipp).show();

		IJ.showStatus("" + (System.currentTimeMillis() - start));
	}
}
