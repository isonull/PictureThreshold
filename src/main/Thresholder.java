package main;

import java.awt.Image;
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
	static public int pixelRGBtoGrey(int RGB) {

		return (int) (((RGB & 0x00FF0000) >>> 16) * 0.21 + ((RGB & 0x0000FF00) >>> 8) * 0.72
				+ (RGB & 0x000000FF) * 0.07);
	}

	static public Image imageRGBtoGrey(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		int[] pixelArray = image.getRGB(0, 0, width, height, null, 0, width);
		int tt;
		for (int i = 0; i < width * height; ++i) {
			tt = pixelRGBtoGrey(pixelArray[i]);
			pixelArray[i] = tt + (tt << 8) + (tt << 16);
		}
		image.setRGB(0, 0, width, height, pixelArray, 0, width);
		return image;
	}

	static public BufferedImage imageRGBtoBinary(BufferedImage image, int minDif, int div) throws IOException {
		int width = image.getWidth();
		int height = image.getHeight();
		int step = width / div;
		int[] pixelArray;
		pixelArray = image.getRGB(0, 0, width, height, null, 0, width);
		// ImageFilter filter = new gray
		for (int i = 0; i < width * height; ++i) {
			pixelArray[i] = pixelRGBtoGrey(pixelArray[i]);
		}

		int max = 0;
		int min = 255;
		int bd = 128;
		int base = 0;
		int limit = 0;
		for (int row = 0; row < height; ++row) {
			for (int i = 0; i < div - 1; ++i) {
				max = 0;
				min = 255;
				base = width * row + i * step;
				for (int j = base; j < base + step; ++j) {
					max = pixelArray[j] > max ? pixelArray[j] : max;
					min = pixelArray[j] < min ? pixelArray[j] : min;
				}
				bd = (max + min) / 2;
				if (max - min > minDif) {
					for (int j = base; j < base + step; ++j) {
						pixelArray[j] = pixelArray[j] > bd ? 0xFFFFFFFF : 0x00000000;
					}
				} else {
					for (int j = base; j < base + step; ++j) {
						pixelArray[j] = 0xFFFFFFFF;
					}
				}
			}

			base = row * width + (div - 1) * step;
			limit = (row + 1) * width;
			for (int j = base; j < limit; ++j) {
				max = pixelArray[j] > max ? pixelArray[j] : max;
				min = pixelArray[j] < min ? pixelArray[j] : min;
			}
			bd = (max + min) / 2;

			if (max - min > minDif) {
				for (int j = base; j < limit; ++j) {
					pixelArray[j] = pixelArray[j] > bd ? 0xFFFFFFFF : 0x00000000;
				}
			} else {
				for (int j = base; j < limit; ++j) {
					pixelArray[j] = 0xFFFFFFFF;
				}
			}
		}

		image.setRGB(0, 0, width, height, pixelArray, 0, width);
		return image;
	}

}
