package com.example.nonogramimageconverter.image;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
public class ImageController {
    private final ImageService service;

    public ImageController(ImageService service) {
        this.service = service;
    }

    @GetMapping("/image/grayScale")
    public void produceGrayScaleImage(@RequestParam("image") MultipartFile image) {
        service.produceGrayScaleImage(image);
    }

    @GetMapping("/image/blackAndWhite")
    public void produceBlackAndWhiteImage(@RequestParam("image") MultipartFile image,
                                          @RequestParam(value = "otsusVariable", required = false) Optional<Integer> otsusVariable) {
        service.produceBlackAndWhiteImage(image, otsusVariable);
    }

    // 9 bits for width, 9 bits for height, rest for image
    @GetMapping("/image/string")
    public String getStringFromImage(@RequestParam("image") MultipartFile image,
                                     @RequestParam(value = "otsusVariable", required = false) Optional<Integer> otsusVariable,
                                     @RequestParam(value = "shrinkAmount", defaultValue = "0") Integer shrinkAmount,
                                     @RequestParam(value = "inverse", defaultValue = "false") Boolean inverse) {
        return service.getStringFromImage(image, otsusVariable, shrinkAmount, inverse);
    }
}
