package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Ultrasonic;

public class Gatherer {
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

	public void gatherStateMachine() {
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
			} else {
				gatherer.set(.07);

				break;
			}
		case 3:
			if (gatherarm.equals(1339)) {
				state = 3;
				break;
			} else {
				gatherer.set(.07);
				break;

			}

		}
	}
}