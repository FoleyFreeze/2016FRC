
package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;

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
	boolean test = false;

	DriveTrainTest testdrive;
	DriveTrain drive;

	Joystick rJoy;
	Joystick lJoy;

	public void robotInit() {
		if (test == true) {
			testdrive = new DriveTrainTest();
		} else {
			drive = new DriveTrain();
		}
		rJoy = new Joystick(IO.LEFT_JOYSTICK);
		lJoy = new Joystick(IO.RIGHT_JOYSTICK);
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

		double yAxisLeft = lJoy.getY();
		double yAxisRight = rJoy.getY();

		drive.tankDrive(yAxisLeft, yAxisRight);

	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

}
