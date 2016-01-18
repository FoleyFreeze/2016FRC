package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain {

	Talon lmTalon;
	Talon rmTalon;

	Encoder lEncoder;
	Encoder rEncoder;

	public DriveTrain() {
		lmTalon = new Talon(IO.LEFT_DRIVE_MOTOR);
		rmTalon = new Talon(IO.RIGHT_DRIVE_MOTOR);
		lEncoder = new Encoder(IO.LEFT_DRIVE_A_ENCODER, IO.LEFT_DRIVE_B_ENCODER, false);
		rEncoder = new Encoder(IO.RIGHT_DRIVE_A_ENCODER, IO.RIGHT_DRIVE_B_ENCODER, false);
	}

	public void tankDrive(double YAxisLeft, double YAxisRight) {
		lmTalon.set(-YAxisLeft);
		rmTalon.set(YAxisRight);
	}

	// when ljoystick trigger is pressed get the intial encoder value
	// while held, contiually wheel position to intial value initiate
	// dynamicBreaking
	// if encoders change value, engage motor power to move back to held value
	// when reach held value stop
	// smart way, pid gain value to get to 0
	double startEncL, startEncR;

	public void dynamicBraking(boolean firstTime) {

		if (firstTime) {
			startEncL = lEncoder.getDistance();
			startEncR = rEncoder.getDistance();

		} else {
			// set encoder
			tankDrive(lEncoder.getDistance() - startEncL, rEncoder.getDistance() - startEncR);
		}

	}

	public void driveStraight(double lpower) {

		double levalue;
		double revalue;

		levalue = lEncoder.getDistance();
		revalue = rEncoder.getDistance();

		double diff;

		diff = levalue - revalue;

		double adj = diff * 1;

		double lnew = lpower - adj;
		double rnew = lpower + adj;
		tankDrive(lnew, rnew);

	}

	boolean previousDbrake = false;

	public void run(double yAxisLeft, double yAxisRight, boolean sDrive, boolean dBrake) {

		if (dBrake) {
			// Dynamic Braking Function//
			dynamicBraking(!previousDbrake);
			previousDbrake = true;
		}

		else if (sDrive) {
			// Straight Drive Function//
			driveStraight(yAxisRight);
			previousDbrake = false;
		}

		else {
			tankDrive(yAxisLeft, yAxisRight);
			previousDbrake = false;
		}

		SmartDashboard.putNumber("L Encoder", lEncoder.getDistance());
		SmartDashboard.putNumber("R Encoder", rEncoder.getDistance());

	}

}
