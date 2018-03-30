package com.ocr.demo;

import org.apache.commons.io.FilenameUtils;
import org.bytedeco.javacpp.IntPointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

@Component
public class ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    private final ImageLoader imageLoader;
    private final GrayScale grayScale;
    private final TextService textService;
    private final TemplateService templateService;
    private final String outputDir;

    ImageService(
            ImageLoader imageLoader,
            GrayScale grayScale,
            TextService textService,
            TemplateService templateService,
            @Value("${output.dir}") String outputDir) {
        this.imageLoader = imageLoader;
        this.grayScale = grayScale;
        this.textService = textService;
        this.templateService = templateService;
        this.outputDir = outputDir;
    }

    public Mat grayScale(Mat image) {
        Mat grayMat = new Mat();
        cvtColor(image, grayMat, COLOR_RGB2GRAY);
        //adaptiveThreshold(grayMat, grayMat, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 11, 2);
        return grayMat;
    }

    public void process() {
        imageLoader.list().stream().forEach(
            imageFile -> {
                String imagePath = imageFile.getAbsolutePath();
                LOGGER.info("Image={}", imagePath);
                Mat imageMat = grayScale(imread(imagePath));
                String fileName = FilenameUtils.concat(
                        outputDir,
                        FilenameUtils.getBaseName(imageFile.getName()) + "_" + System.currentTimeMillis() + "_%s" + ".png"
                );

                if (imagePath.toUpperCase().contains("CNH")) {
                    handleCNH(fileName, imageMat);
                } else if (imagePath.toUpperCase().contains("RG")) {
                    handleRG(fileName, imageMat);
                }

            }
        );
    }

    private void handleRG(String fileName, Mat imageMat) {

        String rg = extractText(
                String.format(fileName, "rg_rg"),
                imageMat,
                grayScale(imread("templates/rg/rg_rg.png")),
                TM_SQDIFF, 120, -10, 240, 0);
        LOGGER.info("RG={}", rg.replaceAll(" ", ""));

        String name = extractText(
                String.format(fileName, "rg_name"),
                imageMat,
                grayScale(imread("templates/rg/rg_name.png")),
                TM_SQDIFF, 100, -30, 630, 0);
        LOGGER.info("Name={}", name);

        String parents = extractText(
                String.format(fileName, "rg_parents"),
                imageMat,
                grayScale(imread("templates/rg/rg_parents.png")),
                TM_SQDIFF, 100, -30, 630, 130);
        LOGGER.info("Parents={}", parents);

        String cpf = extractText(
                String.format(fileName, "rg_cpf"),
                imageMat,
                grayScale(imread("templates/rg/rg_cpf.png")),
                TM_SQDIFF, 45, -10, 295, 10);
        LOGGER.info("CPF={}", cpf);

    }

    private void handleCNH(String fileName, Mat imageMat) {

        String name = extractText(
                String.format(fileName, "cnh_name"),
                imageMat,
                grayScale(imread("templates/cnh/cnh_name.png")),
                TM_SQDIFF, 0, 15, -50, 25);
        LOGGER.info("Name={}", name);

        String rg = extractText(
                String.format(fileName, "cnh_rg"),
                imageMat,
                grayScale(imread("templates/cnh/cnh_rg.png")),
                TM_SQDIFF, 10, 20, -50, 15);
        LOGGER.info("RG={}", rg.replaceAll(" ", ""));

        String cpf = extractText(
                String.format(fileName, "cnh_cpf"),
                imageMat,
                grayScale(imread("templates/cnh/cnh_cpf.png")),
                TM_SQDIFF, 5, 20, 0, 15);
        LOGGER.info("CPF={}", cpf.replaceAll("[^0-9\\.\\-]", ""));

        String dob = extractText(
                String.format(fileName, "cnh_dob"),
                imageMat,
                grayScale(imread("templates/cnh/cnh_dob.png")),
                TM_SQDIFF, 5, 20, 0, 10);
        LOGGER.info("DoB={}", dob);

        String parents = extractText(
                String.format(fileName, "cnh_parents"),
                imageMat,
                grayScale(imread("templates/cnh/cnh_parents.png")),
                TM_SQDIFF, 10, 30, -20, 120);
        LOGGER.info("Parents={}", parents);

        String doe = extractText(
                String.format(fileName, "cnh_doe"),
                imageMat,
                grayScale(imread("templates/cnh/cnh_doe.png")),
                TM_SQDIFF, 5, 20, 0, 10);
        LOGGER.info("DoE={}", doe);

    }

    private String extractText(String resultFileName, Mat imageMat, Mat templateMat,
                               int matchMethod, int x, int y, int width, int height) {
        Mat imageMatched = templateService.match(imageMat, templateMat, TM_SQDIFF, x, y, width, height);
        imwrite(resultFileName, imageMatched, new IntPointer(CV_IMWRITE_PNG_COMPRESSION));
        return textService.extract(resultFileName);
    }

}
