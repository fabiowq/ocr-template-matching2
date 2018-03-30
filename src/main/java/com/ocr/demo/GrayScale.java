package com.ocr.demo;


import magick.MagickImage;
import org.bytedeco.javacpp.opencv_core;
import org.springframework.stereotype.Component;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_core.Size;
import static org.bytedeco.javacpp.opencv_imgproc.*;

@Component
public class GrayScale {

    public Mat rescale(Mat image) {
        cvtColor(image, image, COLOR_RGB2GRAY);
        //GaussianBlur(image, image,new Size(3, 3),0);
        //adaptiveThreshold(image, image, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY, 99, 4);

//        resize(image, image, new Size(1191, 1600), 0, 0, INTER_LINEAR);
//
//
//        IplImage orig = new IplImage(image);
//
//        CvRect r = new CvRect(160, 180, 600, 50);
//
//        cvSetImageROI(orig, r);
//        IplImage cropped = cvCreateImage(cvGetSize(orig), orig.depth(), orig.nChannels());
//        cvCopy(orig, cropped);
//        return cvarrToMat(cropped);

        return image;

    }

}
