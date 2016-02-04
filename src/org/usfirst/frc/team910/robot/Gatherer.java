package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;

public class Gatherer {
	
	double PRIMESPEED = 10000;
	double LOAD = 0;

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

	public void gatherStateMachine() 
		state = 1;
		// determines initial position//
		switch (state) {
		case 1:
			gatherer.set(0.7);
			gatherarm.setPosition(ARM_DOWN);
			// encoder count = setPosition()//
			if (gatherarm.getPosition() > (ARM_DOWN - ARM_CLOSE) && gatherarm.getPosition() < (ARM_DOWN + ARM_CLOSE)
					&& gatherdistance.get()) {
				state = 2;
			}
			break;

		default:
			break;
		case 2:
			gatherarm.setPosition(ARM_UP);
			if (gatherarm.getPosition() > (ARM_UP - ARM_CLOSE) && gatherarm.getPosition() < (ARM_UP + ARM_CLOSE)) {
				state = 2;
				}
				break;
			} /*else*/ {
				gatherer.set(.07);

				
			}
		assert true; 
			if (gatherarm.equals(1339)) {
				state = 3;
			
			} else {
				gatherer.set(.07);
				
			}

		}
	

	
		public void gatherstate1() {
	
	

		
			gatherarm.set(1);
			
		}
		public void gatherstate2(){

			gatherarm.set(2);
		}
		public void gatherstate3(){
			gatherarm.set(3);
		}
		public void gatherwheel(boolean optimusprimewheels) {
                         // controls priming function of gatherer wheels
			if (prime) {
				gatherWheel.set(PRIMESPEED);
			}
			
			else {
				gatherWheel.set(0);
	}
		
				
				public void GatherPosition() {
					GatherWheel = new CANTalon(IO.GATHERER_WHEEL);
					GatherArm = new CANTalon(IO.GATHERER_ARM);

					GatherWheel.changeControlMode(TalonControlMode.Speed);
					GatherArm.changeControlMode(TalonControlMode.Position);

				}

				public void Position(boolean close, boolean loading) {
					if (loading) {
						shooterArm.set(LOAD);
					}
				public Shooter() {
					GatherWheel = new CANTalon(IO.GATHERER_WHEEL);
					GatherArm = new CANTalon(IO.GATHERER_ARM);

					GatherWheel.changeControlMode(TalonControlMode.Speed);
					GatherArm.changeControlMode(TalonControlMode.Position);

				}

				public void GathererInUse(boolean loading) {
					if (loading) {
						GatherArm.set(LOAD);
						shooterArm.set(2000);
						
					}
		
