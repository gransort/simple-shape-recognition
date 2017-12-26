import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.*;

import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;
import java.util.List;


public class ShapeRecognizer {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {

        Mat shapeTemplate;
        Mat shapeCultivated;

        shapeTemplate = Imgcodecs.imread("./shapes/");
        shapeCultivated = Imgcodecs.imread("./shapes/");

        Mat shapeGrayTemplate = new Mat();
        Mat shapeGrayCultivated = new Mat();
        Mat shapeDstTemplate = new Mat();
        Mat shapeDstCultivated = new Mat();

        Imgproc.cvtColor(shapeTemplate, shapeGrayTemplate, COLOR_RGB2GRAY);
        Imgproc.cvtColor(shapeCultivated, shapeGrayCultivated, COLOR_RGB2GRAY);
        Imgproc.Canny(shapeGrayTemplate, shapeDstTemplate, 100, 600);
        Imgproc.Canny(shapeGrayCultivated, shapeDstCultivated, 100, 600);
        //Imgcodecs.imwrite("GRAYcultivated.jpg", shapeGrayCultivated);
        //Imgcodecs.imwrite("DSTcultivated.jpg", shapeDstCultivated);

        List<MatOfPoint> ShapeContoursTemplate = new ArrayList<>();
        List<MatOfPoint> ShapeContoursCultivated = new ArrayList<>();

        Imgproc.findContours(shapeDstTemplate, ShapeContoursTemplate, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.findContours(shapeDstCultivated, ShapeContoursCultivated, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);


        double matchValue = 0;
        for (MatOfPoint matOfPoint : ShapeContoursTemplate) {
            Mat image1 = matOfPoint.t();
            for (MatOfPoint matOfPoint2 : ShapeContoursCultivated) {
                Mat image2 = matOfPoint2.t();
                matchValue += Imgproc.matchShapes(image1, image2, Imgproc.CV_CONTOURS_MATCH_I3, 0);
            }
        }

        System.out.println("Contours in template: " + ShapeContoursTemplate.size());
        System.out.println("Contours in cultivated: " + ShapeContoursCultivated.size());
        System.out.println("\nSimilarity: " + matchValue);
    }
}