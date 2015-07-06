package de.htwkleipzig.dbv.project;

import ij.ImagePlus;
import ij.gui.Roi;
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
	 *            0=Circle, 1=Triangle, 2=Rectangle, 3=Polygon
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
					// ImagePlus image = new ImagePlus(
					// "/Users/Arti/Downloads/images.png");
					// image.show();
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
		int[] foundBottoms = prepareImage(area,
				new int[][] { { 0, 46, 80, 46 } });

		boolean secondLinePlot = (foundBottoms[0] < PlotValues
				.getStopSignValues()[1] - 2 || foundBottoms[0] > PlotValues
				.getStopSignValues()[1] + 1) ? false : true;

		return true && secondLinePlot;
	}

	private DetectebleSigns checkBlueRectangle(ImageArea area) {
		int[][] foundBottoms = prepareImageBlueRectangle(area, new int[][] {
				{ 0, 26, 79, 26 }, { 0, 59, 79, 59 } });
		boolean firstLinePlot = false;
		boolean secondLinePlot = false;
		if (foundBottoms[1][1] > 20) {
			firstLinePlot = (foundBottoms[0][0] < PlotValues.getOnewayValues()[0] - 2 || foundBottoms[0][0] > PlotValues
					.getOnewayValues()[0] + 1) ? false : true;
			secondLinePlot = (foundBottoms[1][0] < PlotValues.getOnewayValues()[1] - 2 || foundBottoms[1][0] > PlotValues
					.getOnewayValues()[1] + 1) ? false : true;

			return (firstLinePlot && secondLinePlot) ? DetectebleSigns.ONEWAY
					: DetectebleSigns.NOTHING;

		} else {
			firstLinePlot = (foundBottoms[0][0] < PlotValues
					.getPedestrianValues()[0] - 2 || foundBottoms[0][0] > PlotValues
					.getPedestrianValues()[0] + 1) ? false : true;
			secondLinePlot = (foundBottoms[1][0] < PlotValues
					.getPedestrianValues()[1] - 2 || foundBottoms[1][0] > PlotValues
					.getPedestrianValues()[1] + 1) ? false : true;

			return (firstLinePlot && secondLinePlot) ? DetectebleSigns.PEDESTRIAN
					: DetectebleSigns.NOTHING;
		}
	}

	private DetectebleSigns checkRedTriangle(ImageArea area) {
		int[] foundBottoms = prepareImage(area, new int[][] {
				{ 20, 26, 60, 26 }, { 5, 59, 75, 59 } });
		boolean firstLinePlot = false;
		boolean secondLinePlot = false;

		if (foundBottoms[0] > 0) {
			firstLinePlot = (foundBottoms[0] < PlotValues.getPriorityValues()[0] || foundBottoms[0] > PlotValues
					.getPriorityValues()[0] + 1) ? false : true;
			secondLinePlot = (foundBottoms[1] < PlotValues.getPriorityValues()[1] || foundBottoms[1] > PlotValues
					.getPriorityValues()[1] + 1) ? false : true;

			return (firstLinePlot && secondLinePlot) ? DetectebleSigns.PRIORITY_SIGN
					: DetectebleSigns.NOTHING;
		} else {
			firstLinePlot = (foundBottoms[0] < PlotValues.getGiveawayValues()[0] || foundBottoms[0] > PlotValues
					.getGiveawayValues()[0] + 1) ? false : true;
			secondLinePlot = (foundBottoms[1] < PlotValues.getGiveawayValues()[1] || foundBottoms[1] > PlotValues
					.getGiveawayValues()[1] + 1) ? false : true;

			return (firstLinePlot && secondLinePlot) ? DetectebleSigns.GIVEAWAY
					: DetectebleSigns.NOTHING;
		}
	}

	private DetectebleSigns checkRedCircle(ImageArea area) {
		int[] foundBottoms = prepareImage(area, new int[][] {
				{ 0, 26, 80, 26 }, { 0, 43, 80, 43 } });
		boolean firstLinePlot = false;
		boolean secondLinePlot = false;
		if (foundBottoms[0] > 2) {
			firstLinePlot = (foundBottoms[0] < PlotValues
					.getNoOvertakingValues()[0] || foundBottoms[0] > PlotValues
					.getNoOvertakingValues()[0] + 1) ? false : true;
			secondLinePlot = (foundBottoms[1] < PlotValues
					.getNoOvertakingValues()[1] || foundBottoms[1] > PlotValues
					.getNoOvertakingValues()[1] + 1) ? false : true;

			return (firstLinePlot && secondLinePlot) ? DetectebleSigns.NO_OVERTAKING
					: DetectebleSigns.NOTHING;
		} else {
			firstLinePlot = (foundBottoms[0] < PlotValues
					.getProhibitionValues()[0] || foundBottoms[0] > PlotValues
					.getProhibitionValues()[0] + 1) ? false : true;
			secondLinePlot = (foundBottoms[1] < PlotValues
					.getProhibitionValues()[1] || foundBottoms[1] > PlotValues
					.getProhibitionValues()[1] + 1) ? false : true;

			return (firstLinePlot && secondLinePlot) ? DetectebleSigns.PROHIBITION
					: DetectebleSigns.NOTHING;
		}
	}

	private DetectebleSigns checkBlueCircle(ImageArea area) {
		int[] foundBottoms = prepareImage(area, new int[][] {
				{ 0, 26, 79, 26 }, { 0, 59, 79, 59 } });
		boolean firstLinePlot = false;
		boolean secondLinePlot = false;
		if (foundBottoms[0] > 4) {
			firstLinePlot = (foundBottoms[0] < PlotValues.getCircleValues()[0] - 2 || foundBottoms[0] > PlotValues
					.getCircleValues()[0] + 1) ? false : true;
			secondLinePlot = (foundBottoms[1] < PlotValues.getCircleValues()[1] - 2 || foundBottoms[1] > PlotValues
					.getCircleValues()[1] + 1) ? false : true;

			return (firstLinePlot && secondLinePlot) ? DetectebleSigns.CIRCLE
					: DetectebleSigns.NOTHING;
		} else {
			firstLinePlot = (foundBottoms[0] < PlotValues.getStraightValues()[0] - 2 || foundBottoms[0] > PlotValues
					.getStraightValues()[0] + 1) ? false : true;
			secondLinePlot = (foundBottoms[1] < PlotValues.getStraightValues()[1] - 2 || foundBottoms[1] > PlotValues
					.getStraightValues()[1] + 1) ? false : true;

			return (firstLinePlot && secondLinePlot) ? DetectebleSigns.STRAIGHT
					: DetectebleSigns.NOTHING;
		}
	}

	private int[] prepareImage(ImageArea area, int[]... lineplot) {
		Roi roi = new Roi(area.xl, area.yl, area.xh - area.xl, area.yh
				- area.yl);
		ip.setRoi(roi);

		// Cut Area from Original Image
		ImageProcessor ipp = ip.crop();

		// New Image with Size of the Area
		ImagePlus workingImage = new ImagePlus("Working", ipp);

		// Scaled Image (rectification)
		ImageProcessor lastIpp = workingImage.getProcessor().resize(80, 80);
		ImagePlus imageScaled = new ImagePlus(area.group_id + "", lastIpp);

		// if needed, show image that is analyzed
		// imageScaled.show();

		if (lineplot.length > 1) {
			// LinePlot over Image
			int counterBottom = checkPlot(imageScaled, lineplot[0]);
			int counterBottomSec = checkPlot(imageScaled, lineplot[1]);

			return new int[] { counterBottom, counterBottomSec };
		} else {
			// LinePlot over Image
			int counterBottom = checkPlot(imageScaled, lineplot[0]);

			return new int[] { counterBottom, 0 };
		}
	}

	private int[][] prepareImageBlueRectangle(ImageArea area, int[]... lineplot) {
		Roi roi = new Roi(area.xl, area.yl, area.xh - area.xl, area.yh
				- area.yl);
		ip.setRoi(roi);

		// Cut Area from Original Image
		ImageProcessor ipp = ip.crop();

		// New Image with Size of the Area
		ImagePlus workingImage = new ImagePlus("Working", ipp);

		// Scaled Image (rectification)
		ImageProcessor lastIpp = workingImage.getProcessor().resize(80, 80);
		ImagePlus imageScaled = new ImagePlus(area.group_id + "", lastIpp);

		// if needed, show image that is analyzed
		imageScaled.show();

		if (lineplot.length > 1) {
			// LinePlot over Image
			int[] counterBottom = checkPlotDitance(imageScaled, lineplot[0]);
			int[] counterBottomSec = checkPlotDitance(imageScaled, lineplot[1]);

			return new int[][] { counterBottom, counterBottomSec };
		} else {
			// LinePlot over Image
			int[] counterBottom = checkPlotDitance(imageScaled, lineplot[0]);

			return new int[][] { counterBottom };
		}
	}

	private int checkPlot(ImagePlus imageScaled, int[] lineplot) {
		int counterBottom = 0;
		for (int i = lineplot[0]; i < lineplot[2]; i++) {
			if (i > 0) {
				if (((imageScaled.getPixel(i, lineplot[1])[0] == 255)
						&& (imageScaled.getPixel(i - 1, lineplot[1])[0] == 0) && (counterBottom > 0))
						|| ((imageScaled.getPixel(i, lineplot[1])[0] == 0) && (imageScaled
								.getPixel(i - 1, lineplot[1])[0] > 0))) {
					counterBottom++;
				}
			} else {
				if (imageScaled.getPixel(i, lineplot[1])[0] == 255) {
					counterBottom++;
				}
			}
		}
		return counterBottom;
	}

	private int[] checkPlotDitance(ImagePlus imageScaled, int[] lineplot) {
		int counterBottom = 0;
		int distance = 0;
		int temp = -1;
		for (int i = lineplot[0]; i < lineplot[2]; i++) {
			if (i > 0) {
				if (((imageScaled.getPixel(i, lineplot[1])[0] == 255)
						&& (imageScaled.getPixel(i - 1, lineplot[1])[0] == 0) && (counterBottom > 0))) {
					counterBottom++;
					if (temp > -1) {
						distance = i - temp;
					}

				} else if ((imageScaled.getPixel(i, lineplot[1])[0] == 0)
						&& (imageScaled.getPixel(i - 1, lineplot[1])[0] > 0)) {
					counterBottom++;
					temp = i;
				}
			} else {
				if (imageScaled.getPixel(i, lineplot[1])[0] == 255) {
					counterBottom++;

				}
			}
		}
		return new int[] { counterBottom, distance };
	}
}
