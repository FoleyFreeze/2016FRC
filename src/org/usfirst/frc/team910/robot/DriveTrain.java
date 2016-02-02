package org.usfirst.frc.team910.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain {

	int inverse;
	
	
	AHRS navX;

	Talon lmTalon;
	Talon rmTalon;

	Encoder lEncoder;
	Encoder rEncoder;

	public DriveTrain(AHRS x) {
		navX = x;
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

			adj = gooddiff * .25;

			double lnew = lpower - adj;
			double rnew = lpower + adj;
			
			tankDrive(lnew * inverse, rnew * inverse);
		
			
		}
	}

	boolean previousDbrake = false;
	boolean previousSdrive = false;
	boolean previousCdrive = false;

	public void run(double yAxisLeft, double yAxisRight, double xAxisRight, boolean sDrive, boolean dBrake,
			boolean compassDrive) {

		if (dBrake) {
			// Dynamic Braking Function//
			dynamicBraking(!previousDbrake);
			previousDbrake = true;
			previousSdrive = false;
			previousCdrive = false;
		}

		else if (sDrive) {
			// Straight Drive Function//
			driveStraight(yAxisRight, !previousSdrive);
			previousDbrake = false;
			previousSdrive = true;
			previousCdrive = false;

		} else if (compassDrive && navX.isConnected()) {
			// Compass Drive Function//
			compassDrive(getR(xAxisRight, yAxisRight), navX.getYaw(), !previousCdrive,
					getAngle(xAxisRight, yAxisRight));
			previousCdrive = true;
			previousDbrake = false;
			previousSdrive = false;
		}

		else {
			tankDrive(yAxisLeft, yAxisRight);
			previousDbrake = false;
			previousSdrive = false;
			previousCdrive = false;
		}

		SmartDashboard.putNumber("L Encoder", lEncoder.getDistance());
		SmartDashboard.putNumber("R Encoder", rEncoder.getDistance());

	}

	public void compassDrive(double power, double currentYAW, boolean firstYAW, double targetAngle) {

		double diff;
		double adj;
<<<<<<< HEAD
		double inverse;
		
		if (targetAngle)>134){ 
			targetAngle = targetAngle + 180;
			inverse= -1;	
		} else {
			inverse = 1;	
		}
			
		if (Math.abs(power) > 1)
			power = power / Math.abs(power);
=======
>>>>>>> refs/remotes/origin/master

		diff = currentYAW - targetAngle;
		if (diff > 180) {
			diff = 360 - diff;
		} else if (diff < -180) {
			diff = -360 - diff;
		}
<<<<<<< HEAD

		SmartDashboard.putNumber("targetAngle", targetAngle);
		SmartDashboard.putNumber("angleDiff", diff);

		power = power * inverse;
		
		if (diff > 30) {
			tankDrive(-power, power);
=======
		if (diff > 30) {
			tankDrive(power, -power);

>>>>>>> refs/remotes/origin/master
		} else if (diff < -30) {
			tankDrive(-power, power);
		} else {
			adj = diff * .05 * inverse;

			double lnew = power - adj;
			double rnew = power + adj;
<<<<<<< HEAD
			return tankDrive(lnew, rnew);
			
=======
			
			if (getAngle(currentYAW, currentYAW) < 180) {
				inverse = 1;
			}else {
				inverse= -1;
			}
			tankDrive(lnew * inverse, rnew * inverse);
			
		
		
>>>>>>> refs/remotes/origin/master
		}
	}

	public double getAngle(double y, double x) {
		return Math.atan2(y, x);

	}

	public double getR(double y, double x) {
		double c;
		c = (x * x) + (y * y);

<<<<<<< HEAD
=======
		return Math.sqrt(c);
		

>>>>>>> refs/remotes/origin/master
	}
}
