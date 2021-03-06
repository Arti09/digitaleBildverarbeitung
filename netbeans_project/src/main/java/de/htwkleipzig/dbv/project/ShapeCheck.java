package de.htwkleipzig.dbv.project;

import ij.ImagePlus;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class ShapeCheck implements PlugInFilter {
	@Override
	public int setup(String s, ImagePlus imagePlus) {

		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {

		GenericDialog d = new GenericDialog("Parameter");
		d.addNumericField("x", 0, 1);
		d.addNumericField("y", 0, 1);
		d.addNumericField("w", 0, 1);
		d.addNumericField("h", 0, 1);
		d.showDialog();

		int x = (int) d.getNextNumber();
		int y = (int) d.getNextNumber();
		int w = (int) d.getNextNumber();
		int h = (int) d.getNextNumber();

		int i = getshape(ip, x, y, w, h);
		// IJ.write(i + "");

	}

	/**
	 * @param: ip das ImageProcessor eines Bildes
	 * @param: x0,y0 Anfangskoordinaten des Bereiches in dem das Verkehrszeichen
	 *         sich befindet
	 * @param: w,h die Breite und die Höhe des des Bereiches in dem das
	 *         Verkehrszeichen sich befindet
	 * @return: den Wert 0, 1, 2 oder -1. 0:= Ellipse, 1 := Dreieck, 2:=
	 *          Rechteck, -1:= Fehler
	 */
	static int getshape(ImageProcessor ip, int x0, int y0, int w, int h) {
		// Bild vorbereiten
		// 1. RGB-Bild in Grauwertbild umwandeln
		ip = ip.convertToByte(true);

		// 2. Das Bild auf die Größe des ROI skalieren.
		ip.setRoi(x0, y0, w, h);
		ip = ip.crop();
		int width = ip.getWidth();
		int height = ip.getHeight();

		// 3. Bild bereinigen.
		ip.invert();
		ip.dilate();
		ip.erode();
		int fg = Prefs.blackBackground ? 255 : 0;
		int foreground = ip.isInvertedLut() ? 255 - fg : fg;
		int background = 255 - foreground;
		// new ImagePlus("test before", ip).show();

		// FloodFiller ff = new FloodFiller(ip);
		// ip.setColor(127);
		/*
		 * for (int y = 0; y < height; y++) { if (ip.getPixel(0, y) ==
		 * background) ff.fill(0, y); if (ip.getPixel(width - 1, y) ==
		 * background) ff.fill(width - 1, y); } for (int x = 0; x < width; x++)
		 * { if (ip.getPixel(x, 0) == background) ff.fill(x, 0); if
		 * (ip.getPixel(x, height - 1) == background) ff.fill(x, height - 1); }
		 */

		// byte[] pixels = (byte[]) ip.getPixels();
		/*
		 * int n = width * height; for (int i = 0; i < n - 1; i++) { if
		 * (pixels[i] == 127) pixels[i] = (byte) background; else if (pixels[i]
		 * == 0 && pixels[i + 1] == 255) pixels[i] = (byte) foreground; }
		 */
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (ip.getPixel(x, y) == 0) {
					int x2 = 0;
					for (x2 = width - 1; x2 >= 0; x2--) {
						if (ip.getPixel(x2, y) == 0) {
							break;
						}
					}
					// ip.putPixel(x, y, (byte) 127);
					// ip.putPixel(x2, y, (byte) 127);
					for (int x3 = x; x3 < x2; x3++) {
						ip.putPixel(x3, y, (byte) 0);
						// pixels[x3 + y * height] = (byte) 255;
					}

					break;
				}
			}
		}

		// new ImagePlus("test", ip).show();
		ip.invert();
		// new ImagePlus("test", ip).show();
		// Bestimme die zentrale Momente m_00, m_20, m_02 und m_11.
		int s_x = 0;
		int s_y = 0;
		double[] m = new double[4];
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {

				int pixel = ip.getPixel(u, v);
				if (pixel == 255) {

					s_x = s_x + u;
					s_y = s_y + v;
					m[0] = m[0] + 1; // m_00
				}
			}
		}
		s_x = (int) (s_x / m[0]);
		s_y = (int) (s_y / m[0]);

		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				int pixel = ip.getPixel(u, v);
				if (pixel == 255) {
					m[1] = m[1] + (u - s_x) * (u - s_x); // m_20
					m[2] = m[2] + (v - s_y) * (v - s_y); // m_02
					m[3] = m[3] + (u - s_x) * (v - s_y); // m_11
				}
			}
		}

		// Berechne die Größe I.
		double I = (m[1] * m[2] - m[3] * m[3]) / (m[0] * m[0] * m[0] * m[0]);

		// Berechne die Größen E, T und R.
		double E, T, R;
		if (I < 1. / 158) {
			E = I * 158;
		} else {
			E = 1. / (158 * I);
		}

		if (I < 1. / 108) {
			T = 108 * I;
		} else {
			T = 1. / (108 * I);
		}

		R = m[0] / (width * height);
		// IJ.write(E + "\n " + T + "\n " + R);

		// Entscheide welche geometrische Form dasgestellt wird.
		// 0:= Ellipse
		// 1:= Dreieck
		// 2:= Rechteck
		// 3:= Achteck
		// -1:= Fehler (die geometrische Form kann nicht erkannt werden)

		if (0.97 < E && E < 1) {
			// GenericDialog gd = new GenericDialog("Counter");
			// gd.addMessage("E = " + E + " T = " + T + " R = " + R);
			// gd.showDialog();
			if (0.8 <= R && R < 1)
				return 3;
			else
				return 0;
		} else {
			if (0.91 < T && T < 1)
				return 1;
			else {
				if (0.8 <= R && R < 1)
					return 2;
				else
					return -1;
			}
		}
		// if (0.5 < R && R < 0.85)
	}
}
