
package org.usfirst.frc.team910.robot;

import com.kauailabs.navx.frc.AHRS; 

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

	DriveTrainTest testdrive;
	DriveTrain drive;

	Joystick rJoy;
	Joystick lJoy;

	AHRS navX;

	public void robotInit() {
		if (test == true) {
			testdrive = new DriveTrainTest();
		} else {
			drive = new DriveTrain();
		}

		navX = new AHRS(SPI.Port.kMXP);

		lJoy = new Joystick(IO.LEFT_JOYSTICK);
		rJoy = new Joystick(IO.RIGHT_JOYSTICK);

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

		if (test == false) {

			drive.run(YAxisLeft, YAxisRight, rJoy.getTrigger(), lJoy.getTrigger());
		} else {
			testdrive.run(YAxisLeft, YAxisRight, rJoy.getTrigger(), lJoy.getTrigger());
		}

		SmartDashboard.putNumber("navX Pitch", navX.getPitch());
		SmartDashboard.putNumber("navX Yaw", navX.getYaw());
		SmartDashboard.putNumber("navX Roll", navX.getRoll());
	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

}
