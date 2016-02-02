
package org.usfirst.frc.team910.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogInput;
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
	boolean test = true;

	//DriveTrainTest testdrive;
	DriveTrain drive;

	Joystick rJoy;
	Joystick lJoy;
	Joystick driveBoard;

	AHRS navX;

	AnalogInput dSensor;

	public void robotInit() {
		navX = new AHRS(SPI.Port.kMXP); // SPI.Port.kMXP

		if (test == true) {
			testdrive = new DriveTrainTest(navX);
		} else {
			drive = new DriveTrain(navX);
		}

		lJoy = new Joystick(IO.LEFT_JOYSTICK);
		rJoy = new Joystick(IO.RIGHT_JOYSTICK);
		driveBoard = new Joystick(IO.DRIVE_BOARD);

		dSensor = new AnalogInput(1);

<<<<<<< HEAD
		cam = CameraServer.getInstance();
		cam.startAutomaticCapture("cam1");

=======
>>>>>>> origin/Week3
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {

	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {

		double YAxisLeft = -lJoy.getY();
		double YAxisRight = -rJoy.getY();

<<<<<<< HEAD
		int angle = WASDToAngle(driveBoard.getRawButton(11), driveBoard.getRawButton(1), driveBoard.getRawButton(2),
				driveBoard.getRawButton(3));
		// W is 11, A is 1, S is 2, D is 3//
		drive.run(YAxisLeft, YAxisRight, (double) angle, rJoy.getTrigger(), lJoy.getTrigger(), rJoy.getRawButton(2),
				rJoy.getThrottle());
=======
		if (test == false) {

			drive.run(YAxisLeft, YAxisRight, rJoy.getX(), rJoy.getTrigger(), lJoy.getTrigger(), rJoy.getRawButton(2));
		} else {
			testdrive.run(YAxisLeft, YAxisRight, rJoy.getX(), rJoy.getTrigger(), lJoy.getTrigger(),
					rJoy.getRawButton(2));
		}
>>>>>>> origin/Week3

		if (rJoy.getRawButton(3)) {
			navX.zeroYaw();
		}

		SmartDashboard.putNumber("navX Pitch", navX.getPitch());
		SmartDashboard.putNumber("navX Yaw", navX.getYaw());
		SmartDashboard.putNumber("navX Roll", navX.getRoll());
		SmartDashboard.putNumber("navX X", navX.getRawGyroX());
		SmartDashboard.putNumber("navX Y", navX.getRawGyroY());
		SmartDashboard.putNumber("navX Z", navX.getRawGyroZ());
		SmartDashboard.putNumber("Distance", dSensor.getVoltage());

	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

	public int WASDToAngle(boolean W, boolean A, boolean S, boolean D) {
		if (W) {
			if (A) {
				return 45;
			} else if (D) {
				return -45;
			} else {
				return 0;

			}
		} else if (S) {
			if (A) {
				return 135;
			} else if (D) {
				return -135;
			} else {
				return 180;
			}
		} else if (A) {
			return 90;
		} else if (D) {
			return -90;
		} else {
			return -5000;
		}
	}
}
