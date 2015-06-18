import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.measure.*;
import java.awt.*;
import ij.gui.*;
import java.util.*;
import java.awt.image.*;
import ij.plugin.Colors;
import ij.process.ColorProcessor;
import java.awt.Color.*;

public class YellowMarks implements PlugInFilter {
	ImagePlus imp;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB+DOES_STACKS;
	}

	public void run(ImageProcessor ip) {
		ColorProcessor processor = (ColorProcessor)imp.getProcessor();
	    int[] pixels = (int[])ip.getPixels();
		int width = ip.getWidth();
		int height = ip.getHeight();
		java.awt.Color color = new Color(134,50,42);
		int value = ((197 & 0xff)<<16)+((99 & 0xff)<<8) + (88 & 0xff);
		int black = ((0 & 0xff)<<16)+((0 & 0xff)<<8) + (0 & 0xff);

		StringBuffer sb = new StringBuffer();

		int i = 0;
		sb.append(i+"\t"+value+"\n");
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color pixelValue = processor.getColor(x,y);
				sb.append(i+"\t"+pixelValue+"\n");
				if(pixelValue.equals(color)){
					//pixels[i] = ((197 & 0xff)<<16)+((99 & 0xff)<<8) + (88 & 0xff);
					processor.putPixel(x,y, new int[]{197,99,88});
				}
				else{
					processor.putPixel(x,y, new int[]{0,0,0});
				}
				i++;
				//ip.putPixelValue(x,y, 0);
			}
		}
		        new ij.text.TextWindow("Histogram", "Value\tCount", new String(sb), 300, 400);

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

