package org.usfirst.frc.team910.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain {

	AHRS navX;
	double setyaaaw = 0;
	double currentYAW;
	double finalyaw;

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

	double intlevalue;
	double intrevalue;

	public void driveStraight(double lpower, boolean firstTime) {
		double levalue;
		double revalue;

		double currentdiff;
		double intdiff;
		double gooddiff;
		double adj;

		if (firstTime) {
			intlevalue = lEncoder.getDistance();
			intrevalue = rEncoder.getDistance();

		} else {

			intdiff = intlevalue - intrevalue;

			levalue = lEncoder.getDistance();
			revalue = rEncoder.getDistance();

			currentdiff = levalue - revalue;

			gooddiff = currentdiff - intdiff;

			adj = gooddiff * 1;

			double lnew = lpower - adj;
			double rnew = lpower + adj;
			tankDrive(lnew, rnew);
		}
	}

	boolean previousDbrake = false;
	boolean previousSdrive = false;

	public void run(double yAxisLeft, double yAxisRight, boolean sDrive, boolean dBrake) {

		if (dBrake) {
			// Dynamic Braking Function//
			dynamicBraking(!previousDbrake);
			previousDbrake = true;
			previousSdrive = false;
		}

		else if (sDrive) {
			// Straight Drive Function//
			driveStraight(yAxisRight, !previousSdrive);
			previousDbrake = false;
			previousSdrive = true;
		}

		else {
			tankDrive(yAxisLeft, yAxisRight);
			previousDbrake = false;
			previousSdrive = false;
		}

		SmartDashboard.putNumber("L Encoder", lEncoder.getDistance());
		SmartDashboard.putNumber("R Encoder", rEncoder.getDistance());

	}

	double intYAW;

	public void compassDrive(double power, double currentYAW, boolean firstYAW) {

		double gooddiff;
		double adj;

		if (firstYAW) {
			intYAW = currentYAW;

		} else {

			gooddiff = currentYAW - intYAW;

			adj = gooddiff * .25;

			double lnew = power - adj;
			double rnew = power + adj;
			tankDrive(lnew, rnew);
		}
	}

	public void CompassDrive(double currentYAW, double rjoystick) {

		if (currentYAW > 0) {
			double finalyaw;

			finalyaw = setyaaaw - currentYAW;

			double lnew = rjoystick - finalyaw;
			double rnew = rjoystick + finalyaw;
			tankDrive(lnew, rnew);

		} else {

		}
	}
}
