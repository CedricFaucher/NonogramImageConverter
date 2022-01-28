package com.example.nonogramimageconverter.problem;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class APIError {
    private String message;
}
