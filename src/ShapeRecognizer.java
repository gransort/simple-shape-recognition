import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;

public class ShapeRecognizer {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        String shapeOnCultivatedImage;
        Mat shapeCultivated;
        shapeCultivated = Imgcodecs.imread("./shapes/circle.jpg");
        Mat shapeGrayCultivated = new Mat();
        Mat shapeDstCultivated = new Mat();
        Imgproc.cvtColor(shapeCultivated, shapeGrayCultivated, COLOR_RGB2GRAY);
        Imgproc.Canny(shapeGrayCultivated, shapeDstCultivated, 50, 500);
        List<MatOfPoint> shapeContoursCultivated = new ArrayList<>();
        Imgproc.findContours(shapeDstCultivated, shapeContoursCultivated, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

//        Imgcodecs.imwrite("GRAYcultivated.jpg", shapeGrayCultivated);
//        Imgcodecs.imwrite("DSTcultivated.jpg", shapeDstCultivated);

        double max_area = -1;
        int index = 0;
        for (int i = 0; i < shapeContoursCultivated.size(); i++) {
            if (Imgproc.contourArea(shapeContoursCultivated.get(i)) > max_area) {
                max_area = Imgproc.contourArea(shapeContoursCultivated.get(i));
                index = i;
            }
        }

        MatOfPoint2f approxCurve = new MatOfPoint2f();
        MatOfPoint2f oriCurve = new MatOfPoint2f(shapeContoursCultivated.get(index).toArray());
        Imgproc.approxPolyDP(oriCurve, approxCurve, 6.0, true);

        Point[] array = approxCurve.toArray();
//        for (int i = 0; i < array.length; i++) {
//            System.out.println(array[i]);
//        }
//        System.out.println(array.length);

        if (array.length == 4) {
            double aspectRatio = (array[1].x - array[0].x) / (array[3].y - array[0].y);
            System.out.println(aspectRatio);
            if ((aspectRatio >= 0.95) && (aspectRatio <= 1.05)) {
                shapeOnCultivatedImage = "square";
            } else if (aspectRatio < -10) {
                shapeOnCultivatedImage = "trapeze";
            } else if (((array[1].x - array[0].x < 5) && (array[1].x - array[0].x >= 0)) || ((array[2].x - array[1].x < 5)) && (array[2].x - array[1].x >= 0)) {
                shapeOnCultivatedImage = "rectangle";
            } else {
                shapeOnCultivatedImage = "rhombus";
            }

        } else if (array.length == 3) {
            shapeOnCultivatedImage = "triangle";
        } else {
            double maxX = 0;
            double minX = 10000;
            double maxY = 0;
            double minY = 10000;
            double average;
            for (Point anArray : array) {
                if (anArray.x > maxX) {
                    maxX = anArray.x;
                }
                if (anArray.x < minX) {
                    minX = anArray.x;
                }
                if (anArray.y > maxY) {
                    maxY = anArray.y;
                }
                if (anArray.y < minY) {
                    minY = anArray.y;
                }
            }
            average = ((maxX - minX) - (maxY - minY));
            if (average == 0) {
                shapeOnCultivatedImage = "circle";
            } else {
                shapeOnCultivatedImage = "ellipse";
            }
        }
        System.out.println(shapeOnCultivatedImage);
    }
}