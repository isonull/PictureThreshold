package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Thresholder {

	static public BufferedImage readImage(String path) {
		try {
			return ImageIO.read(new File(path));
		} catch (Exception e) {
			System.err.println("ERROR IN: ImageIO.read(new File(" + path + "))");
			return null;
		}
	}

	static public BufferedImage readImage(File imageFile) {
		try {
			return ImageIO.read(imageFile);
		} catch (Exception e) {
			System.err.println("ERROR IN: ImageIO.read(imageFile)");
			return null;
		}
	}

	// 0.21 R + 0.72 G + 0.07 B
	static public int pixelRGBtoGreyScale(int RGB) {
		return (int) (((RGB & 0x00FF0000) >>> 16) * 0.21 + ((RGB & 0x0000FF00) >>> 8) * 0.72
				+ (RGB & 0x000000FF) * 0.07);
	}

	static public BufferedImage imageRGBtoBinary(BufferedImage image, int mindif, int div) throws IOException {
		int width = image.getWidth();
		int height = image.getHeight();
		int step = (int) Math.ceil((((double) width) / div));
		int scansize = step * div;
		int[] pixelArray;
		pixelArray = image.getRGB(0, 0, width, height, null, 0, scansize);
		// ImageFilter filter = new gray
		for (int i = 0; i < width * height; ++i) {
			pixelArray[i] = pixelRGBtoGreyScale(pixelArray[i]);
		}

		int max = 0;
		int min = 255;
		int mindiff = mindif;
		int bd = 128;

		for (int i = 0; i < width * height; i += step) {
			max = 0;
			min = 255;
			for (int j = i; j < i + step; ++j) {
				max = Math.max(max, pixelArray[j]);
				min = Math.min(min, pixelArray[j]);
			}
			bd = (max + min) / 2;
			if (max - min > mindiff) {
				for (int j = i; j < i + step; ++j) {
					pixelArray[j] = pixelArray[j] > bd ? 0xFFFFFFFF : 0x00000000;
				}
			} else {
				for (int j = i; j < i + step; ++j) {
					pixelArray[j] = 0xFFFFFFFF;
				}
			}
		}
		image.setRGB(0, 0, width, height, pixelArray, 0, scansize);
		return image;
	}
}
