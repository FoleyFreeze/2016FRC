package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;

public class DriveTrain {
	
	Talon lmTalon;
	Talon rmTalon;

	Encoder lEncoder;
	Encoder rEncoder;

	public DriveTrain() {
		lmTalon = new Talon(0);
		rmTalon = new Talon(1);
		lEncoder = new Encoder(2, 3, false);
		rEncoder = new Encoder(4, 5, false);
	}

	public void tankDrive(double YAxisLeft, double YAxisRight) {
		lmTalon.set(YAxisLeft);
		rmTalon.set(YAxisRight);
	}

	// when ljoystick trigger is pressed get the intial encoder value
	// while held, contiually wheel position to intial value initiate
	// dynamicBreaking
	// if encoders change value, engage motor power to move back to held value
	// when reach held value stop
	// smart way, pid gain value to get to 0
	double startEncL, startEncR;

	public void dynamicBreaking(boolean firstTime) {

		if (firstTime) {
			startEncL = lEncoder.getDistance();
			startEncR = rEncoder.getDistance();

		} else {
			// actually do the thing
		}

	}

	public void driveStraight(double lpower) {

		double levalue;
		double revalue;

		levalue = lEncoder.get();
		revalue = rEncoder.get();

		double diff;

		diff = levalue - revalue;

		double adj = diff * 1;

		double lnew = lpower - adj;
		double rnew = lpower + adj;
		tankDrive(lnew, rnew);

	}

	public void run(double yAxisLeft, double yAxisRight, boolean sDrive, boolean dBrake){
		
		if (dBrake) {
			//Dynamic Braking Function//
		}
		
		else if  (sDrive) {
			//Straight Drive Function//
		}
		
		else {
			tankDrive(yAxisLeft, yAxisRight);
		}
		
	}

}
