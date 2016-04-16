package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//import edu.wpi.first.wpilibj.CANTalon;

public class BoulderController {

	
	// shooter positions (high to low)
	
	//static double SHOOTER_MAX_HEIGHT = 844 + 18; // Arm at 83 degrees gives this value (862p). 80 deg (prac bot stop) = 844p.
	//COMP BOT 83 degrees is 828 was 760... now 743... now 727
	//COMP BOT max height is 840
	static double SHOOTER_MAX_HEIGHT = 717;///was 813//831  //was 825 // was 727;  
	double SHOOTER_STOW_POS = SHOOTER_MAX_HEIGHT - 345; // 3.28
	double SHOOTER_FARSHOT_POS = SHOOTER_MAX_HEIGHT - 30;// was 5 //was -17 for prac
	static double SHOOTER_MIN_VOLT_SWITCH = SHOOTER_MAX_HEIGHT - 50;
	double SHOOTER_LAYUP_POS = SHOOTER_MAX_HEIGHT - 83;//83//was 80, but reduced speed //71 //63 // 73 //was 85; // 45; 
	double SHOOTER_PRELOAD_POS = SHOOTER_MAX_HEIGHT - 452; // 3.28 was 431
	double SHOOTER_LOAD_POS = SHOOTER_MAX_HEIGHT - 480; // was 468 for prac bot 							was 473 comp 

	// gatherer positions (low to high)
	
	//static double GATHER_FULLDOWN_POS = 626; //PRACTICE was 617  LOWER numbers when gatherer is LOWER
	//static double GATHER_SETPOINT_POS = 626;
	static double GATHER_FULLDOWN_POS = 475; //COMP BOT
	static double GATHER_SETPOINT_POS = 469; //COMP BOT
	
	double GATHER_LOAD_SHOOTER_POS = GATHER_SETPOINT_POS + 40;//was 12
	double GATHER_INTAKE_POS = GATHER_SETPOINT_POS + 90; // 3.28 was 86 3.30 was 100
	static double GATHER_STOW_POS = GATHER_SETPOINT_POS + 225; // 3.28 was 333
	double GATHER_LAYUP_POS = GATHER_SETPOINT_POS + 333;// 3.28
	double GATHER_FARSHOT_POS = GATHER_SETPOINT_POS + 333;// 3.28

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
	AnalogInput ballDistSensor;

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
		ballDistSensor = new AnalogInput(IO.BALL_SENSOR_ANALOG);
		ballDistSensor.setAverageBits(2);
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
		else if (driverstation.getRawButton(IO.GATHER) && !driverstation.getRawButton(IO.CLIMB_2)) {
			gatherState = 1;
			buttonState = 3;
			primeState = 0;
		} else if (driverstation.getRawButton(IO.GATHER) && driverstation.getRawButton(IO.CLIMB_2)) {
			buttonState = 9;
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
			regrippingState = 0;
		}

		else if (buttonState == 1) {
			// set positions to stow on gatherer and shooter arms//
			stow();
			gatherState = 1;
			chevalState = 0;
			regrippingState = 0;
		}

		else if (buttonState == 2) {
			// set positions to far shot on gatherer and shooter arms//
			farShot();
			gatherState = 1;
			chevalState = 0;
			regrippingState = 0;
		}

		else if (buttonState == 3) {
			// set positions to gather on gatherer and shooter arms//
			gather();
			chevalState = 0;
			regrippingState = 0;
		}

		else if (buttonState == 4) {
			sallyPort(driverstation.getRawButton(IO.SALLYPORT));
			gatherState = 1;
			chevalState = 0;
			regrippingState = 0;
		} else if (buttonState == 5) {
			gatherState = 1;
			regrippingState = 0;
		} else if (buttonState == 6) {
			// drawbridge(driverstation.getRawButton(IO.DRAWBRIDGE));
			gatherState = 1;
			chevalState = 0;
			regrippingState = 0;
		} else if (buttonState == 7) {
			lowBar(driverstation.getRawButton(IO.LOWBAR), driverstation.getRawButton(IO.PORT));
			gatherState = 1;
			chevalState = 0;
			regrippingState = 0;
		} else if (buttonState == 8) {
			portcullis(driverstation.getRawButton(IO.PORT));
			gatherState = 1;
			chevalState = 0;
			regrippingState = 0;
		} else if (buttonState == 9){
			gatherState = 1;
			chevalState = 0;
			regrip();
		}

		if (driverstation.getRawButton(IO.PRIME)) {
			prime();
		} else {
			shooter.shooterWheelL.set(0);
			shooter.shooterWheelR.set(0);
		}

		if (driverstation.getRawButton(IO.FIRE) && !driverstation.getRawButton(IO.CLIMB_2)) {
			prevFire = true;
			shooter.fire();
			Robot.vp.closeCamera();
			primeState = 0;
		} else if (driverstation.getRawButton(IO.FIRE) && driverstation.getRawButton(IO.CLIMB_2)) {
			prevFire = true;
			shooter.backupFire();
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
	boolean stoppedbydist = false;

	public void gather() {
		// gatherState = 1;
		// lowers gatherer and shooter and gets ready to gather/
		double ballDist = ballDistSensor.getAverageVoltage();
		SmartDashboard.putNumber("BallDist", ballDist);
		switch (gatherState) {
		case 1:
			gatherer.gatherwheel(-1);
			gatherer.gotoPosition(GATHER_INTAKE_POS);
			shooter.gotoPosition(SHOOTER_LAYUP_POS);
			time.reset();
			gatherState = 11;
			stoppedbydist = false;
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
				time.reset();
			}
			break;

		case 2: // ball is under gatherer, move gatherer down to pick up ball
			gatherer.gatherArm.configPeakOutputVoltage(7.0, -4.0);
			gatherer.gotoPosition(GATHER_LOAD_SHOOTER_POS);
			gatherer.gatherwheel(-1);
			if (Math.abs(gatherer.gatherArm.getClosedLoopError()) < 4 || time.get() > 0.5) {
				gatherer.gatherArm.configPeakOutputVoltage(7.0, -3.5);
				gatherer.gatherArm.ClearIaccum();
				gatherState = 3;
				time.reset();
			}
			break;
		case 3: // center the ball
			gatherer.gotoPosition(GATHER_LOAD_SHOOTER_POS);
			shooter.gotoPosition(SHOOTER_PRELOAD_POS);
			shooter.setLoadWheels(0.65);


			if (/*Math.abs(shooter.shooterArm.getClosedLoopError()) < 7 && */time.get() > 0.7) {
				gatherState = 4;
				time.reset();
				shooter.gotoPosition(SHOOTER_LOAD_POS);
			} else if(ballDist > 0.61 && ballDist < 0.7){
				gatherState = 6;
				time.reset();
				gatherer.gatherwheel(0);
				stoppedbydist = true;
			}
			break;
			
		case 4: // load the ball into the shooter
			shooter.gotoPosition(SHOOTER_LOAD_POS);
			shooter.setLoadWheels(0.65);
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
			if(ballDist > 0.61 && ballDist < 0.7){
				gatherState = 6;
				time.reset();
				gatherer.gatherwheel(0);
				stoppedbydist = true;
			}
			break;
			
		case 45:
			if (IO.COMP) {
				shooter.setLoadWheels(-0.24); 											//was -0.25
				gatherer.gatherwheel(0);
				if (time.get() > 0.35) {
					gatherState = 6;
				}
			} else {
				shooter.setLoadWheels(-0.6);
				gatherer.gatherwheel(0);
				if (time.get() > 0.2) {    
					gatherState = 6;
				}
			}
			break;
			
		case 6: // go to shooting position
			shooter.setLoadWheels(0);
			shooter.gotoPosition(SHOOTER_STOW_POS + 50);
			if (time.get() > 1.0) { // 3.28 was 1
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
		SmartDashboard.putBoolean("distdetect", stoppedbydist);
	}
	
	int regrippingState = 0;	

	public void regrip () {
		//regrippingState = 1;		
		//regrips after portcullis and lowbar
			
		switch (regrippingState) {
		case 0:
			gatherer.gotoPosition(GATHER_LOAD_SHOOTER_POS);
			shooter.gotoPosition(SHOOTER_PRELOAD_POS);
			shooter.setLoadWheels(-0.65);
			time.reset();
			regrippingState = 1;
			break;
			
		case 1:
				// unload ball for pickup
			gatherer.gotoPosition(GATHER_LOAD_SHOOTER_POS);
			shooter.gotoPosition(SHOOTER_PRELOAD_POS);
			shooter.setLoadWheels(-0.65);
			if(time.get() > .5){
				regrippingState = 2;
				time.reset();
			}
			break;
	
	
		case 2: // center the ball
			gatherer.gotoPosition(GATHER_LOAD_SHOOTER_POS);
			shooter.gotoPosition(SHOOTER_PRELOAD_POS);
			shooter.setLoadWheels(0.65);
	
	
			if (/*Math.abs(shooter.shooterArm.getClosedLoopError()) < 7 && */time.get() > 0.7) {
				regrippingState = 3;
				time.reset();
				shooter.gotoPosition(SHOOTER_LOAD_POS);
			} else if(!ballSensor.get()){
				regrippingState = 5;
				time.reset();
				gatherer.gatherwheel(0);
			}
			break;
			
		case 3: // load the ball into the shooter
			shooter.gotoPosition(SHOOTER_LOAD_POS);
			shooter.setLoadWheels(0.65);
			if (IO.COMP) {
				if (time.get() >= 1.0 /* || checkForLoadCurrent() */) { // was
																		// 2.0
					regrippingState= 35;
					time.reset();
				}
			} else {
				if (time.get() >= 2.0 /* || checkForLoadCurrent() */) { // was
																		// 2.0
					regrippingState = 35;
					time.reset();
				}
			}
			if(!ballSensor.get()){
				regrippingState = 5;
				time.reset();
				gatherer.gatherwheel(0);
			}
			break;
			
		case 35:
			if (IO.COMP) {
				shooter.setLoadWheels(-0.25);
				gatherer.gatherwheel(0);
				if (time.get() > 0.4) {
					regrippingState = 5;
				}
			} else {
				shooter.setLoadWheels(-0.6);
				gatherer.gatherwheel(0);
				if (time.get() > 0.2) {
					regrippingState = 4;
				}
			}
			break;
			
			/*
		case 4: // back the ball up slightly
			gatherer.gatherwheel(0);
			if (IO.COMP) {
				shooter.setLoadWheels(0.6);
				if (ballSensor.get() || time.get() > 0.35) {
					regrippingState = 5;
					time.reset();
				}
			} else {
				shooter.setLoadWheels(1.0);	
				if (/*ballSensor.get() ||  /time.get() > 0.2) {   //was .15  3.30 MrC
					regrippingState = 45;
					time.reset();
				}
			}
			break;
			
		case 45:
			if (IO.COMP) {
				shooter.setLoadWheels(-0.4);
				if (time.get() > 0.05) {
					regrippingState = 5;
					time.reset();
				}
			} else {
				shooter.setLoadWheels(0.6);
				if (time.get() > 0.05) {
					regrippingState = 5;
					time.reset();
				}
			}
			break;
			*/
		case 5: // go to shooting position
			shooter.setLoadWheels(0);
			shooter.gotoPosition(SHOOTER_STOW_POS + 50);
			if (time.get() > 1.0) { // 3.28 was 1
				regrippingState = 6;
				time.reset();
			}
			break;
	
		case 6:
			gatherer.gotoPosition(GATHER_STOW_POS + 15);
			if (time.get() > 0.7) { // 3.28 was 1
				regrippingState = 7;
				time.reset();
			}
			break;
	
		case 7:
			shooter.gotoPosition(SHOOTER_STOW_POS);
			if (time.get() > 0.5) {
				regrippingState = 8;
				time.reset();
			}
			break;
	
		case 8:
			gatherer.gotoPosition(GATHER_STOW_POS);
			break;
		}
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
		//drive INTO cheval, let the robot fall off, then press and hold
		//make sure gatherer is facing defense, this drives in reverse
		SmartDashboard.putNumber("chevalState", chevalState);
		
		switch (chevalState) {
		case 0:
			time.reset();
			drive.resetEncoders();
			chevalState = 1;
			break;
	
		case 1:
			//drive.compassDrive(0.3, drive.navX.getYaw(), false, 180.0);
			// not in auton so drive into it manually
			if (time.get() > 0.5 || Math.abs(drive.getDistance()) > 4){
				chevalState = 2;
				time.reset();
				drive.tankDrive(0, 0);
			}
			break;
			
		case 2: //bring down gatherer to bring down the panel
			gatherer.gotoPosition(GATHER_FULLDOWN_POS);
			
			if (time.get() > 2) {
				chevalState = 3;
				time.reset();
			}
			break;
			
		case 3: //drive over the cheval
			drive.compassDrive(0.6, drive.navX.getYaw(), false, 0.0);
			
			if (time.get() > .75){
			chevalState = 4;
			time.reset();
			}
			break;
		
		case 4: //continue driving and put gatherer back up
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
			shooter.prime(0.55, true); //was 0.6
		} else if (buttonState == 2) { // farshot
			shooter.prime(0.68, false);
		} else {
			shooter.prime(0.68, false);
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
	public void aimThenFire(double getAngle, boolean rTrigger){		//Concept for align and fire with right trigger Steven C 4/16
		getAngle = Robot.vp.getAngle();
		
		if (rTrigger){
			if(getAngle <= 0.5){
				drive.shooterAlign(uDriveTargetAngle, navX.getYaw(), true);
				shooter.prime(shooterSpeed, layup);
				shooter.fire();
			}
		}
	}
}
	