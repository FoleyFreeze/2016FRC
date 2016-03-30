package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//import edu.wpi.first.wpilibj.CANTalon;

public class BoulderController {

	// shooter positions (high to low)
	static double SHOOTER_MAX_HEIGHT = 872; // shooting from 859 on 3.24
	double SHOOTER_STOW_POS = SHOOTER_MAX_HEIGHT - 345; // 3.28
	double SHOOTER_FARSHOT_POS = SHOOTER_MAX_HEIGHT - 17;
	static double SHOOTER_MIN_VOLT_SWITCH = SHOOTER_MAX_HEIGHT - 50;
	double SHOOTER_LAYUP_POS = SHOOTER_MAX_HEIGHT - 85; // 45;
	double SHOOTER_PRELOAD_POS = SHOOTER_MAX_HEIGHT - 452; // 3.28 was 431
	double SHOOTER_LOAD_POS = SHOOTER_MAX_HEIGHT - 468; // was 448

	// gatherer positions (low to high)
	static double GATHER_FULLDOWN_POS = 617;
	double GATHER_LOAD_SHOOTER_POS = GATHER_FULLDOWN_POS + 15;
	double GATHER_INTAKE_POS = GATHER_FULLDOWN_POS + 100; // 3.28 was 86
	static double GATHER_STOW_POS = GATHER_FULLDOWN_POS + 225; // 3.28 was 333
	double GATHER_LAYUP_POS = GATHER_FULLDOWN_POS + 333;// 3.28
	double GATHER_FARSHOT_POS = GATHER_FULLDOWN_POS + 333;// 3.28

	// defense positions
	double SHOOTER_LOWBAR_POS = SHOOTER_MAX_HEIGHT - 512; // was 470
	double GATHER_LOWBAR_POS = GATHER_FULLDOWN_POS;
	double SHOOTER_PORT_POS = SHOOTER_LAYUP_POS;
	double GATHER_PORT_POS = GATHER_FULLDOWN_POS;
	double SHOOTER_SALLY_UP = SHOOTER_LAYUP_POS;
	double SHOOTER_SALLY_DOWN = SHOOTER_LAYUP_POS;
	double GATHER_SALLY_POS = GATHER_FULLDOWN_POS;
	double GATHER_FLIPPY_FLOPPIES_POS = GATHER_FULLDOWN_POS;
	double SHOOTER_FLIPPY_FLOPPIES_UP = SHOOTER_LAYUP_POS;
	double SHOOTER_FLIPPY_FLOPPIES_DOWN = SHOOTER_LAYUP_POS;
	double GATHER_DRAWBRIDGE_POS = GATHER_FULLDOWN_POS;
	double SHOOTER_DRAWBRIDGE_DOWN = SHOOTER_LAYUP_POS;
	double SHOOTER_DRAWBRIDGE_UP = SHOOTER_LAYUP_POS;

	// interference positions
	double GATHER_HIGH = GATHER_STOW_POS + 10;
	double GATHER_LOW = GATHER_FULLDOWN_POS;
	double SHOOTER_HIGH = SHOOTER_PRELOAD_POS + 140;
	double SHOOTER_LOW = SHOOTER_LOAD_POS;
	
	double jogoffset = 0;
	double JOGNUMBER = 5;
	double visionAngleOffset = 0;

	DigitalInput ballSensor;

	DriveTrain drive;
	Shooter shooter;
	Gatherer gatherer;
	Timer time;

	PowerDistributionPanel pdp;

	public BoulderController(PowerDistributionPanel pdp, DriveTrain drive) {
		this.pdp = pdp;
		shooter = new Shooter(pdp);
		gatherer = new Gatherer();
		time = new Timer();
		time.start();
		ballSensor = new DigitalInput(IO.SHOOTER_BALL_SENSOR);
		this.drive = drive;
	}

	int buttonState = -1;

	boolean prevFire = false;

	public void runBC(Joystick driverstation) {
		// Sets all buttons to premade positions for gatherer and shooter arm
		gatherer.aquireShooterPosition(shoottogather(shooter.getPosition()));
		shooter.aquireGatherPosition(gathertoshoot(gatherer.getPosition()));

		if (driverstation.getRawButton(IO.LAYUP))
			buttonState = 0;
		else if (driverstation.getRawButton(IO.STOW))
			buttonState = 1;
		else if (driverstation.getRawButton(IO.FAR_SHOT))
			buttonState = 2;
		else if (driverstation.getRawButton(IO.GATHER)) {
			gatherState = 1;
			buttonState = 3;
			primeState = 0;
		} else if (driverstation.getRawButton(IO.LOWBAR)) {
			buttonState = 7;
			// } else if (driverstation.getRawButton(IO.PORT)) {
			// button = 8;
		} else if (driverstation.getRawButton(IO.SALLYPORT))
			buttonState = 4;
		else if (driverstation.getRawButton(IO.FLIPPY_DE_LOS_FLOPPIES)) {
			flippyFloppies();
			buttonState = 5;
			// } else if (driverstation.getRawButton(IO.DRAWBRIDGE)) {
			// button = 6;
		} else {
			chevalState = 0;
		}

		if (buttonState == 0) {
			// set positions to lay up on gatherer and shooter arms//
			layup();
			gatherState = 1;
			chevalState = 0;
		}

		else if (buttonState == 1) {
			// set positions to stow on gatherer and shooter arms//
			stow();
			gatherState = 1;
			chevalState = 0;
		}

		else if (buttonState == 2) {
			// set positions to far shot on gatherer and shooter arms//
			farShot();
			gatherState = 1;
			chevalState = 0;
		}

		else if (buttonState == 3) {
			// set positions to gather on gatherer and shooter arms//
			gather();
			chevalState = 0;
		}

		else if (buttonState == 4) {
			sallyPort(driverstation.getRawButton(IO.SALLYPORT));
			gatherState = 1;
			chevalState = 0;
		} else if (buttonState == 5) {
			gatherState = 1;
		} else if (buttonState == 6) {
			// drawbridge(driverstation.getRawButton(IO.DRAWBRIDGE));
			gatherState = 1;
			chevalState = 0;
		} else if (buttonState == 7) {
			lowBar(driverstation.getRawButton(IO.LOWBAR), driverstation.getRawButton(IO.PORT));
			gatherState = 1;
			chevalState = 0;
		} else if (buttonState == 8) {
			portcullis(driverstation.getRawButton(IO.PORT));
			gatherState = 1;
			chevalState = 0;
		}

		if (driverstation.getRawButton(IO.PRIME)) {
			prime();
		} else {
			shooter.shooterWheelL.set(0);
			shooter.shooterWheelR.set(0);
		}

		if (driverstation.getRawButton(IO.FIRE)) {
			prevFire = true;
			shooter.fire();
			primeState = 0;
		} else if (prevFire) {
			prevFire = false;
			shooter.loadWheelL.set(0);
			shooter.loadWheelR.set(0);
		}

	}

	public void layup() {
		// sets shooter layup position and gatherer stow position
		shooter.gotoPosition(SHOOTER_LAYUP_POS + jogoffset);
		gatherer.gotoPosition(GATHER_LAYUP_POS);
		gatherer.gatherwheel(0);
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
		gatherer.gatherwheel(0);
	}

	public void farShot() {
		// sets shooter for far shot and gatherer to stow position
		shooter.gotoPosition(SHOOTER_FARSHOT_POS + jogoffset + visionAngleOffset);
		gatherer.gotoPosition(GATHER_FARSHOT_POS);
		gatherer.gatherwheel(0);
	}

	int gatherState = 1;

	public void gather() {
		// gatherState = 1;
		// lowers gatherer and shooter and gets ready to gather//
		switch (gatherState) {
		case 1:
			gatherer.gatherwheel(-1);
			gatherer.gotoPosition(GATHER_INTAKE_POS);
			shooter.gotoPosition(SHOOTER_LAYUP_POS);
			time.reset();
			gatherState = 11;
			break;

		case 11:
			gatherer.gatherwheel(-1);
			gatherer.gotoPosition(GATHER_INTAKE_POS);
			shooter.gotoPosition(SHOOTER_LAYUP_POS);

			if (checkForGatherCurrent() && time.get() >= 0.5) {
				gatherState = 12;
				time.reset();
			}
			break;

		case 12: // wait for ball to reach bumper
			gatherer.gatherwheel(-1);
			if (time.get() >= 0.5) {
				gatherState = 2;
			}
			break;

		case 2: // ball is under gatherer, move gatherer down to pick up ball
			gatherer.gatherArm.configPeakOutputVoltage(7.0, -6.0);
			gatherer.gotoPosition(GATHER_LOAD_SHOOTER_POS);
			gatherer.gatherwheel(-1);
			if (Math.abs(gatherer.gatherArm.getClosedLoopError()) < 3) {
				gatherer.gatherArm.configPeakOutputVoltage(7.0, -3.0);
				gatherState = 3;
				time.reset();
			}
			break;
		case 3: // center the ball
			gatherer.gotoPosition(GATHER_LOAD_SHOOTER_POS);
			shooter.gotoPosition(SHOOTER_PRELOAD_POS);
			shooter.setLoadWheels(1);


			if (/*Math.abs(shooter.shooterArm.getClosedLoopError()) < 7 && */time.get() > 0.7) {
				gatherState = 4;
				time.reset();
			}
			break;
		case 4: // load the ball into the shooter
			shooter.gotoPosition(SHOOTER_LOAD_POS);
			shooter.setLoadWheels(1);
			if (IO.COMP) {
				if (time.get() >= 1.0 /* || checkForLoadCurrent() */) { // was
																		// 2.0
					gatherState = 45;
					time.reset();
				}
			} else {
				if (time.get() >= 2.0 /* || checkForLoadCurrent() */) { // was
																		// 2.0
					gatherState = 45;
					time.reset();
				}
			}
			break;

		case 45:
			if (IO.COMP) {
				shooter.setLoadWheels(-0.4);
				gatherer.gatherwheel(0);
				if (time.get() > 0.3) {
					gatherState = 5;
				}
			} else {
				shooter.setLoadWheels(-0.6);
				gatherer.gatherwheel(0);
				if (time.get() > 0.2) {
					gatherState = 5;
				}
			}

		case 5: // back the ball up slightly
			if (IO.COMP) {
				shooter.setLoadWheels(-0.4);
				if (!ballSensor.get() || time.get() > 0.35) {
					gatherState = 55;
					time.reset();
				}
			} else {
				shooter.setLoadWheels(-0.6);
				if (/* !ballSensor.get() || */ time.get() > 0.15) {
					gatherState = 55;
					time.reset();
				}
			}
			break;

		case 55:
			if (IO.COMP) {
				shooter.setLoadWheels(-0.4);
				if (time.get() > 0.05) {
					gatherState = 6;
					time.reset();
				}
			} else {
				shooter.setLoadWheels(-0.6);
				if (time.get() > 0.05) {
					gatherState = 6;
					time.reset();
				}
			}
			break;

		case 6: // go to shooting position
			shooter.setLoadWheels(0);
			shooter.gotoPosition(SHOOTER_STOW_POS + 50);
			if (time.get() > 0.4) { // 3.28 was 1
				gatherState = 7;
				time.reset();
			}
			break;

		case 7:
			gatherer.gotoPosition(GATHER_STOW_POS + 15);
			if (time.get() > 0.7) { // 3.28 was 1
				gatherState = 8;
				time.reset();
			}
			break;

		case 8:
			shooter.gotoPosition(SHOOTER_STOW_POS);
			if (time.get() > 0.5) {
				gatherState = 9;
				time.reset();
			}
			break;

		case 9:
			gatherer.gotoPosition(GATHER_STOW_POS);
			break;
		}

		SmartDashboard.putNumber("gather state", gatherState);
	}

	public void scoreLow() {

	}

	public void lowBar(boolean lowBar, boolean port) {
		// folds shooter down and gatherer as low as possible to go under low
		// bar
		if (lowBar) {
			if (port) {
				shooter.gotoPosition(SHOOTER_LAYUP_POS);
				gatherer.gotoPosition(GATHER_LOWBAR_POS);
			} else {
				shooter.gotoPosition(SHOOTER_LOWBAR_POS);
				gatherer.gotoPosition(GATHER_LOWBAR_POS);
			}
		}
		gatherer.gatherwheel(0);
	}

	public void sallyPort(boolean sallyBtn) {
		// When the "sally" button is pressed, it will move down, otherwise it
		// will move up
		if (sallyBtn) {
			shooter.gotoPosition(SHOOTER_SALLY_DOWN);
		} else {
			shooter.gotoPosition(SHOOTER_SALLY_UP);
		}
		gatherer.gatherwheel(0);
	}

	public void portcullis(boolean portcullis) {
		// move shooter and gatherer to porticullis position
		shooter.gotoPosition(SHOOTER_PORT_POS);
		gatherer.gotoPosition(GATHER_PORT_POS);
		gatherer.gatherwheel(0);
	}

	static int chevalState = 0;

	public void flippyFloppies() {
		
		SmartDashboard.putNumber("chevalState", chevalState);
		
		switch (chevalState) {
		case 0:
			time.reset();
			drive.resetEncoders();
			chevalState = 1;
			break;
	
		case 1:
			//drive.compassDrive(0.3, drive.navX.getYaw(), false, 180.0);

			if (time.get() > 0.5 || Math.abs(drive.getDistance()) > 4){
				chevalState = 2;
				time.reset();
				drive.tankDrive(0, 0);
			}
			break;
			
		case 2:
			gatherer.gotoPosition(GATHER_FULLDOWN_POS);
			
			if (time.get() > 2) {
				chevalState = 3;
				time.reset();
			}
			break;
			
		case 3:
			drive.compassDrive(0.6, drive.navX.getYaw(), false, 0.0);
			
			if (time.get() > .75){
			chevalState = 4;
			time.reset();
			}
			break;
		
		case 4:
			drive.compassDrive(0.4, drive.navX.getYaw(), false, 0.0);
			gatherer.gotoPosition(GATHER_LAYUP_POS);;
			time.reset();
			break;
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
		gatherer.gatherwheel(0);
	}

	public double gathertoshoot(double gatherpos) {
		return ((gatherpos - GATHER_LOW) / (GATHER_HIGH - GATHER_LOW)) * (SHOOTER_HIGH - SHOOTER_LOW) + SHOOTER_LOW;
	}

	public double shoottogather(double shooterpos) {
		return ((shooterpos - SHOOTER_LOW) / (SHOOTER_HIGH - SHOOTER_LOW)) * (GATHER_HIGH - GATHER_LOW) + GATHER_LOW;

	}

	public boolean checkForGatherCurrent() {
		// double curr = pdp.getCurrent(IO.GATHERER) -
		// pdp.getCurrent(IO.GATHER_ARM);
		double curr = pdp.getCurrent(IO.GATHERER);
		return curr > 7;
	}

	public boolean checkForLoadCurrent() {
		double curr = pdp.getCurrent(IO.LOAD_WHEEL_L) + pdp.getCurrent(IO.LOAD_WHEEL_R);
		return curr > 5;
	}

	int primeState = 0;

	public void prime() {

		if (buttonState == 0) { // layup
			shooter.prime(0.6);
		} else if (buttonState == 2) { // farshot
			shooter.prime(0.68);
		} else {
			shooter.prime(0.68);
		}

	}
	
	boolean prevJogUp = false;
	boolean prevJogDown = false;
	
	public void jog(boolean jogUp, boolean jogDown) {
		// Adds a static amount to the shooter's position, up or down
		if (jogUp && !prevJogUp) {

			jogoffset += JOGNUMBER;
		} else if (jogDown && !prevJogDown) {

			jogoffset -= JOGNUMBER;
		}

		prevJogUp = jogUp;
		prevJogDown = jogDown;
	}
}
