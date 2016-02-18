package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.DigitalInput;

public class Gatherer {

	double PRIMESPEED = 10000;
	double LOAD = 0;
	double unjamspeed = -100;
	double restposition = 1;
	double gatherposition = 2;
	
	boolean GamepadRBumper;
	boolean brokenz;
	boolean dungoofed;
	boolean nextposition;
	DigitalInput gatherdistance;
	boolean release;

	CANTalon gatherer;
	CANTalon gatherarm;
	int state;

	static final double ARM_DOWN = 446;
	static final double ARM_UP = 893;
	static final double ARM_CLOSE = 10;

	

	public void gotoPosition(double position) {

	}

	public Gatherer() {
		gatherer = new CANTalon(IO.GATHERER);
		gatherarm = new CANTalon(IO.GATHER_ARM);
	}

	public void position(boolean loadin, boolean ballin) {

		if (loadin) {

			gatherarm.set(LOAD);
		} else if (ballin) {
			gatherarm.set(gatherposition);
		} else {
			gatherarm.set(0);
		}
	}

	public void gatherwheel(double speed) {
		gatherer.set(speed);
	}

	public boolean inTheWay() {
		return false;
	}

	public void manualGather(double YAxisGamePadRight) {

		gatherarm.changeControlMode(TalonControlMode.PercentVbus);

		gatherarm.set(YAxisGamePadRight);
		
		if (GamepadRBumper == true) {
			
			gatherer.set(PRIMESPEED);
			
		}
		else {
			
			gatherer.set(LOAD);
		}

	}

}
