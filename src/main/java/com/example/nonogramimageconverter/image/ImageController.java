package com.example.nonogramimageconverter.image;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.List;

@RestController
public class ImageController {
    private final ImageService service;

    public ImageController(ImageService service) {
        this.service = service;
    }

    @GetMapping("/image")
    public List<Color> getColorsFromImage() {
        return service.getColorsFromImage();
    }
}
