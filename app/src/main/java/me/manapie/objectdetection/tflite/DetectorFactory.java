package me.manapie.objectdetection.tflite;

import android.content.res.AssetManager;

import java.io.IOException;

public class DetectorFactory {
    public static YoloV5Classifier getDetector(
            final AssetManager assetManager,
            final String modelFilename)
            throws IOException {
        String labelFilename = "file:///android_asset/coco.txt";
        boolean isQuantized = false;
        int inputSize = 640;

        if (modelFilename.contains("int8")) {
            isQuantized = true;
        }

        if (modelFilename.contains("img320")) {
            inputSize = 320;
        } else if (modelFilename.contains("img480")) {
            inputSize = 480;
        } else if (modelFilename.contains("img640")) {
            inputSize = 640;
        } else if (modelFilename.contains("img768")) {
            inputSize = 768;
        } else if (modelFilename.contains("img960")) {
            inputSize = 960;
        }

        return YoloV5Classifier.create(assetManager, modelFilename, labelFilename, isQuantized,
                inputSize);
    }

}
