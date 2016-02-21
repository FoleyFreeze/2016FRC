
package org.usfirst.frc.team910.robot;

import com.kauailabs.navx.frc.AHRS;
import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	static final boolean TEST = false;

	DriveTrain drive;
	BoulderController BC;

	Joystick rJoy;
	Joystick lJoy;
	Joystick driveBoard;
	Joystick GamePad;

	AHRS navX;

	AnalogInput dSensor;

	int camSession;
	Image cameraFrame;

	public void robotInit() {
		navX = new AHRS(SPI.Port.kMXP); // SPI.Port.kMXP

		drive = new DriveTrain(navX);
		BC = new BoulderController();

		lJoy = new Joystick(IO.LEFT_JOYSTICK);

		rJoy = new Joystick(IO.RIGHT_JOYSTICK);
		GamePad = new Joystick(IO.GAME_PAD);

		driveBoard = new Joystick(IO.DRIVE_BOARD);

		dSensor = new AnalogInput(1);

		// setup things for camera switching
		//cameraFrame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
		//camSession = NIVision.IMAQdxOpenCamera("cam0", NIVision.IMAQdxCameraControlMode.CameraControlModeController);
		//NIVision.IMAQdxConfigureGrab(camSession);
	}

	/**
	 * This function is called periodically during autonomous
	 * 
	 */

	int autonstate = 0;

	public void autonomousPeriodic() {
		// Auton
		/*
		 * switch (autonstate) {
		 * 
		 * case 0: autonstate = 1; // prep robot for crossing defences break;
		 * 
		 * case 1: drive.compassDrive(1, navX.getYaw(), false, 0);// Compass
		 * drive // forward at Full // Power if (getAvgAccel() > 0.8) autonstate
		 * = 2; break;
		 * 
		 * case 2: drive.compassDrive(1, navX.getYaw(), false, 0); if
		 * (getAvgAccel() < 0.3) autonstate = 3; break;
		 * 
		 * case 3: drive.tankDrive(0, 0); break; }
		 */

	}

	/**
	 * This function is called periodically during operator control
	 */
	boolean previousMode = false;
	boolean prevFlipControls = false;
	boolean firstTime = true;
	boolean capCam = false;
	Timer time = new Timer();

	public void teleopPeriodic() {

		// this means on = auto, off = manual. Add ! before driveBoard to flip
		if (driveBoard.getRawButton(IO.MAN_AUTO_SW)) {

			if (driveBoard.getRawButton(IO.MAN_AUTO_SW) != previousMode) {
				// Call Mode Switch Function
			}
			BC.runBC(driveBoard.getRawButton(IO.LAYUP), driveBoard.getRawButton(IO.STOW),
					driveBoard.getRawButton(IO.FAR_SHOT), driveBoard.getRawButton(IO.GATHER),
					driveBoard.getRawButton(IO.PRIME), driveBoard.getRawButton(IO.FIRE),
					driveBoard.getRawButton(IO.LOWBAR), driveBoard.getRawButton(IO.PORT),
					driveBoard.getRawButton(IO.SALLYPORT), driveBoard.getRawButton(IO.FLIPPY_DE_LOS_FLOPPIES),
					driveBoard.getRawButton(IO.DRAWBRIDGE));

		}

		else {
			if (driveBoard.getRawButton(IO.MAN_AUTO_SW) != previousMode) {
				// Call Mode Switch Function
				BC.gatherer.autoAndback(driveBoard.getRawButton(IO.MAN_AUTO_SW));
			}
			// call manual position functions
			BC.gatherer.manualGather(GamePad.getRawAxis(1) * 0.5);
			BC.gatherer.gatherwheel(GamePad.getRawAxis(5));
		}
		previousMode = driveBoard.getRawButton(IO.MAN_AUTO_SW);

		boolean flipControls = rJoy.getRawButton(IO.FLIP_CONTROLS);

		double YAxisLeft = -lJoy.getY();
		double YAxisRight = -rJoy.getY();

		if (firstTime) {
			firstTime = false;
			//NIVision.IMAQdxStartAcquisition(camSession);
		}
		
		if (flipControls) {
			/*if (flipControls != prevFlipControls) {
				camSession = NIVision.IMAQdxOpenCamera("cam1",
						NIVision.IMAQdxCameraControlMode.CameraControlModeController);
				NIVision.IMAQdxConfigureGrab(camSession);
				NIVision.IMAQdxStartAcquisition(camSession);
			}*/

			YAxisLeft = lJoy.getY();
			YAxisRight = rJoy.getY();
		} else {
			/*if (flipControls != prevFlipControls) {
				camSession = NIVision.IMAQdxOpenCamera("cam0",
						NIVision.IMAQdxCameraControlMode.CameraControlModeController);
				NIVision.IMAQdxConfigureGrab(camSession);
				NIVision.IMAQdxStartAcquisition(camSession);
			}*/
			
			YAxisLeft = -lJoy.getY();
			YAxisRight = -rJoy.getY();
		}
		prevFlipControls = flipControls;
		
		//NIVision.IMAQdxGrab(camSession, cameraFrame, 1);
		//CameraServer.getInstance().setImage(cameraFrame);


		// BC.shooter.jog(GamePad.getRawButton(IO.JOG_SHOOTER_UP),
		// GamePad.getRawButton(IO.JOG_SHOOTER_DOWN));

		int angle = WASDToAngle(driveBoard.getRawButton(IO.WASD_W), driveBoard.getRawButton(IO.WASD_A),
				driveBoard.getRawButton(IO.WASD_S), driveBoard.getRawButton(IO.WASD_D));

		drive.run(YAxisLeft, YAxisRight, (double) angle, rJoy.getTrigger(), lJoy.getTrigger(),
				rJoy.getRawButton(IO.COMPASS_POWER_THROTTLE), rJoy.getThrottle());

		if (rJoy.getRawButton(IO.ZERO_YAW)) {
			navX.zeroYaw();
		}

		SmartDashboard.putNumber("wasd angle", angle);
		SmartDashboard.putNumber("navX Pitch", navX.getPitch());
		SmartDashboard.putNumber("navX Yaw", navX.getYaw());
		SmartDashboard.putNumber("navX Roll", navX.getRoll());
		SmartDashboard.putNumber("navX X", navX.getRawGyroX());
		SmartDashboard.putNumber("navX Y", navX.getRawGyroY());
		SmartDashboard.putNumber("navX Z", navX.getRawGyroZ());
		SmartDashboard.putNumber("Distance", dSensor.getVoltage());
		SmartDashboard.putNumber("accel X", navX.getRawAccelX());
		SmartDashboard.putNumber("accel Y", navX.getRawAccelY());
		SmartDashboard.putNumber("accel Z", navX.getRawAccelZ());
		// SmartDashboard.putNumber("avgAccel", getAvgAccel());

	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

	public int WASDToAngle(boolean W, boolean A, boolean S, boolean D) {
		if (W) {
			if (A) {
				return -45;
			} else if (D) {
				return 45;
			} else {
				return 0;

			}
		} else if (S) {
			if (A) {
				return -135;
			} else if (D) {
				return 135;
			} else {
				return 180;
			}
		} else if (A) {
			return -90;
		} else if (D) {
			return 90;
		} else {
			return -5000;
		}
	}

	double accelArray[] = { 0, 0, 0, 0, 0 };
	int accelArrayIndex = 0;

	public double getAvgAccel() {
		accelArray[accelArrayIndex] = navX.getRawAccelX() + navX.getRawAccelY();
		accelArrayIndex++;
		if (accelArrayIndex >= accelArray.length)
			accelArrayIndex = 0;

		double accel = 0;
		for (int i = 0; i < accelArray.length; i++) {
			accel += accelArray[i];
		}
		return accel / accelArray.length;
	}
}
