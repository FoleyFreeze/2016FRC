package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.DigitalInput;

//import edu.wpi.first.wpilibj.CANTalon;

public class BoulderController {

	double SHOOT_LAYUP_POS = 0;
	double GATHER_LAYUP_POS = 0;
	double SHOOT_STOW_POS = 0;
	double GATHER_STOW_POS = 0;
	double SHOOT_FARSHOT_POS = 0;
	double GATHER_FARSHOT_POS = 0;
	double GATHER_INTAKE_POS = 0;
	double GATHER_OUTOFWAY_POS = 0;
	double SHOOTER_LOAD_POS = 0;

	// defense positions
	double SHOOTER_LOWBAR_POS = 0;
	double GATHER_LOWBAR_POS = 0;
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

	double GATHER_HIGH;
	double GATHER_LOW;
	double SHOOTER_HIGH;
	double SHOOTER_LOW;
	Shooter shooter;
	Gatherer gatherer;

	public BoulderController() {
		shooter = new Shooter();
		gatherer = new Gatherer();
	}

	DigitalInput gatherBallSensor;
	DigitalInput shooterBallSensor;

	double button = -1;

	public void runBC(boolean layupBtn, boolean stowBtn, boolean farShotBtn, boolean gatherBtn, boolean primeBtn,
			boolean fireBtn, boolean lowBarBtn, boolean portBtn, boolean sallyBtn, boolean flippyBtn,
			boolean drawbridgeBtn) {
		gatherer.aquireShooterPosition(shoottogather( shooter.getPosition()));
		shooter.aquireGatherPosition(gathertoshoot(gatherer.getPosition()));

		if (layupBtn)
			button = 0;
		else if (stowBtn)
			button = 1;
		else if (farShotBtn)
			button = 2;
		else if (gatherBtn)
			button = 3;
		else if (lowBarBtn) {
			button = 7;
		} else if (portBtn) {
			button = 8;
		} else if (sallyBtn)
			button = 4;
		else if (flippyBtn) {
			button = 5;
		} else if (drawbridgeBtn) {
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
			sallyPort(sallyBtn);
			gatherState = 1;
		} else if (button == 5) {
			flippyFloppies(flippyBtn);
			gatherState = 1;
		} else if (button == 6) {
			drawbridge(drawbridgeBtn);
			gatherState = 1;
		}
		else if(button == 7){
			lowBar(lowBarBtn);
			gatherState = 1;
		}else if(button == 8){
			portcullis(portBtn);
			gatherState = 1;
		}
		

		if (primeBtn) {
			shooter.prime();
		}

		if (fireBtn) {
			shooter.fire();
		}

	}

	public void layup() {
		shooter.gotoPosition(SHOOT_LAYUP_POS);
		gatherer.gotoPosition(GATHER_LAYUP_POS);
	}

	public void stow() {
		// make sure shooter is not in the way of the gatherer
		// boolean if in the way
		// if in way move shooter arm
		// if not move both to stow positions
		// gather retreats upwards to fit inside the bumper
		// shooter arm rotates to fit inside the bumper
		shooter.gotoPosition(SHOOT_STOW_POS);
		gatherer.gotoPosition(GATHER_STOW_POS);

	}

	public void farShot() {
		shooter.gotoPosition(SHOOT_FARSHOT_POS);
		gatherer.gotoPosition(GATHER_FARSHOT_POS);
	}

	int gatherState = 1;

	public void gather() {
		// gatherState = 1;
		// determines initial position//
		switch (gatherState) {
		case 1:
			gatherer.gatherwheel(0.7);
			gatherer.gotoPosition(GATHER_INTAKE_POS);
			shooter.gotoPosition(SHOOTER_LOAD_POS);

			if (gatherBallSensor.get()) {
				gatherState = 2;
			}
			break;

		case 2:
			if (gatherBallSensor.get()) {
				gatherer.gatherwheel(0);
				gatherer.gotoPosition(GATHER_OUTOFWAY_POS);
				shooter.gotoPosition(SHOOTER_LOAD_POS);
				gatherState = 3;
			} else {
				gatherer.gatherwheel(.07);
			}
			break;
		case 3:
			shooter.gotoPosition(GATHER_FARSHOT_POS);
			gatherer.gotoPosition(GATHER_FARSHOT_POS);
			if (shooterBallSensor.get()) {
				shooter.setLoadWheels(0);
				gatherState = 4;

			} else {
				shooter.setLoadWheels(.7);
				gatherState = 3;
			}
			break;
		case 4:

			break;
		}
	}

	public void scoreLow() {

	}

	public void lowBar(boolean lowBar) {
		shooter.gotoPosition(SHOOTER_LOWBAR_POS);
		gatherer.gotoPosition(GATHER_LOWBAR_POS);
	}

	public void sallyPort(boolean sallyBtn) {
		if (sallyBtn) {
			shooter.gotoPosition(SHOOTER_SALLY_DOWN);
		} else {
			shooter.gotoPosition(SHOOTER_SALLY_UP);
		}
	}

	public void portcullis(boolean portcullis) {
		shooter.gotoPosition(SHOOTER_PORT_POS);
		gatherer.gotoPosition(GATHER_PORT_POS);
	}

	public void flippyFloppies(boolean flippyBtn) {
		gatherer.gotoPosition(GATHER_FLIPPY_FLOPPIES_POS);
		if (flippyBtn) {
			shooter.gotoPosition(SHOOTER_FLIPPY_FLOPPIES_DOWN);
		} else {
			shooter.gotoPosition(SHOOTER_FLIPPY_FLOPPIES_UP);
		}
	}

	public void drawbridge(boolean drawbridgeBtn) {
		gatherer.gotoPosition(GATHER_DRAWBRIDGE_POS);
		if (drawbridgeBtn) {
			shooter.gotoPosition(SHOOTER_DRAWBRIDGE_DOWN);
		} else {
			shooter.gotoPosition(SHOOTER_DRAWBRIDGE_UP);
		}
	}

	public double gathertoshoot(double gatherpos){
		return  ((gatherpos - GATHER_LOW)/(GATHER_HIGH - GATHER_LOW))*(SHOOTER_HIGH - SHOOTER_LOW)+SHOOTER_LOW;		
	}

	public double shoottogather(double shooterpos){
		return ((shooterpos - SHOOTER_LOW)/(SHOOTER_HIGH - SHOOTER_LOW))*(GATHER_HIGH - GATHER_LOW)+ GATHER_LOW;
		
		
	}
}

