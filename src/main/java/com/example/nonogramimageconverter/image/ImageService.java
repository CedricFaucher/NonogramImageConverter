package com.example.nonogramimageconverter.image;

import com.example.nonogramimageconverter.problem.ImageIOException;
import com.example.nonogramimageconverter.problem.WrongFormatException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static com.example.nonogramimageconverter.utils.BinaryConversion.convertIntegerToBinaryString;

@Service
public class ImageService {

    private final double RED_WEIGHT = 0.299;
    private final double GREEN_WEIGHT = 0.587;
    private final double BLUE_WEIGHT = 0.114;
    private int width = 0;
    private int height = 0;
    private int otsus = 0;

    // Function that will take an image and returns a gray scale image.
    public void produceGrayScaleImage(MultipartFile image) {
        List<Color> colorList = getColorListFromImage(image);
        List<Color> grayScale = new ArrayList<>();

        colorList.forEach(color -> {
            double red = color.getRed() * RED_WEIGHT;
            double green = color.getGreen() * GREEN_WEIGHT;
            double blue = color.getBlue() * BLUE_WEIGHT;

            int gray = (int) Math.round(red + green + blue);
            grayScale.add(new Color(gray, gray, gray));
        });

        BufferedImage img = getImageFromColorList(grayScale);

        File file = new File("grayScaleResult.png");

        try {
            ImageIO.write(img, "png", file);
        } catch (IOException e) {
            throw new ImageIOException("There was an issue while trying to create the new grayscale image");
        }
    }

    // Function that will take an image and an optional otsusVariable and returns a black and white image.
    public void produceBlackAndWhiteImage(MultipartFile image, Optional<Integer> otsusVariable) {
        List<Color> colorList = getColorListFromImage(image);
        List<Integer> computableGrayScale = new ArrayList<>();

        colorList.forEach(color -> {
            double red = color.getRed() * RED_WEIGHT;
            double green = color.getGreen() * GREEN_WEIGHT;
            double blue = color.getBlue() * BLUE_WEIGHT;

            int gray = (int) Math.round(red + green + blue);
            computableGrayScale.add(gray);
        });

        BufferedImage img = getBlackAndWhiteImageFromGrayScaleList(computableGrayScale, otsusVariable);

        File file = new File("blackAndWhiteResult.png");

        try {
            ImageIO.write(img, "png", file);
        } catch (IOException e) {
            throw new ImageIOException("There was an issue while trying to create the new black and white image");
        }
    }

    // Function that will take in an image and an optional Otsu's variable and that will output a string of zeroes and ones
    // It works only for image up to a resolution of 256x256
    public ImageAsString getStringFromImage(MultipartFile image, Optional<Integer> otsusVariable, Integer shrinkAmount, Boolean inverse) {
        List<Color> colorList = getColorListFromImage(image);
        List<Integer> computableGrayScale = new ArrayList<>();

        if (width > 256 && height > 256) {
            throw new WrongFormatException("Wrong resolution for image. It should be maximum 256x256");
        }

        colorList.forEach(color -> {
            double red = color.getRed() * RED_WEIGHT;
            double green = color.getGreen() * GREEN_WEIGHT;
            double blue = color.getBlue() * BLUE_WEIGHT;

            int gray = (int) Math.round(red + green + blue);
            computableGrayScale.add(gray);
        });

        String zeroesAndOnes = getStringOfZeroesAndOnesFromGrayScaleList(computableGrayScale, otsusVariable, shrinkAmount, inverse);
        String widthInBinary = convertIntegerToBinaryString(width);
        String heightInBinary = convertIntegerToBinaryString(height);

        String imageAsString = "0".repeat(9 - widthInBinary.length()) +
                widthInBinary +
                "0".repeat(9 - heightInBinary.length()) +
                heightInBinary +
                zeroesAndOnes;

        return ImageAsString.builder()
                .image(imageAsString)
                .width(width)
                .height(height)
                .otsusVariable(otsus)
                .shrinkAmount(shrinkAmount)
                .inverse(inverse)
                .build();
    }

    // Function that will take an image and returns a list of colors from the image.
    // It will also set up global height and width variables.
    private List<Color> getColorListFromImage(MultipartFile image) {
        BufferedImage img;
        try {
            img = ImageIO.read(image.getInputStream());
        } catch (IOException e) {
            throw new ImageIOException("There was an issue while trying to read the image provided");
        }

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
        otsus = otsusVariable.orElseGet(() -> otsusMethod(grayScales));

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

    // Function that takes a list of grayScale represented by integers and returns a string of zeroes and ones.
    // This function also takes an optional otsusVariable that can alter the weight for the black vs white.
    private String getStringOfZeroesAndOnesFromGrayScaleList(List<Integer> grayScales, Optional<Integer> otsusVariable,
                                                             Integer shrinkAmount, Boolean inverse) {
        List<Integer> shrinkedList = new ArrayList<>(grayScales);

        for (int i = 0; i < shrinkAmount; i++) {
            shrinkedList = shrinkImage(shrinkedList);
        }

        List<Integer> finalShrinkedList = shrinkedList;
        otsus = otsusVariable.orElseGet(() -> otsusMethod(finalShrinkedList));

        StringBuilder zeroesAndOnes = new StringBuilder();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int gray = finalShrinkedList.get((j * height) + i);
                if (gray >= otsus) {
                    zeroesAndOnes.append(inverse ? "1" : "0");
                } else {
                    zeroesAndOnes.append(inverse ? "0" : "1");
                }
            }
        }

        return zeroesAndOnes.toString();
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

    // Function that takes a list of integers (grayScale) and apply a bilinear interpolation algorithm to reduce
    // the image by four
    private List<Integer> shrinkImage(List<Integer> colors) {
        if (width % 2 != 0 || height % 2 != 0) {
            throw new WrongFormatException("Format right now needs to be even * even after shrink");
        }
        List<Integer> shrinkedList = new ArrayList<>();
        for (int i = 0; i < width; i += 2) {
            for (int j = 0; j < height; j += 2) {
                double mean = Math.round(
                        (colors.get((i * height) + j) +
                                colors.get((i * height) + (j + 1)) +
                                colors.get(((i + 1) * height) + j) +
                                colors.get(((i + 1) * height) + (j + 1))) / 4.0);
                shrinkedList.add((int) mean);
            }
        }

        width /= 2;
        height /= 2;

        return shrinkedList;
    }
}
