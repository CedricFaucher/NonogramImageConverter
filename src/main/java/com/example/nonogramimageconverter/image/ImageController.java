package com.example.nonogramimageconverter.image;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
    public void produceBlackAndWhiteImage(@RequestParam(value = "otsusVariable", required = false) Optional<Integer> otsusVariable) {
        service.produceBlackAndWhiteImage(otsusVariable);
    }
}
