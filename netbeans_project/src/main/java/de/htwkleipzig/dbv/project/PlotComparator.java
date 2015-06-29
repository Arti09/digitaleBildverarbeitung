package de.htwkleipzig.dbv.project;

import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Plot;
import ij.gui.PlotWindow;
import ij.gui.ProfilePlot;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;

public class PlotComparator {

	ImageProcessor ip;
	SegmentedColor color;

	public PlotComparator(ImageProcessor ip, SegmentedColor color) {
		this.ip = ip;
		this.color = color;
	}

	/**
	 * Returns the Type of Sign in the ImageArea or if nothing is found a
	 * DetectableSigns.NOTHING will be returned.
	 * 
	 * @param area
	 * @param form
	 *            3=Circle, 1=Triangle, 2=Rectangle, 0=Polygon
	 * @return
	 */
	public DetectebleSigns comparePlot(ImageArea area, int form) {
		switch (form) {
		case 0:
			if (color.compareTo(SegmentedColor.RED) == 0) {
				return checkRedCircle(area);
			} else if (color.compareTo(SegmentedColor.BLUE) == 0) {
				return checkBlueCircle(area);
			} else {
				return DetectebleSigns.NOTHING;
			}
		case 1:
			if (color.compareTo(SegmentedColor.RED) == 0) {
				return checkRedTriangle(area);
			} else if (color.compareTo(SegmentedColor.BLUE) == 0) {
				return DetectebleSigns.NOTHING;
			} else {
				return DetectebleSigns.NOTHING;
			}
		case 2:
			if (color.compareTo(SegmentedColor.RED) == 0) {
				return DetectebleSigns.NOTHING;
			} else if (color.compareTo(SegmentedColor.BLUE) == 0) {
				return checkBlueRectangle(area);
			} else {
				return DetectebleSigns.NOTHING;
			}
		case 3:
			if (color.compareTo(SegmentedColor.RED) == 0) {
				if (checkStopsign(area)) {
					ImagePlus image = new ImagePlus(
							"/Users/Arti/Downloads/images.png");
					image.show();
					return DetectebleSigns.STOP_SIGN;
				} else {
					return DetectebleSigns.NOTHING;
				}
			} else if (color.compareTo(SegmentedColor.BLUE) == 0) {
				return DetectebleSigns.NOTHING;
			} else {
				return DetectebleSigns.NOTHING;
			}
		default:
			return DetectebleSigns.NOTHING;
		}
	}

	private boolean checkStopsign(ImageArea area) {
		int foundBottoms = prepareImage(area, new int[] { 0, 43, 80, 43 });
		return (foundBottoms < PlotValues.getStopSignValues() - 2 || foundBottoms > PlotValues
				.getStopSignValues() + 1) ? false : true;
	}

	private DetectebleSigns checkBlueRectangle(ImageArea area) {
		return DetectebleSigns.NOTHING;
	}

	private DetectebleSigns checkRedTriangle(ImageArea area) {
		return DetectebleSigns.NOTHING;
	}

	private DetectebleSigns checkRedCircle(ImageArea area) {
		return DetectebleSigns.NOTHING;
	}

	private DetectebleSigns checkBlueCircle(ImageArea area) {
		return DetectebleSigns.NOTHING;
	}

	private int prepareImage(ImageArea area, int[] lineplot) {
		Roi roi = new Roi(area.xl, area.yl, area.xh - area.xl, area.yh
				- area.yl);

		ImagePlus image = new ImagePlus("temp", ip);
		image.setRoi(roi);
		image.copy();
		ImageProcessor ipp = ip.resize(area.xh - area.xl, area.yh - area.yl);
		ImagePlus workingImage = new ImagePlus("Working", ipp);
		workingImage.paste();
		ImageProcessor lastIpp = workingImage.getProcessor().resize(80, 80);
		ImagePlus imageScaled = new ImagePlus("last", lastIpp);
		// imageScaled.show();

		Line line = new Line(lineplot[0], lineplot[1], lineplot[2], lineplot[3]);
		imageScaled.setRoi(line);
		ProfilePlot profile = new ProfilePlot(imageScaled);
		Plot plot = profile.getPlot();
		PlotWindow window = plot.show();

		ResultsTable table = window.getResultsTable();
		float[] yValues = table.getColumn(1);
		int counterBottom = 0;
		for (int i = 0; i < yValues.length; i++) {
			if (i > 0) {
				if ((yValues[i] == 0 && yValues[i - 1] > 0)
						|| (yValues[i] > 0 && yValues[i - 1] == 0)) {
					counterBottom++;
				}
			} else {
				if (yValues[i] == 0) {
					counterBottom++;
				}
			}
		}
		window.close();
		// GenericDialog gd = new GenericDialog("Counter");
		// gd.addMessage("counter = " + counterBottom);
		// gd.showDialog();
		return counterBottom;
	}
}
