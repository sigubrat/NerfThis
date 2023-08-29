import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import nu.pattern.OpenCV;

public class NerfThis extends Application {
	public static void main(String[] args) {
		OpenCV.loadLocally();
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		detectFaceFromWebcam(primaryStage);
	}

	public static void detectFaceFromWebcam(Stage stage) {
		VideoCapture capture = new VideoCapture(0);
		ImageView imageView = new ImageView();
		HBox hbox = new HBox(imageView);
		Scene scene = new Scene(hbox);
		Platform.runLater(() -> {
			stage.setScene(scene);
			stage.show();
		});


		new AnimationTimer() {
			@Override
			public void handle(long l) {
				imageView.setImage(getCapture(capture));
			}
		}.start();
	}

	public static void detectFaceImage() {
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
				new Size());

		Rect[] facesArray = facesDetected.toArray();
		for (Rect face : facesArray) {
			Imgproc.rectangle(loadedImage, face.tl(), face.br(), new Scalar(0, 0, 255), 3);
		}
		saveImage(loadedImage, targetImagePath);
	}

	public static Image getCapture(VideoCapture capture) {
		Mat mat = new Mat();
		capture.read(mat);
		return matToImg(mat);
	}

	public static Mat loadImage(String imagePath) {
		Imgcodecs imageCodecs = new Imgcodecs();
		return imageCodecs.imread(imagePath);
	}

	public static void saveImage(Mat imageMatrix, String targetPath) {
		Imgcodecs imgcodecs = new Imgcodecs();
		imgcodecs.imwrite(targetPath, imageMatrix);
	}

	public static Image matToImg(Mat mat) {
		MatOfByte bytes = new MatOfByte();
		Imgcodecs.imencode("img", mat, bytes);
		InputStream InputStream = new ByteArrayInputStream(bytes.toArray());
		return new Image(InputStream);
	}
}
