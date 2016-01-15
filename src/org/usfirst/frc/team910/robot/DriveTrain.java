package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.Encoder;
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

	public void tankDrive(double yAxisLeft, double yAxisRight) {
		lmTalon.set(yAxisLeft);
		rmTalon.set(yAxisRight);

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
