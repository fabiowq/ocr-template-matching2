package com.ocr.demo;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.bytedeco.javacpp.lept.*;
import static org.bytedeco.javacpp.lept.pixRead;
import static org.bytedeco.javacpp.tesseract.*;

@Component
public class TextService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextService.class);

    public String extract(String imagePath) {
        TessBaseAPI tesseractAPI = new TessBaseAPI();
        // Initialize tesseract-ocr with English, without specifying tessdata path
        if (tesseractAPI.Init("./src/main/resources", "por") != 0) {
            RuntimeException e = new RuntimeException("Could not initialize tesseract");
            LOGGER.error("Error initializing tesseract", e);
            throw e;
        }
        // Open input image with leptonica library
        PIX image = pixRead(imagePath);
        tesseractAPI.SetImage(image);
        // Get OCR result
        BytePointer outText = tesseractAPI.GetUTF8Text();

        String ocrOutputString = outText.getString().trim();
        //LOGGER.info("OCR output:\n{}", ocrOutputString);

        // Destroy used object and release memory
        tesseractAPI.End();
        outText.deallocate();
        pixDestroy(image);

        return ocrOutputString;

    }


}
