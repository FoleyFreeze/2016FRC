package org.usfirst.frc.team910.robot;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;
import com.ni.vision.NIVision.MeasurementType;
import com.ni.vision.NIVision.Rect;
import com.ni.vision.NIVision.ShapeMode;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.USBCamera;

public class VisionProcessor {
	int session;
	Image frame;
	Image binaryFrame;
	Timer time;

	// constants
	NIVision.Range HUE_RANGE = new NIVision.Range(0, 255);
	NIVision.Range SAT_RANGE = new NIVision.Range(0, 255);
	NIVision.Range VAL_RANGE = new NIVision.Range(40, 75);
	double VIEW_ANGLE = 60; // msft hd 3000
	int WHITE_BALANCE = 4000; // 3000 - 5300ish
	double EXPOSURE = 0.0;
	double BRIGHTNESS = 1.0;
	double AREA_MIN = 0.05;
	double AREA_MAX = 100.0;
	double TARGET_SCORE = 0.15;

	// filter criteria
	NIVision.ParticleFilterCriteria2 criteria[] = new NIVision.ParticleFilterCriteria2[1];
	NIVision.ParticleFilterOptions2 filterOptions = new NIVision.ParticleFilterOptions2(0, 0, 1, 1);

	VisionProcessor() {

	}

	public void setup() {
		frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_HSL, 0);
		binaryFrame = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
		Timer time = new Timer();
		time.start();
		time.reset();

		// setup filter by area criteria
		criteria[0] = new NIVision.ParticleFilterCriteria2(NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA, AREA_MIN,
				AREA_MAX, 0, 0);

		session = NIVision.IMAQdxOpenCamera("cam0", NIVision.IMAQdxCameraControlMode.CameraControlModeController);

		// configure settings
		NIVision.IMAQdxSetAttributeString(session, "CameraAttributes::WhiteBalance::Mode", "Manual");
		NIVision.IMAQdxSetAttributeI64(session, "CameraAttributes::WhiteBalance::Value", WHITE_BALANCE);
		NIVision.IMAQdxSetAttributeString(session, "CameraAttributes::Exposure::Mode", "Manual");
		long minv = NIVision.IMAQdxGetAttributeMinimumI64(session, "CameraAttributes::Exposure::Value");
		long maxv = NIVision.IMAQdxGetAttributeMaximumI64(session, "CameraAttributes::Exposure::Value");
		long val = minv + (long) (((double) (maxv - minv)) * (((double) EXPOSURE) / 100.0));
		NIVision.IMAQdxSetAttributeI64(session, "CameraAttributes::Exposure::Value", val);
		NIVision.IMAQdxSetAttributeString(session, "CameraAttributes::Brightness::Mode", "Manual");
		minv = NIVision.IMAQdxGetAttributeMinimumI64(session, "CameraAttributes::Brightness::Value");
		maxv = NIVision.IMAQdxGetAttributeMaximumI64(session, "CameraAttributes::Brightness::Value");
		val = minv + (long) (((double) (maxv - minv)) * (((double) BRIGHTNESS) / 100.0));
		NIVision.IMAQdxSetAttributeI64(session, "CameraAttributes::Brightness::Value", val);

		NIVision.IMAQdxConfigureGrab(session);
		NIVision.IMAQdxStartAcquisition(session);
	}

	public void disabled() {
		NIVision.IMAQdxGrab(session, frame, 1);
		CameraServer.getInstance().setImage(frame);
	}

	public void run() {
		NIVision.IMAQdxGrab(session, frame, 1);

		NIVision.imaqColorThreshold(binaryFrame, frame, 255, NIVision.ColorMode.HSV, HUE_RANGE, SAT_RANGE, VAL_RANGE);

		// total number of particles
		int numParticles = NIVision.imaqCountParticles(binaryFrame, 1);
		SmartDashboard.putNumber("Masked particles", numParticles);

		// CameraServer.getInstance().setImage(binaryFrame);

		// filter out small particles
		int imaqError = NIVision.imaqParticleFilter4(binaryFrame, binaryFrame, criteria, filterOptions, null);
		SmartDashboard.putNumber("error", imaqError);

		// Send particle count after filtering to dash board
		numParticles = NIVision.imaqCountParticles(binaryFrame, 1);
		SmartDashboard.putNumber("Filtered particles", numParticles);

		// search the targets for the best one, then see if it is good enough
		double smallestScore = 999;
		int bestParticle = 0;
		if (numParticles < 20 && numParticles > 0) {
			for (int i = 0; i < numParticles; i++) {
				double score = scoreParticle(i);
				if (score < smallestScore) {
					bestParticle = i;
					smallestScore = score;
				}
			}
		}

		// display best particle data
		if (smallestScore < TARGET_SCORE) {
			double area = NIVision.imaqMeasureParticle(binaryFrame, bestParticle, 0, NIVision.MeasurementType.MT_AREA);

			double cvh_area = NIVision.imaqMeasureParticle(binaryFrame, bestParticle, 0,
					NIVision.MeasurementType.MT_CONVEX_HULL_AREA);
			double cvh_area_ratio = area / cvh_area;

			double perimeter = NIVision.imaqMeasureParticle(binaryFrame, bestParticle, 0,
					NIVision.MeasurementType.MT_PERIMETER);
			double cvh_perimeter = NIVision.imaqMeasureParticle(binaryFrame, bestParticle, 0,
					NIVision.MeasurementType.MT_CONVEX_HULL_PERIMETER);
			double perimeter_ratio = perimeter / cvh_perimeter;

			double com_x = NIVision.imaqMeasureParticle(binaryFrame, bestParticle, 0,
					NIVision.MeasurementType.MT_CENTER_OF_MASS_X);
			double com_y = NIVision.imaqMeasureParticle(binaryFrame, bestParticle, 0,
					NIVision.MeasurementType.MT_CENTER_OF_MASS_Y);
			double top = NIVision.imaqMeasureParticle(binaryFrame, bestParticle, 0,
					NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
			double left = NIVision.imaqMeasureParticle(binaryFrame, bestParticle, 0,
					NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
			double width = NIVision.imaqMeasureParticle(binaryFrame, bestParticle, 0,
					NIVision.MeasurementType.MT_BOUNDING_RECT_WIDTH);
			double height = NIVision.imaqMeasureParticle(binaryFrame, bestParticle, 0,
					NIVision.MeasurementType.MT_BOUNDING_RECT_HEIGHT);
			double com_x_ratio = (com_x - left) / width;
			double com_y_ratio = (com_y - top) / height;

			SmartDashboard.putNumber("comX", com_x_ratio);
			SmartDashboard.putNumber("comY", com_y_ratio);
			SmartDashboard.putNumber("cvhAreaRatio", cvh_area_ratio);
			SmartDashboard.putNumber("periRatio", perimeter_ratio);

			Rect rect = new Rect((int) top, (int) left, (int) height, (int) width);
			NIVision.imaqDrawShapeOnImage(frame, frame, rect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_RECT,
					(float) 0xFFFFFF);
		}
		SmartDashboard.putNumber("bestScore", smallestScore);
		// SmartDashboard.putNumber("cycleTime", time.get());
		// time.reset();

		// draw image to driver station
		CameraServer.getInstance().setImage(frame);
		// CameraServer.getInstance().setImage(binaryFrame);
	}

	// score metrics
	double COM_X_TARGET = 0.5;
	double COM_Y_TARGET = 0.675;
	double AREA_TARGET = 0.245;
	double PERI_TARGET = 1.3;

	// for debugging
	boolean DRAW_RECT = true;

	public double scoreParticle(int particleIndex) {
		double area = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_AREA);

		double cvh_area = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0,
				NIVision.MeasurementType.MT_CONVEX_HULL_AREA);
		double cvh_area_ratio = area / cvh_area;

		double perimeter = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0,
				NIVision.MeasurementType.MT_PERIMETER);
		double cvh_perimeter = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0,
				NIVision.MeasurementType.MT_CONVEX_HULL_PERIMETER);
		double perimeter_ratio = perimeter / cvh_perimeter;

		double com_x = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0,
				NIVision.MeasurementType.MT_CENTER_OF_MASS_X);
		double com_y = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0,
				NIVision.MeasurementType.MT_CENTER_OF_MASS_Y);
		double top = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0,
				NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
		double left = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0,
				NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
		double width = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0,
				NIVision.MeasurementType.MT_BOUNDING_RECT_WIDTH);
		double height = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0,
				NIVision.MeasurementType.MT_BOUNDING_RECT_HEIGHT);
		double com_x_ratio = (com_x - left) / width;
		double com_y_ratio = (com_y - top) / height;

		if (DRAW_RECT) {
			Rect rect = new Rect((int) top, (int) left, (int) height, (int) width);
			NIVision.imaqDrawShapeOnImage(frame, frame, rect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_RECT, (float) 255);
		}

		double score = 0.0;
		score += Math.abs(com_x_ratio - COM_X_TARGET);
		score += Math.abs(com_y_ratio - COM_Y_TARGET);
		score += Math.abs(cvh_area_ratio - AREA_TARGET);
		score += Math.abs(perimeter_ratio - PERI_TARGET) / 4;
		return score;

	}

	getAngle(){
		double RES_Y = 480;
		double RES_X = 640;
		double rect_left = 40;
		double rect_top = 360;
		double rect_width = 100;
		double rect_height = 80;
		double FOV = 60;
		
		double center = rect_left + (rect_width/2);
		double DDistance = center -(RES_X/2);
		double angle = DDistance * FOV/2;
		
	
	}

}
