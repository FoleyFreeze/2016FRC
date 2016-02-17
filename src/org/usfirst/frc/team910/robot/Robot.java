
package org.usfirst.frc.team910.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
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

	CameraServer cam;

	public void robotInit() {
		navX = new AHRS(SPI.Port.kMXP); // SPI.Port.kMXP

		drive = new DriveTrain(navX);
		BC = new BoulderController();
		
		lJoy = new Joystick(IO.LEFT_JOYSTICK);
		
		
		
		
		rJoy = new Joystick(IO.RIGHT_JOYSTICK);
		GamePad = new Joystick(IO.GAME_PAD);
		
		
		driveBoard = new Joystick(IO.DRIVE_BOARD);

		dSensor = new AnalogInput(1);

		
		cam = CameraServer.getInstance();
		cam.startAutomaticCapture("cam0");
		
	}

	/**
	 * This function is called periodically during autonomous
	 * 
	 */

	int autonstate = 0;

	public void autonomousPeriodic() {
		// Auton

		switch (autonstate) {

		case 0:
			autonstate = 1;
			// prep robot for crossing defences
			break;

		case 1:
			drive.compassDrive(1, navX.getYaw(), false, 0);// Compass drive
															// forward at Full
															// Power
			if (getAvgAccel() > 0.8)
				autonstate = 2;
			break;

		case 2:
			drive.compassDrive(1, navX.getYaw(), false, 0);
			if (getAvgAccel() < 0.3)
				autonstate = 3;
			break;

		case 3:
			drive.tankDrive(0, 0);
			break;
		}

	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		
		BC.runBC(driveBoard.getRawButton(4), driveBoard.getRawButton(5), driveBoard.getRawButton(6), 
				driveBoard.getRawButton(7), driveBoard.getRawButton(8), driveBoard.getRawButton(9));
	 
		boolean negate = lJoy.getRawButton(12);
		
		double YAxisLeft = -lJoy.getY();
		double YAxisRight = -rJoy.getY();
		
		if (negate) {
		
			YAxisLeft = lJoy.getY();
			YAxisRight = rJoy.getY();
		} else{ 
			YAxisLeft = -lJoy.getY();
			YAxisRight = -rJoy.getY();
		}
 
		

		int angle = WASDToAngle(driveBoard.getRawButton(11), driveBoard.getRawButton(1), driveBoard.getRawButton(2),
				driveBoard.getRawButton(3));

		// W is 11, A is 1, S is 2, D is 3//
		drive.run(YAxisLeft, YAxisRight, (double) angle, rJoy.getTrigger(), lJoy.getTrigger(), rJoy.getRawButton(2),
				rJoy.getThrottle());

		if (rJoy.getRawButton(3)) {
			navX.zeroYaw();
		}

		SmartDashboard.putNumber("wasd angle",angle);
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
		SmartDashboard.putNumber("avgAccel", getAvgAccel());


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
		if (accelArrayIndex > accelArray.length)
			accelArrayIndex = 0;

		double accel = 0;
		for (int i = 0; i < accelArray.length; i++) {
			accel += accelArray[i];
		}
		return accel / accelArray.length;
	}
}
