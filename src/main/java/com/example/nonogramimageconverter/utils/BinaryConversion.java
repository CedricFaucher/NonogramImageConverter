package com.example.nonogramimageconverter.utils;

public class BinaryConversion {
    // Convert a base 10 integer into a base 2 string
    public static String convertIntegerToBinaryString(int value) {
        StringBuilder result = new StringBuilder();
        double updatedValue = value;

        while (updatedValue > 0) {
            result.insert(0, updatedValue % 2 == 0 ? "0" : "1");
            updatedValue = Math.floor(updatedValue / 2.0);
        }

        return result.toString();
    }
}
