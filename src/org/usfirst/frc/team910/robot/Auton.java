package org.usfirst.frc.team910.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Auton {
	AHRS navX;
	DriveTrain drive;
	BoulderController bc;
	boolean visionWorking = false;

	/*
	final String defaultAuto = "Do Nothing";
	final String crossCamShootAuto = "Cross, Camera, Shoot";
	final String justDriveAuto = "Just Drive Fwd";
	String autoSelected;
	SendableChooser chooser;
	*/
	
	//defenses
	/*
	 * rough terrain
	 * 
	 * moat
	 * rockwall
	 * ramparts
	 */
	
	String[] stationSelection = {"-1 ElimAuton (3 Ramparts)", "0 Do Nothing" , "1 Low Bar", "2nd Def", "3rd Def", "4th Def", "5th Def" };
	int stationIndex = -1;
	String[] defenseType = { "0 Drive Over", "1 Rough Terrain", "2 Moat", "3 Rock Wall", "4 Ramparts", "5 Cheval", "6 Portcullis" };
	int defenseIndex = 0;
	String[] extraCredit = { "0 No Modification", "1 No Shooting", "2 Spy Box" };
	int extraIndex = 0;
	
	public Auton(AHRS navX, DriveTrain drive, BoulderController bc, boolean visionWorking) {
		this.navX = navX;
		this.drive = drive;
		this.bc = bc;
		this.visionWorking = visionWorking;
	}

	Timer time = new Timer();
	int autonstate = 0;
	double cameraAngle = 0;
	
	boolean prevStationInc = false;
	boolean prevStationDec = false;
	boolean prevDefenseInc = false;
	boolean prevDefenseDec = false;
	boolean prevExtraInc = false;
	boolean prevExtraDec = false;
	
	public void selectAuto(Joystick joy) {
		if (joy.getRawButton(IO.AUTON_INC_SLOT)){
			if (!prevStationInc) stationIndex++;
			prevStationInc = true;
		} else {
			prevStationInc = false;
		}
		
		if (joy.getRawButton(IO.AUTON_DEC_SLOT)){
			if (!prevStationDec) stationIndex--;
			prevStationDec = true;
		} else {
			prevStationDec = false;
		}
		
		if (joy.getRawButton(IO.AUTON_INC_DEF)){
			if (!prevDefenseInc) defenseIndex++;
			prevDefenseInc = true;
		} else {
			prevDefenseInc = false;
		}
		
		if (joy.getRawButton(IO.AUTON_DEC_DEF)){
			if (!prevDefenseDec) defenseIndex--;
			prevDefenseDec = true;
		} else {
			prevDefenseDec = false;
		}
		
		if (joy.getRawButton(IO.AUTON_INC_EXTRA)){
			if (!prevExtraInc) extraIndex++;
			prevExtraInc = true;
		} else {
			prevExtraInc = false;
		}
		
		if (joy.getRawButton(IO.AUTON_DEC_EXTRA)){
			if (!prevExtraDec) extraIndex--;
			prevExtraDec = true;
		} else {
			prevExtraDec = false;
		}
		
		if(stationIndex < -1) stationIndex = -1;
		if(stationIndex >= stationSelection.length-1) stationIndex = stationSelection.length - 2;
		if(defenseIndex < 0) defenseIndex = 0;
		if(defenseIndex >= defenseType.length) defenseIndex = defenseType.length - 1;
		if(extraIndex < 0) extraIndex = 0;
		if(extraIndex >= extraCredit.length) extraIndex = extraCredit.length - 1;
		
		SmartDashboard.putString("Auton Station", stationSelection[stationIndex+1]);
		SmartDashboard.putString("Auton Defense", defenseType[defenseIndex]);
		SmartDashboard.putString("Auton Extra", extraCredit[extraIndex]);
	}

	public void emptyAuto() {

	}

	public void crossCamShootAuto() {
		// When auton begins, the robot will use Compass Drive to drive over a
		// defense
		switch (autonstate) {
		case 0:
			time.start();
			time.reset();
			navX.zeroYaw();
			drive.resetEncoders();
			autonstate = 1;
			break;

		case 1:
			drive.compassDrive(0.6, navX.getYaw(), false, 0.0);
			// drive for 7ft or 3 seconds
			if (time.get() >= 3 || drive.getDistance() > 160) {
				autonstate = 2;
			}
			break;

		case 2:
			drive.tankDrive(0.0, 0.0);
			time.reset();
			if (visionWorking) {
				autonstate = 3;
			} else {
				autonstate = 7;
			}
			break;

		case 3:// look for camera target
			Robot.vp.run();
			// keep trying until we get a good image
			if (Robot.vp.goodTarget) {
				cameraAngle = Robot.vp.getAngle() + navX.getYaw();
				autonstate = 4;
				time.reset();
			}
			break;

		case 4:// once target is found, turn to face it
			drive.shooterAlign(cameraAngle, navX.getYaw(), false);
			SmartDashboard.putNumber("cameraAngle", cameraAngle);
			SmartDashboard.putBoolean("goodTarget", Robot.vp.goodTarget);
			if (time.get() > 2) {
				autonstate = 5;
				time.reset();
			}
			break;

		case 5:// prime shooter and stop tank
			drive.tankDrive(0, 0);
			bc.shooter.manualShooter(0, true, 0);
			if (time.get() > 1) {
				autonstate = 6;
				time.reset();
			}
			break;

		case 6:// fire
			bc.shooter.manualShooter(0, true, 1);
			if (time.get() > 0.75) {
				autonstate = 7;
				time.reset();
			}
			break;

		case 7:// stop
			bc.shooter.manualShooter(0, false, 0);
			break;
		}
	}

	public void justDriveAuto() {
		// When auton begins, the robot will use Compass Drive to drive over a
		// defense
		switch (autonstate) {
		case 0:
			time.start();
			time.reset();
			navX.zeroYaw();
			drive.resetEncoders();
			autonstate = 1;
			break;

		case 1:
			drive.compassDrive(0.6, navX.getYaw(), false, 0.0);
			bc.gatherer.autoAndback(false);
			bc.gatherer.gatherArm.set(0.2);
			// drive for 7ft or 3 seconds
			if (time.get() >= 3.25 || drive.getDistance() > 170) {
				autonstate = 2;
			}
			break;

		case 2:
			drive.tankDrive(0.0, 0.0);
			bc.gatherer.autoAndback(false);
			bc.gatherer.gatherArm.set(0);
			time.reset();
			break;
		}
	}

	public void lowBarAuto() {
		switch (autonstate) {

		case 0:
			time.start();
			time.reset();
			navX.zeroYaw();
			drive.resetEncoders();
			autonstate = 1;
			break;

		case 1: // bring the gatherer down
			bc.gatherer.autoAndback(true);
			bc.gatherer.gatherArm.set(bc.GATHER_LOWBAR_POS);
			if (time.get() > 1.5) {
				autonstate = 2;
				time.reset();
			}
			break;

		case 2: // bring the shooter down
			bc.shooter.autoAndback(true);
			bc.shooter.shooterArm.set(bc.SHOOTER_LOWBAR_POS);
			if (time.get() > 1.5) {
				autonstate = 3;
				time.reset();
			}
			break;

		case 3:
			bc.shooter.autoAndback(false);
			bc.gatherer.autoAndback(false);
			autonstate = 4;
			time.reset();
			break;

		case 4:
			drive.compassDrive(0.6, navX.getYaw(), false, 0.0);
			// drive for 7ft or 3 seconds
			if (time.get() >= 3.25 || drive.getDistance() > 170) {
				autonstate = 5;
			}
			break;

		case 5:
			drive.tankDrive(0.0, 0.0);
			time.reset();
			break;
		}
	}

	public void straightShootAuto(){			//WIP, Steven C, 3/30
		switch (autonstate) {
		case 0:
			time.start();
			time.reset();
			navX.zeroYaw();
			drive.resetEncoders();
			autonstate = 1;
			break;
		case 1:
			drive.compassDrive(0.6, navX.getYaw(), false, 0.0);
			bc.gatherer.autoAndback(false);
			bc.gatherer.gatherArm.set(0.2);
			if (time.get() >= 3.25 || drive.getDistance() > 170){
				autonstate = 2;
			}
			break;
		case 2:
			drive.tankDrive(0.0, 0.0);
			bc.shooter.fire();
			time.reset();
			break;
		}	
	}
	
	boolean collapse = false;
	boolean doA180 = false;
	boolean doStow = false;
	boolean driving = false; 
	double drivePower = 0;
	double driveDistance = 0;
	double driveTime = 0;
	boolean doElimAuton = false;
	boolean doCheval = false;
	boolean crossDrive = false;
	double crossDistance = 0;
	double crossTime = 0;
	boolean alignDrive = false;
	double alignAngle = 0;
	boolean cameraAlign = false;
	boolean shooting = false;
	
	public void runAuto() {

		// emptyAuto();
		// justDriveAuto();
		//lowBarAuto();
		
		//stationIndex;
		//defenseIndex;
		//extraIndex;
		
		switch(stationIndex){
		case -1: //special elims auton
			collapse = false;
			doA180 = false;
			doStow = true;
			driving = false; 
			drivePower = 0.6;
			driveDistance = 140;
			driveTime = 3.0;
			doElimAuton = true;
			doCheval = false;
			crossDrive = false;
			crossDistance = 0;
			crossTime = 0;
			alignDrive = true;
			alignAngle = 0;
			cameraAlign = true;
			shooting = true;
			break;
			
		case 0: //do nothing so exit before anything happens
			Robot.vp.closeCamera();
			return;
			
		case 1: //the lowbar
			collapse = true;
			doA180 = false;
			doStow = false;
			driving = true;
			drivePower = 0.6;
			driveDistance = 160; // was 120
			driveTime = 3.2;  // was 3
			doElimAuton = false;
			doCheval = false;
			crossDrive = false;
			crossDistance = 0;
			crossTime = 0;
			alignDrive = true;
			alignAngle = 90;  //was 60
			cameraAlign = true;
			shooting = true;
			break;
			
		case 2:
			collapse = false;
			doA180 = false;
			doStow = true;
			driving = true;
			drivePower = 0.6;
			driveDistance = 160;  // was 140
			driveTime = 3.2;  // was 3
			doElimAuton = false;
			doCheval = false;
			crossDrive = false;
			crossDistance = 0;
			crossTime = 0;
			alignDrive = true;
			alignAngle = 45;  // was 25
			cameraAlign = true;
			shooting = true;
			break;
			
		case 3:
			collapse = false;
			doA180 = false;
			doStow = true;
			driving = true; 
			drivePower = 0.6;
			driveDistance = 140;
			driveTime = 3.0;
			doElimAuton = false;
			doCheval = false;
			crossDrive = false;
			crossDistance = 0;
			crossTime = 0;
			alignDrive = true;
			alignAngle = 15;
			cameraAlign = true;
			shooting = true;
			break;
			
		case 4:
			collapse = false;
			doA180 = false;
			doStow = true;
			driving = true; 
			drivePower = 0.6;
			driveDistance = 140;
			driveTime = 3.0;
			doElimAuton = false;
			doCheval = false;
			crossDrive = false;
			crossDistance = 0;
			crossTime = 0;
			alignDrive = true;
			alignAngle = -10;
			cameraAlign = true;
			shooting = true;
			break;
			
		case 5:
			collapse = false;
			doA180 = false;
			doStow = true;
			driving = true; 
			drivePower = 0.6;
			driveDistance = 190;  // was 140
			driveTime = 4.0;  // was 3, then 3.2, then 3.6
			doElimAuton = false;
   			doCheval = false;
			crossDrive = false;
			crossDistance = 0;
			crossTime = 0;
			alignDrive = true;
			alignAngle = -70; //was 30
			cameraAlign = true;
			shooting = true;
			break;
		}
		
		switch(defenseIndex){
		case 0://default drive case
			break;
			
		case 1: //rough terrain
			//driveDistance = 120; //reduced from 140
			//driveTime = 2.5;
			break;
			
		case 2: //moat
			//driveDistance = 140;
			//driveTime = 3.0;
			break;
			
		case 3: //rock wall
			//driveDistance = 140;
			//driveTime = 3.0;
			break;
			
		case 4: //ramparts
			//driveDistance = 130;
			//driveTime = 3.0;
			break;
			
		case 5://cheval
			doStow = false;
			Robot.vp.closeCamera();
			return;   //if you cheval and its not programmed, dont do anyting
		
		case 6://portcullis 
			collapse = true;
			doStow = false;
			drivePower = 0.5;
			//driveDistance = 140;
			//driveTime = 3.0;
			break;
			
		}
		
		switch(extraIndex){
		case 0://none
			break;
			
		case 1://dont shoot
			shooting = false;
			break;
			
		case 2://spy box
			doStow = false;
			driving = false;
			crossDrive = false;
			break;
		}
		
		runFancyAuto();
		
	}
	
	int repeatAlign = 3;
	
	public void runFancyAuto(){
		switch(autonstate){
		case 0:
			time.start();
			time.reset();
			navX.zeroYaw();
			drive.resetEncoders();
			if(collapse){
				autonstate = 11;
			} else {
				autonstate = 20;
			}
			break;

		//collapse section
		case 11: // bring the gatherer down
			bc.gatherer.autoAndback(true);
			bc.gatherer.gatherArm.set(bc.GATHER_LOWBAR_POS);
			if (time.get() > 1.5) {
				autonstate = 12;
				time.reset();
			}
			break;

		case 12: // bring the shooter down
			bc.shooter.autoAndback(true);
			bc.shooter.shooterArm.set(bc.SHOOTER_LOWBAR_POS);
			if (time.get() > 1.5) {
				autonstate = 13;
				time.reset();
			}
			break;

		case 13:
			bc.shooter.autoAndback(false);
			bc.gatherer.autoAndback(false);
			autonstate = 20;
			time.reset();
			break;
			
		//do a barrel roll section				!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		case 20:
			if(doA180){
				autonstate = 21;
			} else {
				autonstate = 25;
			}
			break;
			
		//go to stow position section
		case 25:
			time.reset();
			drive.resetEncoders();
			if(doStow){
				autonstate = 26;
			} else {
				autonstate = 30;
			}
			break;
			
		case 26:
			bc.gatherer.autoAndback(true);
			bc.gatherer.gotoPosition(bc.GATHER_STOW_POS + 15);
			drive.compassDrive(0.25, navX.getYaw(), false, 0.0);
			if(time.get() > 0.5){
				autonstate = 27;
				time.reset();
			}
			break;
			
		case 27:
			bc.shooter.autoAndback(true);
			bc.shooter.gotoPosition(bc.SHOOTER_STOW_POS);
			if(time.get() > 0.4){
				autonstate = 28;
				time.reset();
			}
			break;
			
		case 28:
			bc.gatherer.autoAndback(true);
			bc.gatherer.gotoPosition(bc.GATHER_STOW_POS);
			if(time.get() > 0.3){
				autonstate = 29;
				time.reset();
			}
			break;
			
		case 29:
			bc.shooter.autoAndback(false);
			bc.gatherer.autoAndback(false);
			bc.shooter.manualShooter(0, false, 0);
			bc.gatherer.manualGather(0, false, false);
			drive.tankDrive(0, 0);
			autonstate = 30;
			break;
			
		//drive section
		case 30:
			//drive.resetEncoders();
			time.reset();
			if(driving){
				autonstate = 31;
			} else {
				autonstate = 36;
			}
			break;
			
		case 31:
			drive.compassDrive(drivePower, navX.getYaw(), false, 0.0);
			// drive for some distance or for some time
			if (time.get() >= driveTime || drive.getDistance() > driveDistance) {
				autonstate = 32;
			}
			break;

		case 32:
			drive.tankDrive(0.0, 0.0);
			time.reset();
			autonstate = 36;
			break;
			
		case 35: //do elim auton section
			if(doElimAuton){
				autonstate = 36;
			} else {
				autonstate = 40;
			}
			break;
			
		case 36:
			if(elimAuton()){
				autonstate = 40;
			}
			break;
			
		//cheval section					!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		case 40:
			if(doCheval){
				autonstate = 41;
			} else {
				autonstate = 50;
			}
			break;
			
		//cross drive section
		case 50:
			drive.resetEncoders();
			time.reset();
			if(crossDrive){
				autonstate = 51;
			} else {
				autonstate = 60;
			}
			break;
			
		case 51:
			drive.compassDrive(0.6, navX.getYaw(), false, 0.0);
			// drive for some distance or for some time
			if (time.get() >= crossTime || drive.getDistance() > crossDistance) {
				autonstate = 52;
			}
			break;

		case 52:
			drive.tankDrive(0.0, 0.0);
			time.reset();
			autonstate = 60;
			break;
			
		//align drive section	
		case 60:
			time.reset();
			if(alignDrive){
				autonstate = 61;
			} else {
				autonstate = 65;
			}
			break;
			
		case 61:
			drive.shooterAlign(alignAngle, navX.getYaw(), true);
			double angleDiff = alignAngle - navX.getYaw();
			if((Math.abs(angleDiff) < 10 && time.get() > 1.5) || time.get() > 4.0){
				autonstate = 62;
			}
			break;
			
		case 62:
			drive.tankDrive(0, 0);
			autonstate = 65;
			break;
			
		//put gatherer and shooter in shooting position
		case 65:
			time.reset();
			if(shooting && collapse){
				autonstate = 100;
			} else if ( shooting && doStow){
				autonstate = 66;
			} else {
				autonstate = 70;
			}
			break;
			
		case 100:
			bc.shooter.autoAndback(true);
			bc.gatherer.autoAndback(true);
			bc.regrippingState = 0;
			autonstate = 101;
			break;
			
		case 101:
			bc.regrip();
			if(bc.regrippingState == 8){
				autonstate = 66;
			}
			break;
			
		case 66: //lift shooter first
			bc.shooter.autoAndback(true);
			bc.shooter.gotoPosition(bc.SHOOTER_FARSHOT_POS);
			if(time.get() > 0.6){
				autonstate = 67;
				time.reset();
			}
			break;
			
		case 67://then gatherer
			bc.gatherer.autoAndback(true);
			bc.gatherer.gotoPosition(bc.GATHER_FARSHOT_POS);
			if(time.get() > 1){
				autonstate = 68;
				time.reset();
			}
			break;
			
		case 68://then go back to manual mode
			bc.shooter.autoAndback(false);
			bc.gatherer.autoAndback(false);
			autonstate = 70;
			break;	
		
			
		//camera align section
		case 70:
			time.reset();
			if(cameraAlign){
				autonstate = 71;
				repeatAlign = 3;
			} else {
				autonstate = 80;
			}
			break;
			
		case 71:
			bc.shooter.autoAndback(true);
			bc.gatherer.autoAndback(true);
			bc.shooter.gotoPosition(bc.SHOOTER_FARSHOT_POS);
			bc.gatherer.gotoPosition(bc.GATHER_FARSHOT_POS);
			if(time.get() > 0.75){
				autonstate = 72;
				time.reset();
			}
			break;
			
		case 72:// look for camera target
			Robot.vp.run();
			bc.shooter.autoAndback(true);
			bc.gatherer.autoAndback(true);
			bc.shooter.gotoPosition(bc.SHOOTER_FARSHOT_POS);
			bc.gatherer.gotoPosition(bc.GATHER_FARSHOT_POS);
			// keep trying until we get a good image
			if (Robot.vp.goodTarget) {
				cameraAngle = Robot.vp.getAngle() + navX.getYaw();
				autonstate = 73;
				time.reset();
			}
			break;

		case 73:// once target is found, turn to face it
			drive.shooterAlign(cameraAngle, navX.getYaw(), false);
			SmartDashboard.putNumber("cameraAngle", cameraAngle);
			SmartDashboard.putBoolean("goodTarget", Robot.vp.goodTarget);
			double dist = Robot.vp.getDistance();
			if(dist == 0){
				bc.visionAngleOffset = 0;
			} else {
				bc.visionAngleOffset = IO.lookup(IO.SHOOTER_ANGLE, IO.DISTANCE_AXIS, dist);
			}
			bc.shooter.gotoPosition(bc.SHOOTER_FARSHOT_POS);
			bc.gatherer.gotoPosition(bc.GATHER_FARSHOT_POS);
			if (time.get() > 0.66) {
				autonstate = 74;
				time.reset();
			}
			break;
			
		//do it again for good measure
		case 74:
			drive.tankDrive(0, 0);
			repeatAlign--;
			if(repeatAlign <= 0){
				autonstate = 80;
			} else {
				autonstate = 72;
			}
			break;
			
		//shoot things section
		case 80:
			if(shooting){
				autonstate = 81;
			} else {
				autonstate = 90;
			}
			break;
			
		case 81:// prime shooter and stop tank
			bc.shooter.autoAndback(true);
			bc.shooter.gotoPosition(bc.SHOOTER_FARSHOT_POS);
			bc.prime();
			if (time.get() > 1) {
				autonstate = 82;
				time.reset();
			}
			break;

		case 82:// fire
			bc.shooter.autoAndback(true);
			bc.shooter.gotoPosition(bc.SHOOTER_FARSHOT_POS);
			bc.shooter.fire();
			bc.shooter.prime(0.6, false);
			if (time.get() > 0.75) {
				autonstate = 83;
				time.reset();
			}
			break;

		case 83:// stop
			bc.shooter.autoAndback(false);
			bc.gatherer.autoAndback(false);
			bc.shooter.manualShooter(0, false, 0);
			bc.gatherer.manualGather(0, false, false);
			autonstate = 90;
			break;
			
		//stop things section
		case 90:
			drive.tankDrive(0, 0);
			bc.shooter.autoAndback(false);
			bc.gatherer.autoAndback(false);
			Robot.vp.closeCamera();
			break;
		}
	}
	
	int elimAutonState = 0;
	
	public boolean elimAuton(){
		boolean done = false;
		switch(elimAutonState){
		//drive section
		case 0:
			//drive.resetEncoders();
			time.reset();
			elimAutonState = 1;
			break;
			
		case 1:
			drive.compassDrive(0.6, navX.getYaw(), false, 0.0);
			// drive for some distance or for some time
			if (time.get() >= 2.7 || drive.getDistance() > 100) {
				elimAutonState = 2;
				time.reset();
				drive.resetEncoders();
			}
			break;
			
		case 2:
			drive.compassDrive(0.6, navX.getYaw(), false, -20);
			// drive for some distance or for some time
			if (time.get() >= 2.0 || drive.getDistance() > 90) {
				elimAutonState = 3;
				time.reset();
				drive.resetEncoders();
			}
			break;

		case 3:
			drive.tankDrive(0.0, 0.0);
			time.reset();
			elimAutonState = 4;
			break;
			
		case 4:
			done = true;
			break;
		}
		
		return done;
	}

}
