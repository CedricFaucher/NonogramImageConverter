package com.example.nonogramimageconverter.image;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {
    private final ImageService service;

    public ImageController(ImageService service) {
        this.service = service;
    }

    @GetMapping("/image/grayScale")
    public void produceGrayScaleImage() {
        service.produceGrayScaleImage();
    }

    @GetMapping("/image/blackAndWhite")
    public void produceBlackAndWhiteImage() {
        service.produceBlackAndWhiteImage();
    }
}
