package com.example.nonogramimageconverter.image;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

@Service
public class ImageService {

    private final double RED_WEIGHT = 0.299;
    private final double GREEN_WEIGHT = 0.587;
    private final double BLUE_WEIGHT = 0.114;
    private int width = 0;
    private int height = 0;

    // Function that will take an image and returns a gray scale image.
    public void produceGrayScaleImage() {
        List<Color> colorList = getColorListFromImage();
        List<Color> grayScale = new ArrayList<>();

        colorList.forEach(color -> {
            double red = color.getRed() * RED_WEIGHT;
            double green = color.getGreen() * GREEN_WEIGHT;
            double blue = color.getBlue() * BLUE_WEIGHT;

            int gray = (int) Math.round(red + green + blue);
            grayScale.add(new Color(gray, gray, gray));
        });

        BufferedImage image = getImageFromColorList(grayScale);

        File file = new File("grayScaleResult.png");

        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Function that will take an image and returns a black and white image.
    public void produceBlackAndWhiteImage() {
        List<Color> colorList = getColorListFromImage();
        List<Integer> computableGrayScale = new ArrayList<>();

        colorList.forEach(color -> {
            double red = color.getRed() * RED_WEIGHT;
            double green = color.getGreen() * GREEN_WEIGHT;
            double blue = color.getBlue() * BLUE_WEIGHT;

            int gray = (int) Math.round(red + green + blue);
            computableGrayScale.add(gray);
        });

        BufferedImage image = getBlackAndWhiteImageFromGrayScaleList(computableGrayScale, Optional.empty());

        File file = new File("blackAndWhiteResult.png");

        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Function that will take an image and returns a list of colors from the image.
    // It will also set up global height and width variables.
    private List<Color> getColorListFromImage() {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("/Users/cfaucher/Dev/NonogramImageConverter/src/main/resources/turtle.jpg"));
        } catch (IOException e) {
            System.out.println("pow");
        }
        assert img != null;

        width = img.getWidth();
        height = img.getHeight();

        List<Color> colorList = new ArrayList<>();
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                colorList.add(new Color(img.getRGB(i, j)));
            }
        }

        return colorList;
    }

    // Function that takes a list of colors and returns an image from that list
    private BufferedImage getImageFromColorList(List<Color> colors) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                g2d.setColor(colors.get((i * height) + j));
                g2d.drawLine(i, j, i, j);
            }
        }

        g2d.dispose();
        return image;
    }

    // Function that takes a list of grayScale represented by integers and returns an image from that list.
    // This function also takes an optional otsusVariable that can alter the weight for the black vs white.
    private BufferedImage getBlackAndWhiteImageFromGrayScaleList(List<Integer> grayScales, Optional<Integer> otsusVariable) {
        int otsus = otsusVariable.orElseGet(() -> otsusMethod(grayScales));

        List<Color> blackAndWhiteColors = new ArrayList<>();
        grayScales.forEach(gray -> {
            if (gray >= otsus) {
                blackAndWhiteColors.add(new Color(255, 255, 255));
            } else {
                blackAndWhiteColors.add(new Color(0, 0, 0));
            }
        });

        return getImageFromColorList(blackAndWhiteColors);
    }

    // Function that takes a list of grayScale represented by integers and returns the result of the Otsu's method.
    // The Otsu's method is using the histogram of the image to calculate the value defining the background and foreground
    // of a grayScale image.
    private int otsusMethod(List<Integer> grayScale) {
        int numberOfPixels = width * height;

        // Create histogram
        List<Integer> histogram = new ArrayList<>(Collections.nCopies(256, 0));
        grayScale.forEach(pixel -> histogram.set(pixel, histogram.get(pixel) + 1));

        // Get threshold for each value in histogram
        int highestGrayValue = 0;
        double highestThreshold = 0;

        for (int i = 1; i < 255; i++) {
            double wb = 0;
            double wf = 0;
            double ub = 0;
            double uf = 0;

            for (int b = 0; b < i; b++) {
                wb += histogram.get(b);
                ub += histogram.get(b) * b;
            }
            ub /= wb;
            wb /= numberOfPixels;

            for (int f = i; f <= 255; f++) {
                wf += histogram.get(f);
                uf += histogram.get(f) * f;
            }
            uf /= wf;
            wf /= numberOfPixels;

            double ob2 = wb * wf * Math.pow(ub - uf, 2);

            if (ob2 > highestThreshold) {
                highestThreshold = ob2;
                highestGrayValue = i;
            }
        }

        return highestGrayValue;
    }
}
