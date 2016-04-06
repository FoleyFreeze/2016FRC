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
			// SmartDashboard.putNumber("init diff", intdiff);

			levalue = lEncoder.getDistance();
			revalue = rEncoder.getDistance();

			currentdiff = levalue - revalue;
			// SmartDashboard.putNumber("curr diff", currentdiff);

			gooddiff = currentdiff - intdiff;
			// SmartDashboard.putNumber("good diff", gooddiff);

			adj = gooddiff * .25; // Drive Straight P value; applying full power
									// after 4 inches. pretty agressive

			double lnew = lpower - adj;
			double rnew = lpower + adj;
			tankDrive(lnew, rnew);
		}
	}

	boolean previousDbrake = false;
	boolean previousSdrive = false;
	boolean previousCdrive = false;
	boolean previousUdrive = false;
	double uDriveTargetAngle = 0;
	double prevCompassDir = 0;
	
	double prevEncoderCt = 0;

	public void run(double yAxisLeft, double yAxisRight, double pov, boolean sDrive, boolean dBrake,
			boolean compassDrive, double rThrottle, boolean doA180) {
		
		if(BoulderController.chevalState != 0){
			return;
		}

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

		boolean compassDriveOn = pov != -5000;
		if (dBrake) {
			// Dynamic Braking Function//
			dynamicBraking(!previousDbrake);
			previousDbrake = true;
			previousSdrive = false;
			previousCdrive = false;
			previousUdrive = false;
			prevT = 0;
		}

		else if (sDrive) {
			// Straight Drive Function//
			driveStraight(yAxisRight, !previousSdrive);
			previousDbrake = false;
			previousSdrive = true;
			previousCdrive = false;
			previousUdrive = false;
			prevT = 0;

		} else if (doA180) {
			
			//if first run, figure out the target angle
			if(!previousUdrive) {
				previousUdrive = true;
				
				if(Math.abs(navX.getYaw()) < 90){
					uDriveTargetAngle = 180;
				} else {
					uDriveTargetAngle = 0;
				}
			}
			
			shooterAlign(uDriveTargetAngle, navX.getYaw(), true);
			
			previousDbrake = false;
			previousSdrive = false;
			previousCdrive = false;
			prevT = 0;
			
		} else if (compassDriveOn && navX.isConnected()) {
			rThrottle = (-rThrottle + 1) / 2;
			double power;
			
			if (rThrottle > prevT + MAX_RAMP_RATE) {// if increasing power,
													// slowly ramp
				power = prevT + MAX_RAMP_RATE;
			} else {// if decreasing power, just do it
				power = rThrottle;
			}
			SmartDashboard.putNumber("Compass Power", power);
			
			// Compass Drive Function//
			compassDrive(power, navX.getYaw(), !previousCdrive, pov);
			prevT = power;
			previousCdrive = true;
			previousDbrake = false;
			previousSdrive = false;
			previousUdrive = false;
			prevCompassDir = pov;
			
		} else if (previousCdrive && navX.isConnected()){
			//previousCdrive = false;
			// commenting this out for now, because if it doesnt work it will be scary (robot driving itself)
			double BRAKE_POWER = 0.03; 
			
			
			//slow down compass drive after the button is released
			double invert = 0;
			if(prevCompassLpwr > 0 && prevCompassRpwr > 0){ //we were moving forward
				invert = 1;
			} else if (prevCompassLpwr < 0 && prevCompassRpwr < 0){ //we were moving backwards
				invert = -1;
			} else { // we were turning
				invert = 0; //this causes the last if to eval as 0, thus ending this program
			}
			
			//angle difference
			double dirDiff = navX.getYaw() - prevCompassDir;
			
			//prevent wrap around
			if (dirDiff > 180) {
				dirDiff = -360 + dirDiff;
			} else if (dirDiff < -180) {
				dirDiff = 360 + dirDiff;
			}
			
			//fix potentially inverted directions
			if (dirDiff > 90){
				dirDiff -= 180;
			} else if (dirDiff < -90){
				dirDiff += 180;
			}
			
			double drivePowerL = -BRAKE_POWER * invert - dirDiff * 0.05;
			double drivePowerR = -BRAKE_POWER * invert + dirDiff * 0.05;
			
			//normalize
			double max = Math.max(Math.abs(drivePowerL), Math.abs(drivePowerR));
			if (max == 0){
				drivePowerL = 0;
				drivePowerR = 0;
			} else if (max > BRAKE_POWER) {
				drivePowerL /= max;
				drivePowerR /= max;
				drivePowerL *= BRAKE_POWER;
				drivePowerR *= BRAKE_POWER;
			}
			
			tankDrive(drivePowerL,drivePowerR);
			//SmartDashboard.putNumber("lnew", drivePowerL);
			//SmartDashboard.putNumber("rnew", drivePowerR);
			
			//if the encoders are moving in the opposite direction, we have stopped coasting and are done
			if ((prevEncoderCt - getDistance()) * invert >= 0){
				previousCdrive = false;
			} else {
				previousCdrive = true;
			}
			//SmartDashboard.putNumber("EncDiff", prevEncoderCt - getDistance());
			prevEncoderCt = getDistance();
			//SmartDashboard.putNumber("encAvg",getDistance());
			
			previousDbrake = false;
			previousSdrive = false;
			previousUdrive = false;
			prevT = 0;
		}

		else {// just drive

			tankDrive(driveL, driveR);
			previousDbrake = false;
			previousSdrive = false;
			previousCdrive = false;
			previousUdrive = false;
			prevT = 0;
		}

		SmartDashboard.putNumber("L Encoder", lEncoder.getDistance());
		SmartDashboard.putNumber("R Encoder", rEncoder.getDistance());

		//SmartDashboard.putBoolean("preC", previousCdrive);
	}

	double cmpsPrevPower = 0;
	double prevCompassLpwr = 0;
	double prevCompassRpwr = 0;

	public void compassDrive(double actualPower, double currentYAW, boolean firstYAW, double targetAngle) {
		/*
		 * Compass drive uses field-oriented drive to drive in straight lines
		 * using the "WASD" buttons pushing the left button causes the robot to
		 * drive left, the right to move right, and so on. Each button is set to
		 * an angle that the robot turns.
		 */

		double diff;
		double adj;
		double inverse = 1;

		// SmartDashboard.putNumber("targetAngle", targetAngle);

		boolean closeInvert = false;

		//if (Math.abs(targetAngle) == 90) {
		double targetDiff = Math.abs(currentYAW - targetAngle);
		if (targetDiff > 180) {
			targetDiff = -(targetDiff - 360);
		}
		double oppositeDiff = Math.abs(targetDiff - 180);
		closeInvert = oppositeDiff < targetDiff;
		//}

		if (/*targetAngle > 134 || targetAngle < -134 || */closeInvert) {
			targetAngle = targetAngle + 180;
			inverse = -1;
		} else {
			inverse = 1;
		}

		if (Math.abs(actualPower) > 1)
			actualPower = actualPower / Math.abs(actualPower);

		diff = currentYAW - targetAngle;

		// SmartDashboard.putNumber("preAdjDiff", diff);

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

		// SmartDashboard.putNumber("adjustedDiff", diff);
		// SmartDashboard.putNumber("power", power);
		// SmartDashboard.putNumber("inverse", inverse);

		double turnAngle = IO.lookup(IO.COMPASS_ANGLE, IO.POWER_AXIS, Math.abs(actualPower));
		// SmartDashboard.putNumber("turnAngle", turnAngle);

		if (diff > turnAngle) {
			tankDrive(-actualPower, actualPower);
		} else if (diff < -turnAngle) {
			tankDrive(actualPower, -actualPower);
		} else {
			adj = diff * .05; // was .02 compass drive P value (for driving
								// straight) pretty low, but only increase if
								// necessary
			// SmartDashboard.putNumber("adjustment", adj);

			// power = power * inverse;
			double lnew = actualPower * inverse - adj;
			double rnew = actualPower * inverse + adj;

			double max = Math.max(Math.abs(lnew), Math.abs(rnew));
			if (max == 0){
				lnew = 0;
				rnew = 0;
			} else if (max > actualPower) {
				lnew /= max;
				rnew /= max;
				lnew *= actualPower;
				rnew *= actualPower;
			}

			tankDrive(lnew, rnew);
			prevCompassLpwr = lnew;
			prevCompassRpwr = rnew;
			prevEncoderCt = getDistance();
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

	public void shooterAlign(double cameraAngle, double botAngle, boolean farTurn) {
		// Moves shooter to the camera's position
		double P_VAL, MAX_PWR;
		if(IO.COMP){
			P_VAL = 0.1;								 // was .1 4/1/2016
			MAX_PWR = 0.45;
		} else {
			P_VAL = 0.1;
			MAX_PWR = 0.25;
		}
		
		//if we are using this for uTurns
		if(farTurn){
			if(IO.COMP){
				P_VAL = 0.05;
				MAX_PWR = 0.5;
			} else {
				P_VAL = 0.03;
				MAX_PWR = 0.35;
			}
		}
		
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

		slowPower = diff * P_VAL; // align to goal (vision) P value; could be
									// increased by .05 or so

		if (slowPower > MAX_PWR) { // max power levels, consider increasing if no
								// movement at large angles
			slowPower = MAX_PWR;
		} else if (slowPower < -MAX_PWR) {
			slowPower = -MAX_PWR;
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
		return (lEncoder.getDistance());  // + rEncoder.getDistance()) / 2;
	}

}
