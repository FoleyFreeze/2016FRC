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
	Shooter shooter;
	Gatherer gatherer;

	public BoulderController() {
		shooter = new Shooter();
		gatherer = new Gatherer();
	}
	int button;
	DigitalInput gatherBallSensor;
	DigitalInput shooterBallSensor;

	double button = -1;

	public void runBC(boolean layupBtn, boolean stowBtn, boolean farShotBtn, boolean gatherBtn, boolean primeBtn,
			boolean fireBtn) {
		gatherer.aquireShooterPosition(shooter.getPosition());
		shooter.aquireGatherPosition(gatherer.getPosition());	


		if(layupBtn) button = 0;
		else if (stowBtn) button = 1;
		else if (farShotBtn) button = 2;
		else if (gatherBtn) button = 3;
		
		
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

		if (primeBtn){
			prime();
		}
		
		if (fireBtn){
			fire();
		}

	}

	public void layup() {
		if (gatherer.inTheWay()) {
			gatherer.gotoPosition(GATHER_LAYUP_POS);
		} else {
			gatherer.gotoPosition(GATHER_LAYUP_POS);
			shooter.gotoPosition(SHOOT_LAYUP_POS);
		}
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

		if (shooter.inTheWay()) {
			shooter.gotoPosition(SHOOT_STOW_POS);

		} else {
			shooter.gotoPosition(SHOOT_STOW_POS);
			gatherer.gotoPosition(GATHER_STOW_POS);
		}

	}

	public void farShot() {
		if (gatherer.inTheWay()) {
			gatherer.gotoPosition(GATHER_FARSHOT_POS);
		} else {
			gatherer.gotoPosition(GATHER_FARSHOT_POS);
			shooter.gotoPosition(SHOOT_FARSHOT_POS);
		}
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
			}else{
			gatherer.gatherwheel(.07);}
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

	public void prime() {

	}

	public void fire() {

	}

	public void scoreLow() {

	}

	public void lowBar() {

	}

}
