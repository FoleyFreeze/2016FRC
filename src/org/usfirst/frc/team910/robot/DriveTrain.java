package org.usfirst.frc.team910.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain {

	AHRS navX;

	Talon lmTalon;
	Talon rmTalon;

	CANTalon LFmCANTalon; // LF
	CANTalon LBmCANTalon;
	CANTalon RFmCANTalon; // RF
	CANTalon RBmCANTalon; //

	Encoder lEncoder;
	Encoder rEncoder;

	double MAX_RAMP_RATE = 0.03; // one second 0 to 1 ramp

	public DriveTrain(AHRS x) {

		if (Robot.TEST) {
			LFmCANTalon = new CANTalon(2);
			LBmCANTalon = new CANTalon(3);
			RFmCANTalon = new CANTalon(0);
			RBmCANTalon = new CANTalon(1);

			lEncoder = new Encoder(IO.LEFT_DRIVE_A_ENCODER, IO.LEFT_DRIVE_B_ENCODER, false);
			rEncoder = new Encoder(IO.RIGHT_DRIVE_A_ENCODER, IO.RIGHT_DRIVE_B_ENCODER, false);
			lEncoder.setDistancePerPulse(120.0 / 2244.0);
			rEncoder.setDistancePerPulse(-120.0 / 1571.0);

		} else {
			lmTalon = new Talon(IO.LEFT_DRIVE_MOTOR);
			rmTalon = new Talon(IO.RIGHT_DRIVE_MOTOR);
			lEncoder = new Encoder(IO.LEFT_DRIVE_A_ENCODER, IO.LEFT_DRIVE_B_ENCODER, false);
			rEncoder = new Encoder(IO.RIGHT_DRIVE_A_ENCODER, IO.RIGHT_DRIVE_B_ENCODER, false);
			lEncoder.setDistancePerPulse(120.0 / 3600.0);
			rEncoder.setDistancePerPulse(120.0 / 3600.0);
		}

		navX = x;

	}

	double prevL = 0;
	double prevR = 0;
	double prevT = 0;

	public void tankDrive(double YAxisLeft, double YAxisRight) {
		// controls tank drive motors, obtaining joystick inputs
		if (Math.abs(YAxisLeft) > 1)
			YAxisLeft = YAxisLeft / Math.abs(YAxisLeft);
		if (Math.abs(YAxisRight) > 1)
			YAxisRight = YAxisRight / Math.abs(YAxisRight);

		prevL = YAxisLeft;
		prevR = YAxisRight;

		if (Robot.TEST) {
			LFmCANTalon.set(-YAxisLeft * 1);
			LBmCANTalon.set(-YAxisLeft * 1);
			RFmCANTalon.set(YAxisRight * 1);
			RBmCANTalon.set(YAxisRight * 1);
		} else {
			lmTalon.set(YAxisLeft * 1);
			rmTalon.set(-YAxisRight * 1);
		}

	}

	// when ljoystick trigger is pressed get the intial encoder value
	// while held, contiually wheel position to intial value initiate
	// dynamicBreaking
	// if encoders change value, engage motor power to move back to held value
	// when reach held value stop
	// smart way, pid gain value to get to 0
	double startEncL, startEncR;

	public void dynamicBraking(boolean firstTime) {
		/*
		 * code for dynamic braking The first time it is called, it obtains the
		 * distances of the encoders, then power to the motors is set
		 */
		if (firstTime) {
			startEncL = lEncoder.getDistance();
			startEncR = rEncoder.getDistance();

		} else {
			// set encoder
			double lPwr = startEncL - lEncoder.getDistance();
			double rPwr = startEncR - rEncoder.getDistance();
			tankDrive(lPwr, rPwr);
		}

	}

	double intlevalue;
	double intrevalue;

	public void driveStraight(double lpower, boolean firstTime) {
		// driveStraight allows the robot to be driven straight by moving one
		// joystick with the right trigger held
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
			//SmartDashboard.putNumber("init diff", intdiff);

			levalue = lEncoder.getDistance();
			revalue = rEncoder.getDistance();

			currentdiff = levalue - revalue;
			//SmartDashboard.putNumber("curr diff", currentdiff);

			gooddiff = currentdiff - intdiff;
			//SmartDashboard.putNumber("good diff", gooddiff);

			adj = gooddiff * .25; //Drive Straight P value; applying full power after 4 inches. pretty agressive

			double lnew = lpower - adj;
			double rnew = lpower + adj;
			tankDrive(lnew, rnew);
		}
	}

	boolean previousDbrake = false;
	boolean previousSdrive = false;
	boolean previousCdrive = false;

	public void run(double yAxisLeft, double yAxisRight, double pov, boolean sDrive, boolean dBrake,
			boolean compassDrive, double rThrottle) {
		
		SmartDashboard.putNumber("Compass Power", rThrottle);

		// ramp rate limiting left side
		double driveL;
		double driveR;
		if (yAxisLeft > 0) {// for positive powers
			if (yAxisLeft > prevL + MAX_RAMP_RATE) {// if increasing power,
													// slowly ramp
				driveL = prevL + MAX_RAMP_RATE;
			} else {// if decreasing power, just do it
				driveL = yAxisLeft;
			}
		} else {// for negative powers
			if (yAxisLeft < prevL - MAX_RAMP_RATE) {// if increasing negative
													// power, slowly ramp
				driveL = prevL - MAX_RAMP_RATE;
			} else {// if decreasing power, just do it
				driveL = yAxisLeft;
			}
		}
		// ramp rate limiting right side
		if (yAxisRight > 0) {
			if (yAxisRight > prevR + MAX_RAMP_RATE) {
				driveR = prevR + MAX_RAMP_RATE;
			} else {
				driveR = yAxisRight;
			}
		} else {
			if (yAxisRight < prevR - MAX_RAMP_RATE) {
				driveR = prevR - MAX_RAMP_RATE;
			} else {
				driveR = yAxisRight;
			}
		}

		if (dBrake) {
			// Dynamic Braking Function//
			dynamicBraking(!previousDbrake);
			previousDbrake = true;
			previousSdrive = false;
			previousCdrive = false;
			prevT = 0;
		}

		else if (sDrive) {
			// Straight Drive Function//
			driveStraight(yAxisRight, !previousSdrive);
			previousDbrake = false;
			previousSdrive = true;
			previousCdrive = false;
			prevT = 0;

		} else if (pov != -5000 && navX.isConnected()) {
			rThrottle = (-rThrottle + 1) / 2;
			double power;
			if (rThrottle > prevT + MAX_RAMP_RATE) {// if increasing power,
													// slowly ramp
				power = prevT + MAX_RAMP_RATE;
			} else {// if decreasing power, just do it
				power = rThrottle;
			}
			// Compass Drive Function//
			compassDrive(power, navX.getYaw(), !previousCdrive, pov);
			prevT = power;
			previousCdrive = true;
			previousDbrake = false;
			previousSdrive = false;
		}

		else {// just drive

			tankDrive(driveL, driveR);
			previousDbrake = false;
			previousSdrive = false;
			previousCdrive = false;
			prevT = 0;
		}

		SmartDashboard.putNumber("L Encoder", lEncoder.getDistance());
		SmartDashboard.putNumber("R Encoder", rEncoder.getDistance());

	}

	double cmpsPrevPower = 0;
	
	public void compassDrive(double actualPower, double currentYAW, boolean firstYAW, double targetAngle) {
		/*
		 * Compass drive uses field-oriented drive to drive in straight lines
		 * using the "WASD" buttons pushing the left button causes the robot to drive left, the right to move right, and so on.
		 * Each button is set to an angle that the robot turns. 
		 */
		
		//double actualPower = 0;
		/*if (power > 0) {// for positive powers
			if (power > prevL + MAX_RAMP_RATE) {// if increasing power,
													// slowly ramp
				actualPower = prevL + MAX_RAMP_RATE;
			} else {// if decreasing power, just do it
				actualPower = power;
			}
		} else {// for negative powers
			if (power < prevL - MAX_RAMP_RATE) {// if increasing negative
													// power, slowly ramp
				actualPower = prevL - MAX_RAMP_RATE;
			} else {// if decreasing power, just do it
				actualPower = power;
			}
		}*/
		
		
		double diff;
		double adj;
		double inverse = 1;

		//SmartDashboard.putNumber("targetAngle", targetAngle);

		boolean closeInvert = false;

		if (Math.abs(targetAngle) != 900) {
			double targetDiff = Math.abs(currentYAW - targetAngle);
			if (targetDiff > 180) {
				targetDiff = -(targetDiff - 360);
			}
			double oppositeDiff = Math.abs(targetDiff - 180);
			closeInvert = oppositeDiff < targetDiff;
		}

		if (targetAngle > 134 || targetAngle < -134 || closeInvert) {
			targetAngle = targetAngle + 180;
			inverse = -1;
		} else {
			inverse = 1;
		}

		if (Math.abs(actualPower) > 1)
			actualPower = actualPower / Math.abs(actualPower);

		diff = currentYAW - targetAngle;

		//SmartDashboard.putNumber("preAdjDiff", diff);

		if (Math.abs(diff) > 360) {
			if (diff > 0)
				diff = diff - 360;
			else
				diff = diff + 360;
		}

		if (diff > 180) {
			diff = -360 + diff;
		} else if (diff < -180) {
			diff = 360 + diff;
		}

		//SmartDashboard.putNumber("adjustedDiff", diff);
		//SmartDashboard.putNumber("power", power);
		//SmartDashboard.putNumber("inverse", inverse);

		double turnAngle = IO.lookup(IO.COMPASS_ANGLE, IO.POWER_AXIS, Math.abs(actualPower));
		//SmartDashboard.putNumber("turnAngle", turnAngle);

		if (diff > turnAngle) {
			tankDrive(-actualPower, actualPower);
		} else if (diff < -turnAngle) {
			tankDrive(actualPower, -actualPower);
		} else {
			adj = diff * .05; // was .02   compass drive P value (for driving straight) pretty low, but only increase if necessary
			//SmartDashboard.putNumber("adjustment", adj);

			// power = power * inverse;
			double lnew = actualPower * inverse - adj;
			double rnew = actualPower * inverse + adj;

			double max = Math.max(Math.abs(lnew), Math.abs(rnew));
			if (max > actualPower) {
				lnew /= max;
				rnew /= max;
				lnew *= actualPower;
				rnew *= actualPower;
			}

			tankDrive(lnew, rnew);

		}

	}

	// returns the value of x and y
	public double getAngle(double y, double x) {
		return Math.atan2(y, x) * 180 / Math.PI;
	}

	// calculates the hypotenuse created with x and y
	public double getR(double y, double x) {
		double c;
		c = (x * x) + (y * y);
		return c;
	}

	public void shooterAlign(double cameraAngle, double botAngle) {
		// Moves shooter to the camera's position
		double diff;

		diff = cameraAngle - botAngle;

		if (Math.abs(diff) > 360) {
			if (diff > 0)
				diff = diff - 360;
			else
				diff = diff + 360;
		}

		if (diff > 180) {
			diff = -360 + diff;
		} else if (diff < -180) {
			diff = 360 + diff;
		}

		double slowPower;

		slowPower = diff * 0.15; //align to goal (vision) P value; could be increased by .05 or so

		if(slowPower > 0.35){  //max power levels, consider increasing if no movement at large angles
			slowPower = 0.35;
		} else if(slowPower < -0.35){
			slowPower = -0.35;
		}
		
		tankDrive(slowPower, -slowPower);

	}

	public void resetEncoders() {
		// Resets Encoders
		lEncoder.reset();
		rEncoder.reset();
	}

	public double getDistance() {
		// Obtains the distance from each encoder
		return (lEncoder.getDistance() + rEncoder.getDistance()) / 2;
	}

}
