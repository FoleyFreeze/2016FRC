package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;

public class Shooter {
	Talon shooter;
	Talon shooterwheels;
	Encoder shootencoder;
	boolean dungoofed;
	boolean nextposition;
	DigitalInput gatherdistance;
	boolean release;

	CANTalon shooter;
	CANTalon shooterarm;
	int state;

	static final double ARM_DOWN = 446;
	static final double ARM_UP = 893;
	static final double ARM_CLOSE = 10;

	public void shooterStateMachine() {
		state = 1;
		// determines initial position//
		switch (state) {
		case 1:
			shooter.set(0.7);
			shooterarm.setPosition(ARM_DOWN);
			// encoder count = setPosition()//
			if (shooterarm.getPosition() > (ARM_DOWN - ARM_CLOSE) && shooterarm.getPosition() < (ARM_DOWN + ARM_CLOSE)
					&& shooterdistance.get()) {
				state = 2;
			}
			break;

		default:
			break;
		case 2:
			shooterarm.setPosition(ARM_UP);
			if (shooterarm.getPosition() > (ARM_UP - ARM_CLOSE) && shooterarm.getPosition() < (ARM_UP + ARM_CLOSE)) {
				state = 2;
				}
				break;
			} else {
				shooter.set(.07);

				break;
			}
		case 3:
			if (shooterarm.equals(1339)) {
				state = 3;
				break;
			} else {
				shooter.set(.07);
				break;

			}

		}	
}
