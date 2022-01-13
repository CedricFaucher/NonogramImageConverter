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

    public List<Color> getColorsFromImage() {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("/Users/cfaucher/Dev/NonogramImageConverter/src/main/resources/turtle.jpg"));
        } catch (IOException e) {
            System.out.println("pow");
        }
        assert img != null;

        List<Color> colorList = new ArrayList<>();
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                colorList.add(new Color(img.getRGB(i, j)));
            }
        }

        List<Color> grayScale = new ArrayList<>();
        List<Integer> computableGaryScale = new ArrayList<>();
        colorList.forEach(color -> {
            double red = color.getRed() * 0.299;
            double green = color.getGreen() * 0.587;
            double blue = color.getBlue() * 0.114;

            int gray = (int) Math.round(red + green + blue);
            computableGaryScale.add(gray);
            grayScale.add(new Color(gray, gray, gray));
        });

        int p = otsusMethod(computableGaryScale, img.getWidth() * img.getHeight());

        List<Color> baw = new ArrayList<>();

        computableGaryScale.forEach(gray -> {
            if (gray >= p) {
                baw.add(new Color(255, 255, 255));
            } else {
                baw.add(new Color(0, 0, 0));
            }
        });

        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = newImg.createGraphics();

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                g2d.setColor(baw.get((i * img.getHeight()) + j));
                g2d.drawLine(i, j, i, j);
            }
        }

        g2d.dispose();
        File file = new File("result.png");

        try {
            ImageIO.write(newImg, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    private int otsusMethod(List<Integer> grayScale, int numberOfPixels) {
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
