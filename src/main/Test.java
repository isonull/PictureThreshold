package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Test {
	public static void main(String[] args) throws IOException {
		// /Users/zijunyan/Desktop/JAVAWORKDIR/PictureThreadhold/testImage/IMG_1006.JPG

		BufferedImage image = Thresholder
				.readImage("/Users/zijunyan/Desktop/JAVAWORKDIR/PictureThreadhold/testImage/IMG_1006.JPG");

		Thresholder.imageRGBtoBinary(image, 64, 13);

		ImageIO.write(image, "png",
				new File("/Users/zijunyan/Desktop/JAVAWORKDIR/PictureThreadhold/testOutput/IMG_1006.png"));
	}
}
