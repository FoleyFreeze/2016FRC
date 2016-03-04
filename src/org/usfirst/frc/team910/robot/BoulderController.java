package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;

//import edu.wpi.first.wpilibj.CANTalon;

public class BoulderController {

	// shooter positions (high to low)
	double SHOOTER_STOW_POS = 0;
	double SHOOTER_FARSHOT_POS = 0;
	double SHOOTER_LAYUP_POS = 0;
	double SHOOTER_PRELOAD_POS = 0;
	double SHOOTER_LOAD_POS = 0;

	double GATHER_STOW_POS = 0;
	double GATHER_INTAKE_POS = 0;
	double GATHER_LOAD_SHOOTER_POS = 0;
	double GATHER_FULLDOWN_POS = 0;

	// defense positions
	double SHOOTER_LOWBAR_POS = 0;
	double GATHER_LOWBAR_POS = GATHER_FULLDOWN_POS;
	double SHOOTER_PORT_POS = 0;
	double GATHER_PORT_POS = 0;
	double SHOOTER_SALLY_UP = 0;
	double SHOOTER_SALLY_DOWN = 0;
	double GATHER_SALLY_POS = 0;
	double GATHER_FLIPPY_FLOPPIES_POS = 0;
	double SHOOTER_FLIPPY_FLOPPIES_UP = 0;
	double SHOOTER_FLIPPY_FLOPPIES_DOWN = 0;
	double GATHER_DRAWBRIDGE_POS = 0;
	double SHOOTER_DRAWBRIDGE_DOWN = 0;
	double SHOOTER_DRAWBRIDGE_UP = 0;

	// interference positions
	double GATHER_HIGH;
	double GATHER_LOW;
	double SHOOTER_HIGH;
	double SHOOTER_LOW;

	Shooter shooter;
	Gatherer gatherer;
	Timer time;

	PowerDistributionPanel pdp;

	public BoulderController(PowerDistributionPanel pdp) {
		this.pdp = pdp;
		shooter = new Shooter();
		gatherer = new Gatherer();
		time = new Timer();
		time.start();
	}

	double button = -1;

	public void runBC(Joystick driverstation) {
		// Sets all buttons to premade positions for gatherer and shooter arm
		gatherer.aquireShooterPosition(shoottogather(shooter.getPosition()));
		shooter.aquireGatherPosition(gathertoshoot(gatherer.getPosition()));

		if (driverstation.getRawButton(IO.LAYUP))
			button = 0;
		else if (driverstation.getRawButton(IO.STOW))
			button = 1;
		else if (driverstation.getRawButton(IO.FAR_SHOT))
			button = 2;
		else if (driverstation.getRawButton(IO.GATHER))
			button = 3;
		else if (driverstation.getRawButton(IO.LOWBAR)) {
			button = 7;
		} else if (driverstation.getRawButton(IO.PORT)) {
			button = 8;
		} else if (driverstation.getRawButton(IO.SALLYPORT))
			button = 4;
		else if (driverstation.getRawButton(IO.FLIPPY_DE_LOS_FLOPPIES)) {
			button = 5;
		} else if (driverstation.getRawButton(IO.DRAWBRIDGE)) {
			button = 6;
		}

		if (button == 0) {
			// set positions to lay up on gatherer and shooter arms//
			layup();
			gatherState = 1;
		}

		else if (button == 1) {
			// set positions to stow on gatherer and shooter arms//
			stow();
			gatherState = 1;
		}

		else if (button == 2) {
			// set positions to far shot on gatherer and shooter arms//
			farShot();
			gatherState = 1;
		}

		else if (button == 3) {
			// set positions to gather on gatherer and shooter arms//
			gather();
		}

		else if (button == 4) {
			sallyPort(driverstation.getRawButton(IO.SALLYPORT));
			gatherState = 1;
		} else if (button == 5) {
			flippyFloppies(driverstation.getRawButton(IO.FLIPPY_DE_LOS_FLOPPIES));
			gatherState = 1;
		} else if (button == 6) {
			drawbridge(driverstation.getRawButton(IO.DRAWBRIDGE));
			gatherState = 1;
		} else if (button == 7) {
			lowBar(driverstation.getRawButton(IO.LOWBAR));
			gatherState = 1;
		} else if (button == 8) {
			portcullis(driverstation.getRawButton(IO.PORT));
			gatherState = 1;
		}

		if (driverstation.getRawButton(IO.PRIME)) {
			shooter.prime();
		}

		if (driverstation.getRawButton(IO.FIRE)) {
			shooter.fire();
		}

	}

	public void layup() {
		// sets shooter layup position and gatherer stow position
		shooter.gotoPosition(SHOOTER_LAYUP_POS);
		gatherer.gotoPosition(GATHER_STOW_POS);
	}

	public void stow() {
		// make sure shooter is not in the way of the gatherer
		// boolean if in the way
		// if in way move shooter arm
		// if not move both to stow positions
		// gather retreats upwards to fit inside the bumper
		// shooter arm rotates to fit inside the bumper
		shooter.gotoPosition(SHOOTER_STOW_POS);
		gatherer.gotoPosition(GATHER_STOW_POS);

	}

	public void farShot() {
		// sets shooter for far shot and gatherer to stow position
		shooter.gotoPosition(SHOOTER_FARSHOT_POS);
		gatherer.gotoPosition(GATHER_STOW_POS);
	}

	int gatherState = 1;

	public void gather() {
		// gatherState = 1;
		// lowers gatherer and shooter and gets ready to gather//
		switch (gatherState) {
		case 1:
			gatherer.gatherwheel(1);
			gatherer.gotoPosition(GATHER_INTAKE_POS);
			shooter.gotoPosition(SHOOTER_LAYUP_POS);

			if (checkForGatherCurrent()) {
				gatherState = 11;
				time.reset();
			}
			break;

		case 11: // wait for ball to reach bumper
			gatherer.gatherwheel(1);
			if (time.get() >= 0.5) {
				gatherState = 2;
			}
			break;

		case 2: // ball is under gatherer, move gatherer down to pick up ball
			gatherer.gotoPosition(GATHER_FULLDOWN_POS);
			gatherer.gatherwheel(1);
			if (Math.abs(gatherer.gatherArm.getClosedLoopError()) < 10) {
				gatherState = 3;
				time.reset();
			}
			break;
		case 3: // center the ball
			gatherer.gotoPosition(GATHER_LOAD_SHOOTER_POS);
			shooter.gotoPosition(SHOOTER_PRELOAD_POS);
			shooter.setLoadWheels(1);
			if (time.get() >= 3) {
				gatherState = 4;
				time.reset();
			}
			break;
		case 4: // load the ball into the shooter
			shooter.gotoPosition(SHOOTER_LOAD_POS);
			shooter.setLoadWheels(1);
			if (time.get() >= 2 || checkForLoadCurrent()) {
				gatherState = 5;
				time.reset();
			}
			break;

		case 5: // back the ball up slightly
			shooter.setLoadWheels(-0.5);
			if (time.get() >= 0.5) {
				gatherState = 6;
			}
			break;

		case 6: // go to shooting position
			shooter.gotoPosition(SHOOTER_LAYUP_POS);
			gatherer.gotoPosition(GATHER_STOW_POS);
			break;
		}
	}

	public void scoreLow() {

	}

	public void lowBar(boolean lowBar) {
		// folds shooter down and gatherer as low as possible to go under low
		// bar
		shooter.gotoPosition(SHOOTER_LOWBAR_POS);
		gatherer.gotoPosition(GATHER_LOWBAR_POS);
	}

	public void sallyPort(boolean sallyBtn) {
		// When the "sally" button is pressed, it will move down, otherwise it
		// will move up
		if (sallyBtn) {
			shooter.gotoPosition(SHOOTER_SALLY_DOWN);
		} else {
			shooter.gotoPosition(SHOOTER_SALLY_UP);
		}
	}

	public void portcullis(boolean portcullis) {
		// move shooter and gatherer to porticullis position
		shooter.gotoPosition(SHOOTER_PORT_POS);
		gatherer.gotoPosition(GATHER_PORT_POS);
	}

	public void flippyFloppies(boolean flippyBtn) {
		// moves gatherer to Cheval de Frise Position
		// If the button is active, move the shooter down, otherwise shooter
		// goes up
		gatherer.gotoPosition(GATHER_FLIPPY_FLOPPIES_POS);
		if (flippyBtn) {
			shooter.gotoPosition(SHOOTER_FLIPPY_FLOPPIES_DOWN);
		} else {
			shooter.gotoPosition(SHOOTER_FLIPPY_FLOPPIES_UP);
		}
	}

	public void drawbridge(boolean drawbridgeBtn) {
		// sets gatherer to drawbridge position
		// if button is active, shooter goes down, otherwise shooter goes up
		gatherer.gotoPosition(GATHER_DRAWBRIDGE_POS);
		if (drawbridgeBtn) {
			shooter.gotoPosition(SHOOTER_DRAWBRIDGE_DOWN);
		} else {
			shooter.gotoPosition(SHOOTER_DRAWBRIDGE_UP);
		}
	}

	public double gathertoshoot(double gatherpos) {
		return ((gatherpos - GATHER_LOW) / (GATHER_HIGH - GATHER_LOW)) * (SHOOTER_HIGH - SHOOTER_LOW) + SHOOTER_LOW;
	}

	public double shoottogather(double shooterpos) {
		return ((shooterpos - SHOOTER_LOW) / (SHOOTER_HIGH - SHOOTER_LOW)) * (GATHER_HIGH - GATHER_LOW) + GATHER_LOW;

	}

	public boolean checkForGatherCurrent() {
		double curr = pdp.getCurrent(IO.GATHERER);
		return curr > 5;
	}

	public boolean checkForLoadCurrent() {
		double curr = pdp.getCurrent(IO.LOAD_WHEEL_L) + pdp.getCurrent(IO.LOAD_WHEEL_R);
		return curr > 5;
	}
}
