package de.htwkleipzig.dbv;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.ChannelSplitter;
import ij.plugin.ImageCalculator;
import ij.plugin.PlugIn;
import ij.plugin.RGBStackMerge;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/** This a prototype ImageJ plugin. */
public class Difference implements PlugIn {

	private static final String[] operators = { "Add", "Subtract", "Multiply",
			"Divide", "AND", "OR", "XOR", "Min", "Max", "Average",
			"Difference", "Copy", "Transparent-zero", "2.3a-or", "2.3b-and",
			"2.3c-sub", "2.3d-xor" };
	private static final String[] lcOperators = { "add", "sub", "mul", "div",
			"and", "or", "xor", "min", "max", "ave", "diff", "copy", "zero",
			"2.3a-or", "2.3b-and", "2.3c-sub", "2.3d-xor" };
	private static int operator;
	private static String title1 = "";
	private static String title2 = "";

	@Override
	public void run(String arg) {
		int[] wList = WindowManager.getIDList();
		if (wList == null) {
			IJ.noImage();
			return;
		}
		IJ.register(ImageCalculator.class);
		String[] titles = new String[wList.length];
		for (int i = 0; i < wList.length; i++) {
			ImagePlus imp = WindowManager.getImage(wList[i]);
			if (imp != null)
				titles[i] = imp.getTitle();
			else
				titles[i] = "";
		}
		GenericDialog gd = new GenericDialog("Image Calculator",
				IJ.getInstance());
		String defaultItem;
		if (title1.equals(""))
			defaultItem = titles[0];
		else
			defaultItem = title1;
		gd.addChoice("Image1:", titles, defaultItem);
		gd.addChoice("Operation:", operators, operators[operator]);
		if (title2.equals(""))
			defaultItem = titles[0];
		else
			defaultItem = title2;
		gd.addChoice("Image2:", titles, defaultItem);
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		int index1 = gd.getNextChoiceIndex();
		title1 = titles[index1];
		operator = gd.getNextChoiceIndex();
		int index2 = gd.getNextChoiceIndex();
		title2 = titles[index2];
		ImagePlus img1 = WindowManager.getImage(wList[index1]);
		ImagePlus img2 = WindowManager.getImage(wList[index2]);
		/*
		 * switch (lcOperators[operator]) { case "2.3a-or":
		 * CreateSplitImage(img1, img2, "OR create"); break; case "2.3b-and":
		 * CreateSplitImage(img1, img2, "AND create"); break; case "2.3c-sub":
		 * CreateSplitImage(img1, img2, "Subtract create"); break; case
		 * "2.3d-xor": CreateSplitImage(img1, img2, "XOR create"); break;
		 * default: ImageCalculator ic = new ImageCalculator(); ImagePlus img3 =
		 * ic.run(operators[operator] + " create", img1, img2); if (img3!=null)
		 * img3.show(); }
		 */
	}

	private void CreateSplitImage(ImagePlus img1, ImagePlus img2, String create) {
		ImageCalculator ic = new ImageCalculator();
		ImagePlus bkg = ic.run("OR create", img1, img2);
		ImagePlus img3 = ic.run(create, img1, img2);
		bkg = ic.run("Subtract create", bkg, img3);
		ImagePlus[] arrayMerge_b = { img3, img3, null, null, null, null };
		img3 = ChannelSplitter.split(RGBStackMerge.mergeChannels(arrayMerge_b,
				true))[1];

		ImagePlus[] arrayMerge_bc = { null, img3, null, bkg, null, null };
		img3 = RGBStackMerge.mergeChannels(arrayMerge_bc, true);
		img3.show();
	}
}
