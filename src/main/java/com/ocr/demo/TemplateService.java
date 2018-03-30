package com.ocr.demo;

import org.bytedeco.javacpp.DoublePointer;
import org.springframework.stereotype.Component;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.matchTemplate;
import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

@Component
public class TemplateService {

    public Mat match(Mat img, Mat template, int matchMethod, int x, int y, int width, int height) {

        Size size = new Size(img.cols() - template.cols() + 1,
                img.rows() - template.rows() + 1);
        Mat result = new Mat(size, CV_32FC1);
        matchTemplate(img, template, result, TM_CCORR_NORMED);

        DoublePointer minVal = new DoublePointer();
        DoublePointer maxVal = new DoublePointer();
        Point min = new Point();
        Point max = new Point();
        minMaxLoc(result, minVal, maxVal, min, max, null);

        Rect rectCrop = new Rect(max.x() + x, max.y() + y, template.cols() + width, template.rows() + height);
        Mat croppedImage = new Mat(img, rectCrop);

        return croppedImage;

    }

}
