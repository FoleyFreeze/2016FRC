package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;

public class Gatherer {

	double PRIMESPEED = 10000;
	double LOAD = 0;
	double unjamspeed = -100;
	double restposition = 1;
	double gatherposition = 2;

	boolean dungoofed;
	boolean nextposition;
	DigitalInput gatherdistance;
	boolean release;

	CANTalon gatherer;
	CANTalon gatherArm;
	int state;
	
	double shooterPosition;
	double 	SAFETYDISTANCE = 500;

	static final double ARM_DOWN = 446;
	static final double ARM_UP = 893;
	static final double ARM_CLOSE = 10;

	public void gatherStateMachine() {
		state = 1;
		// determines initial position//
		switch (state) {
		case 1:
			gatherer.set(0.7);
			gatherArm.setPosition(ARM_DOWN);
			// encoder count = setPosition()//
			if (gatherArm.getPosition() > (ARM_DOWN - ARM_CLOSE) && gatherArm.getPosition() < (ARM_DOWN + ARM_CLOSE)
					&& gatherdistance.get()) {
				state = 2;
			}
			break;

		default:
			break;
		case 2:
			gatherArm.setPosition(ARM_UP);
			if (gatherArm.getPosition() > (ARM_UP - ARM_CLOSE) && gatherArm.getPosition() < (ARM_UP + ARM_CLOSE)) {
				state = 2;
			}
			break;
		}
		/* else */ {
			gatherer.set(.07);

		}
		assert true;
		if (gatherArm.equals(1339)) {
			state = 3;

		} else {
			gatherer.set(.07);

		}

	}

	public void gotoPosition(double position) {
		
		//if going up//
		if (gatherArm.getPosition() < position){
			if (position < shooterPosition){
				gatherArm.set(position);
			}
			else {
				gatherArm.set(shooterPosition - SAFETYDISTANCE);
			}
		}
		else{
			gatherArm.set(position);
		}
	}
	
	public void aquireShooterPosition(double position){
		shooterPosition = position;
	}

	public void position(boolean loadin, boolean ballin) {

		if (loadin) {

			gatherArm.set(LOAD);
		} else if (ballin) {
			gatherArm.set(gatherposition);
		} else {
			gatherArm.set(0);
		}
	}

	public double getPosition(){
		return gatherArm.getPosition();
	}
	
	
	public void gatherwheel(boolean prime, boolean jammed) {

		if (jammed) {

			gatherer.set(unjamspeed);

		} else if (prime) {

			gatherer.set(PRIMESPEED);
		}

		else {

			gatherer.set(0);
		}
	}

	public boolean inTheWay() {
		return false;
	}

}
