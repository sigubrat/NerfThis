import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import nu.pattern.OpenCV;

public class NerfThis {
	public static void main(String[] args) {
		System.out.println("Test");
		OpenCV.loadShared();
		
		String sourceImagePath = "src/main/resources/face.jpeg";
		String targetImagePath = "src/main/resources/result.jpeg";
		

		Mat loadedImage = loadImage(sourceImagePath);
		int minFaceSize = Math.round(loadedImage.rows() * 0.1f); 
		MatOfRect facesDetected = new MatOfRect();
		CascadeClassifier cascadeClassifier = new CascadeClassifier();
		cascadeClassifier.load("./src/main/resources/haarcascades/haarcascade_frontalface_alt.xml");
		cascadeClassifier.detectMultiScale(loadedImage, 
  			facesDetected, 
  			1.1, 
  			3, 
		  Objdetect.CASCADE_SCALE_IMAGE, 
		  new Size(minFaceSize, minFaceSize), 
		  new Size() 
		);
		
		Rect[] facesArray = facesDetected.toArray(); 
		for(Rect face : facesArray) { 
			Imgproc.rectangle(loadedImage, face.tl(), face.br(), new Scalar(0, 0, 255), 3); 
		} 
		saveImage(loadedImage, targetImagePath);

	}

	public static Mat loadImage(String imagePath) {
		Imgcodecs imageCodecs = new Imgcodecs();
		return imageCodecs.imread(imagePath);
	}

	public static void saveImage(Mat imageMatrix, String targetPath) {
    	Imgcodecs imgcodecs = new Imgcodecs();
    	imgcodecs.imwrite(targetPath, imageMatrix);
	}
}
