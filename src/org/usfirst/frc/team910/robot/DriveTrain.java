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

}
