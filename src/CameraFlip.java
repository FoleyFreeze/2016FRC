import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.vision.USBCamera;

public class CameraFlip {

	
	boolean frontface = true;
	
	
	public void cameraFlip() {
		
		
		CameraServer cam;
		USBCamera FCam;
		USBCamera BCam;
		
		Ultrasonic RF;
		Ultrasonic LF;
		
		Ultrasonic RB;
		Ultrasonic LB;
		
		if (frontface == true) {
			
			cam.startAutomaticCapture(FCam);
			BCam.stopCapture();
			RF.setEnabled(true);
			LF.setEnabled(true);
			RB.setEnabled(false);
			LB.setEnabled(false);
				}
		else {
			cam.startAutomaticCapture(BCam);
			FCam.stopCapture();
			LB.setEnabled(true);
			RB.setEnabled(true);
			RF.setEnabled(false);
			LF.setEnabled(false);
			
		
		
	}
}
