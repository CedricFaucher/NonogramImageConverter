package com.example.nonogramimageconverter.image;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
// This DTO contains every informations about the image that is returned. The String image contains the width and height as 9 bits binaries.
// The rest is every pixels of the image as binary.
public class ImageAsString {
    private final String image;
    private final int width;
    private final int height;
    private final int otsusVariable;
    private final int shrinkAmount;
    private final boolean inverse;
}
