package org.usfirst.frc.team910.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
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
	
	String[] stationSelection = {"0 Do Nothing" , "1 Low Bar", "2nd Def", "3rd Def", "4th Def", "5th Def" };
	int stationIndex = 0;
	String[] defenseType = { "0 Drive Over", "1 Rough Terrain", "2 Moat", "3 Rock Wall", "4 Ramparts", "5 Cheval", "6 Portcullis" };
	int defenseIndex = 0;
	String[] extraCredit = { "0 No Modification", "1 No Shooting", "2 Spy Box", "3 Shoot anyway" };
	int extraIndex = 0;
	
	double maxRoll=0;		//Zero these values and record the highest value we get and output to the driver station
	double maxPitch=0;
	double maxPitchAndRoll=0;
	
	double zeroedPitch = 0;	// We'll grab their "resting (zeroed)" values in Auton's SelectAuto
	double zeroedRoll  = 0;
	
	public Auton(AHRS navX, DriveTrain drive, BoulderController bc, boolean visionWorking) {
		this.navX = navX;
		this.drive = drive;
		this.bc = bc;
		this.visionWorking = visionWorking;
	}

	Timer time = new Timer();
	int autonstate = 0;
	double cameraAngle = 0;
	
	Timer safetytimer = new Timer();
	
	boolean prevStationInc = false;
	boolean prevStationDec = false;
	boolean prevDefenseInc = false;
	boolean prevDefenseDec = false;
	boolean prevExtraInc = false;
	boolean prevExtraDec = false;
	
	public void selectAuto(Joystick joy) {
		autonstate = 0;								//Reset to State zero in here
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
		
		if(stationIndex < 0) stationIndex = 0;
		if(stationIndex >= stationSelection.length) stationIndex = stationSelection.length - 1;
		if(defenseIndex < 0) defenseIndex = 0;
		if(defenseIndex >= defenseType.length) defenseIndex = defenseType.length - 1;
		if(extraIndex < 0) extraIndex = 0;
		if(extraIndex >= extraCredit.length) extraIndex = extraCredit.length - 1;
		
		SmartDashboard.putString("Auton Station", stationSelection[stationIndex]);
		SmartDashboard.putString("Auton Defense", defenseType[defenseIndex]);
		SmartDashboard.putString("Auton Extra", extraCredit[extraIndex]);
		
		zeroedPitch = navX.getPitch();	//Grab their "resting (zeroed)" values when auton first starts
		zeroedRoll  = navX.getRoll();
		
		SmartDashboard.putNumber("zeroedPitch", zeroedPitch);
		SmartDashboard.putNumber("zeroedRoll", zeroedRoll);
		
	}

	public void emptyAuto() {

	}

	/*
	public void crossCamShootAuto() {
		// When auton begins, the robot will use Compass Drive to drive over a
		// defense
		switch (autonstate) {
		case 0:														//Reset Time, Gyro, Encoders
			time.start();
			time.reset();
			navX.zeroYaw();
			drive.resetEncoders();
			autonstate = 1;
			break;

		case 1:														//Compass Drive for 3 seconds or 160 inches!
			drive.compassDrive(0.6, navX.getYaw(), false, 0.0);
			// drive for 7ft or 3 seconds
			if (time.get() >= 3 || drive.getDistance() > 160) {
				autonstate = 2;
			}
			break;

		case 2:														//STOP, reset time, if vision works goto 3 else goto 7
			drive.tankDrive(0.0, 0.0);
			time.reset();
			if (visionWorking) {
				autonstate = 3;
			} else {
				autonstate = 7;
			}
			break;

		case 3:// 													//Look for target continuously. When found, reset time & goto 4
			Robot.vp.run();
			// keep trying until we get a good image
			if (Robot.vp.goodTarget) {
				cameraAngle = Robot.vp.getAngle() + navX.getYaw();
				autonstate = 4;
				time.reset();
			}
			break;

		case 4:														// Once target is found, turn to face it
			drive.shooterAlign(cameraAngle, navX.getYaw(), false);
			SmartDashboard.putNumber("cameraAngle", cameraAngle);
			SmartDashboard.putBoolean("goodTarget", Robot.vp.goodTarget);
			if (time.get() > 2) {
				autonstate = 5;
				time.reset();
			}
			break;

		case 5:														// Prime shooter and stop tank
			drive.tankDrive(0, 0);
			bc.shooter.manualShooter(0, true, 0);
			if (time.get() > 1) {
				autonstate = 6;
				time.reset();
			}
			break;

		case 6:														// Fire
			bc.shooter.manualShooter(0, true, 1);
			if (time.get() > 0.75) {
				autonstate = 7;
				time.reset();
			}
			break;

		case 7:														// Stop shooter
			bc.shooter.manualShooter(0, false, 0);
			break;
		}
	}

*/
	
	
/*	
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
			bc.gatherer.goToPositionControl(false);
			bc.gatherer.gatherArm.set(0.2);
			// drive for 7ft or 3 seconds
			if (time.get() >= 3.25 || drive.getDistance() > 170) {
				autonstate = 2;
			}
			break;

		case 2:
			drive.tankDrive(0.0, 0.0);
			bc.gatherer.goToPositionControl(false);
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
			bc.gatherer.goToPositionControl(true);
			bc.gatherer.gatherArm.set(bc.GATHER_LOWBAR_POS);
			if (time.get() > 1.5) {
				autonstate = 2;
				time.reset();
			}
			break;

		case 2: // bring the shooter down
			bc.shooter.goToPositionControl(true);
			bc.shooter.shooterArm.set(bc.SHOOTER_LOWBAR_POS);
			if (time.get() > 1.5) {
				autonstate = 3;
				time.reset();
			}
			break;

		case 3:
			bc.shooter.goToPositionControl(false);
			bc.gatherer.goToPositionControl(false);
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
			bc.gatherer.goToPositionControl(false);
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
	
*/	
	boolean collapse = false;
	boolean doA180 = false;
	boolean doStow = false;
	boolean driving = false;
	double defDrivePower = 0;
	boolean waitForPitch = false;
	double defDriveDistance = 0;
	double defDriveTime = 0;
	boolean turnDrive = false;
	double turnDrivePower = 0;
	double turnDriveAngle = 0;
	double turnDriveDistance = 0;
	double turnDriveTime = 0;
	boolean doCheval = false;
	boolean crossDrive = false;
	double crossDistance = 0;
	double crossTime = 0;
	boolean alignDrive = false;
	double alignAngle = 0;
	boolean cameraAlign = false;
	boolean shooting = false;
	boolean shootanyway = false;
	//boolean driveBack = false; //WIP added for Case 84 4/16 Steven C
	
	public void runAuto() {

		// emptyAuto();
		// justDriveAuto();
		//lowBarAuto();
		
		//stationIndex;
		//defenseIndex;
		//extraIndex;
		
		switch(stationIndex){	//Left Joystick btn 11, 12
		case 0: //do nothing so exit before anything happens
			//Robot.vp.closeCamera();
			return;
			
		case 1: //the lowbar
			collapse = true;
			doA180 = false;
			doStow = false; 
			driving = true;
			defDrivePower = 0.6;
			waitForPitch = false;
			defDriveDistance = 160; 
			defDriveTime = 3.2;  
			turnDrive = false;	//rather than drive at an angle, drive really far forward and shoot in the side goal
			turnDrivePower = 0;
			turnDriveAngle = 0;
			turnDriveDistance = 0;
			turnDriveTime = 0;
			doCheval = false;
			crossDrive = false;
			crossDistance = 0;
			crossTime = 0;
			alignDrive = true;
			alignAngle = 90;  //was 60
			cameraAlign = true;
			shooting = true;
			shootanyway = false;
			break;
			
		case 2:						//Defense 2
			collapse = false;
			doA180 = false;
			doStow = true;
			driving = true;
			defDrivePower = 0.6;
			waitForPitch = true;
			defDriveDistance = 100;  // was 160
			defDriveTime = 1.75;  // was 3.2
			turnDrive = true; 
			turnDrivePower = 0.6;
			turnDriveAngle = 50;
			turnDriveDistance = 113;		//was 80;
			turnDriveTime = 2;	//was 2.5;
			doCheval = false;
			crossDrive = false;
			crossDistance = 0;
			crossTime = 0;
			alignDrive = true;
			alignAngle = 0;  // was 25
			cameraAlign = true;
			shooting = true;
			shootanyway = false;
			break;
			
		case 3:						//Defense 3
			collapse = false;
			doA180 = false;
			doStow = true;
			driving = true; 
			defDrivePower = 0.6;
			waitForPitch = true;
			defDriveDistance = 100; //was 140
			defDriveTime = 1.75; //was 3.0
			turnDrive = true;
			turnDrivePower = 0.6;
			turnDriveAngle = 25;
			turnDriveDistance = 60; //80
			turnDriveTime = 1.5;		//was 2.5
			doCheval = false;
			crossDrive = false;
			crossDistance = 0;
			crossTime = 0;
			alignDrive = true;
			alignAngle = 0; //was 15
			cameraAlign = true;
			shooting = true;
			shootanyway = false;
			break;
			
		case 4:						//Defense 4
			collapse = false;
			doA180 = false;
			doStow = true;
			driving = true; 
			defDrivePower = 0.6;
			waitForPitch = true;
			defDriveDistance = 90; //was 100 was 140
			defDriveTime = 1.75; //was 3.0
			turnDrive = true;
			turnDrivePower = 0.55;
			turnDriveAngle = -12;
			turnDriveDistance = 55;
			turnDriveTime = 1.5;
			doCheval = false;
			crossDrive = false;
			crossDistance = 0;
			crossTime = 0;
			alignDrive = true;
			alignAngle = 0; //-10
			cameraAlign = true;
			shooting = true;
			shootanyway = false;
			break;
			
		case 5:						//Defense 5
			collapse = false;
			doA180 = false;
			doStow = true;
			driving = true; 
			defDrivePower = 0.6;
			waitForPitch = true;
			defDriveDistance = 90;// SHOULD BE 190;  // was 140
			defDriveTime = 1.5; // was 4.0;  // was 3, then 3.2, then 3.6
			turnDrive = true;// was false;	//rather than drive at an angle, drive really far forward and shoot in the side goal
			turnDrivePower = .6;
			turnDriveAngle = 0;
			turnDriveDistance = 93;//was 90//was 85 elim1 //was 60, but too short //was 85 44
			turnDriveTime = 1.4;
			doCheval = false;
			crossDrive = false;
			crossDistance = 0;
			crossTime = 0;
			alignDrive = true;
			alignAngle = -55;//was 57 //65 //-75 //was 30
			cameraAlign = true;
			shooting = true;
			shootanyway = false;
			break;
		}
		
		switch(defenseIndex){
		case 0://default drive case
			break;
			
		case 1: //rough terrain
			//driveDistance = 120; //reduced from 140
			//driveTime = 2.5;
			waitForPitch = true;
			break;
			
		case 2: //moat
			//driveDistance = 140;
			//driveTime = 3.0;
			waitForPitch = true;
			break;
			
		case 3: //rock wall
			//driveDistance = 140;
			//driveTime = 3.0;
			waitForPitch = true;
			break;
			
		case 4: //ramparts
			//driveDistance = 130;
			//driveTime = 3.0;
			waitForPitch = true;
			break;
			
		case 5://cheval
			doStow = false;
			waitForPitch = false;
			//Robot.vp.closeCamera();
			return;   //if you cheval and its not programmed, dont do anyting
		
		case 6://portcullis 
			collapse = true;
			doStow = false;
			defDrivePower = 0.5;
			waitForPitch = false;
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
			
		case 3:
			shootanyway = true;
			
		case 4: //drive back to neutral zone
			//driveBack = true;
		}
		
		runFancyAuto();
		
	}
	
	int repeatAlign = 3;
	boolean visionNotFound = false;
	
	double time_waiting_for_level = 0;
	
	double[] pitchArray = {0,0,0,0,0};
	
	public void runFancyAuto(){
		
		double currentPitch = Math.abs(navX.getPitch() - zeroedPitch);	//Get current Pitch and Roll and normalize to zero
		double currentRoll  = Math.abs(navX.getRoll()  - zeroedRoll);
		
		if (currentPitch > maxPitch) {										//Save Max Pitch we achieve
			maxPitch = currentPitch;
			SmartDashboard.putNumber("Max Pitch", maxPitch);
		}
		if (currentRoll > maxRoll) {										//Save Max Roll we achieve
			maxRoll = currentRoll;
			SmartDashboard.putNumber("Max Roll", maxRoll);
		}
		if (currentPitch + currentRoll > maxPitchAndRoll) {					//Save Max Pitch AND Roll we achieve
			maxPitchAndRoll = currentPitch + currentRoll;
			SmartDashboard.putNumber("maxPitchAndRoll", maxPitchAndRoll);
		}
		
		double sum = 0;
		for(int i=pitchArray.length-1; i>0; i--){
			pitchArray[i] = pitchArray[i-1];
			sum += pitchArray[i];
		}
		pitchArray[0] = currentPitch + currentRoll;
		sum += pitchArray[0];
		double averagePitch = sum / pitchArray.length;
		
		SmartDashboard.putNumber("averagePitch", averagePitch);
		
		SmartDashboard.putNumber("AutoCase", autonstate);

		switch(autonstate){
		case 0:						//Reset Time, Yaw, Encoders 
			time.start();
			time.reset();
			navX.zeroYaw();
			drive.resetEncoders();
			if(collapse){			//Going all the way down?
				autonstate = 11;
			} else {
				autonstate = 20;
			}
			break;

		//collapse section
		case 11: // bring the gatherer down
			bc.gatherer.goToPositionControl(true);
			bc.gatherer.gatherArm.set(bc.GATHER_LOWBAR_POS);
			if (time.get() > 2) {
				autonstate = 12;
				time.reset();
			}
			break;

		case 12: // bring the shooter down
			bc.shooter.goToPositionControl(true);
			bc.shooter.shooterArm.set(bc.SHOOTER_LOWBAR_POS);
			if (time.get() > 1.5) {
				autonstate = 13;
				time.reset();
			}
			break;

		case 13:
			bc.shooter.goToPositionControl(false);
			bc.gatherer.goToPositionControl(false);
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
			bc.gatherer.goToPositionControl(true);
			bc.gatherer.gotoPosition(bc.GATHER_STOW_POS + 15);
			drive.compassDrive(0.25, navX.getYaw(), false, 0.0);
			if(time.get() > 0.5){
				autonstate = 27;
				time.reset();
			}
			break;
			
		case 27:
			bc.shooter.goToPositionControl(true);
			bc.shooter.gotoPosition(bc.SHOOTER_STOW_POS);
			if(time.get() > 0.4){
				autonstate = 28;
				time.reset();
			}
			break;
			
		case 28:
			bc.gatherer.goToPositionControl(true);
			bc.gatherer.gotoPosition(bc.GATHER_STOW_POS);
			if(time.get() > 0.3){
				autonstate = 29;
				time.reset();
			}
			break;
			
		case 29:
			bc.shooter.goToPositionControl(false);
			bc.gatherer.goToPositionControl(false);
			bc.shooter.manualShooter(0, false, 0);
			bc.gatherer.manualGather(0, false, false);
			drive.tankDrive(0, 0);
			autonstate = 30;
			break;
			
		//drive section
		case 30:
			//drive.resetEncoders();
			time.reset();
			safetytimer.reset();										//Let's track how long we're trying to do the next case!
			safetytimer.start();
			if(driving){
				if(waitForPitch){
					autonstate = 32;
				} else {
					autonstate = 31;
				}
			} else {
				autonstate = 35;
			}
			break;
			
		case 31: //drive (without defense detection)	
			bc.gatherer.goToPositionControl(false);
			bc.gatherer.manualGather(0.15, false, false);
			drive.compassDrive(defDrivePower, navX.getYaw(), false, 0.0);
			// drive for some distance or for some time
			if (time.get() >= defDriveTime || drive.getDistance() > defDriveDistance) {
				autonstate = 34;
			}
			break;
			
		case 32: //wait for Pitch and Roll to go NON zero
			bc.gatherer.goToPositionControl(false);
			bc.gatherer.manualGather(0.15, false, false);

			drive.compassDrive(defDrivePower, navX.getYaw(), false, 0.0);		//Drive STRAIGHT over defense
			if(averagePitch > IO.MAX_FLAT_PITCH){	//Did we hit enough angle that we're doing the defense?
				if(time.get() > 0.1) {											//Were we at that angle LONG enough? Then Go To Next State
					autonstate = 33;							
					time.reset();
					safetytimer.reset();										//Let's track how long we're trying to do the next case!
				}
			} else {
				time.reset();													//Time not up yet?  Reset timer and try again
			}
			if(safetytimer.get() > 6){
				autonstate = 34;
				safetytimer.reset();
			}
			break;
			
		case 33: //pitch goes back to zero
			bc.gatherer.goToPositionControl(false);
			bc.gatherer.manualGather(0.15, false, false);
			drive.compassDrive(defDrivePower, navX.getYaw(), false, 0.0);
			
			if(averagePitch <  10){
				if(time.get() > 0.4) autonstate = 34;		//was 0.3
				time.reset();
			} else {
				time.reset();
			}
			
			if (safetytimer.get() > 1.5) {
				SmartDashboard.putNumber("Yikes! safetytimer went off at 33", safetytimer.get());
				safetytimer.reset();
				autonstate = 34;								//Safety timer went off - abort and goto next step!
			}
			
			break;
			
		case 34: //stop block
			bc.gatherer.goToPositionControl(false);
			bc.gatherer.manualGather(0, false, false);
			drive.tankDrive(0.0, 0.0);
			time.reset();
			autonstate = 35;
			break;
			
		//turn drive section
		case 35:
			drive.resetEncoders();
			time.reset();
			if(turnDrive){
				autonstate = 36;
			} else {
				autonstate = 40;
			}
			break;
			
		case 36: 
			drive.compassDrive(turnDrivePower, navX.getYaw(), false, turnDriveAngle);
			// drive for some distance or for some time
			if (drive.getDistance() > turnDriveDistance) {
				autonstate = 37;
			}
			if (time.get() >= turnDriveTime) {
				SmartDashboard.putNumber("Yikes! safetytimer went off at 36", time.get());
			}
			
			break;

		case 37:
			drive.tankDrive(0.0, 0.0);
			time.reset();
			autonstate = 40;
			break;
			
		//cheval section
		case 40:
			if(doCheval){
				autonstate = 41;
			} else {
				autonstate = 50;
			}
			break;
		
		case 41:
			bc.shooter.goToPositionControl(true);
			bc.gatherer.goToPositionControl(true);
			bc.flippyFloppies();
			
			if(bc.chevalState >= 4){
				autonstate = 42;
			}
			break;
			
		case 42:
			bc.shooter.goToPositionControl(false);
			bc.gatherer.goToPositionControl(false);
			drive.tankDrive(0.0, 0.0);
			time.reset();
			autonstate = 50;
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
			bc.shooter.goToPositionControl(true);
			bc.gatherer.goToPositionControl(true);
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
			bc.shooter.goToPositionControl(true);
			bc.shooter.gotoPosition(bc.SHOOTER_FARSHOT_POS);
			if(time.get() > 0.6){
				autonstate = 67;
				time.reset();
			}
			break;
			
		case 67://then gatherer
			bc.gatherer.goToPositionControl(true);
			bc.gatherer.gotoPosition(bc.GATHER_FARSHOT_POS);
			if(time.get() > 1){
				autonstate = 68;
				time.reset();
			}
			break;
			
		case 68://then go back to manual mode
			bc.shooter.goToPositionControl(false);
			bc.gatherer.goToPositionControl(false);
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
			bc.shooter.goToPositionControl(true);
			bc.gatherer.goToPositionControl(true);
			bc.farShot();
			//bc.shooter.gotoPosition(bc.SHOOTER_FARSHOT_POS);
			//bc.gatherer.gotoPosition(bc.GATHER_FARSHOT_POS);
			if(time.get() > 0.15){
				autonstate = 72;
				time.reset();
				//Robot.vp.setupCamera();
			}
			break;
			
		case 72:// look for camera target
			Robot.vp.setupCamera();
			Robot.vp.run();
			bc.shooter.goToPositionControl(true);
			bc.gatherer.goToPositionControl(true);
			bc.farShot();
			//bc.shooter.gotoPosition(bc.SHOOTER_FARSHOT_POS);
			//bc.gatherer.gotoPosition(bc.GATHER_FARSHOT_POS)
			// keep trying until we get a good image
			if (Robot.vp.goodTarget) {
				cameraAngle = Robot.vp.getAngle() + navX.getYaw();
				double dist = Robot.vp.getDistance();
				if(dist == 0){
					bc.visionAngleOffset = 0;
				} else {
					bc.visionAngleOffset = IO.lookup(IO.SHOOTER_ANGLE, IO.DISTANCE_AXIS,Robot.vp.getDistance());
				}
				autonstate = 73;
				time.reset();
				visionNotFound = false;
			}
			if(time.get() > 0.7){
				autonstate = 74;
				visionNotFound = true;
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
			bc.farShot();
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
			if((shooting && !visionNotFound) || shootanyway){
				autonstate = 81;
			} else {
				autonstate = 90;
			}
			break;
			
		case 81:// prime shooter and stop tank
			bc.shooter.goToPositionControl(true);
			bc.gatherer.goToPositionControl(true);
			bc.farShot();
			bc.prime();
			if (time.get() > 1) {
				autonstate = 82;
				time.reset();
			}
			break;

		case 82:// fire
			bc.shooter.goToPositionControl(true);
			bc.gatherer.goToPositionControl(true);
			bc.farShot();
			bc.shooter.fire();
			bc.shooter.prime(0.6, false);
			if (time.get() > 0.75) {
				autonstate = 83;
				time.reset();
			}
			break;

		case 83:// stop
			bc.shooter.goToPositionControl(false);
			bc.gatherer.goToPositionControl(false);
			bc.shooter.manualShooter(0, false, 0);
			bc.gatherer.manualGather(0, false, false);
			autonstate = 90;
			break;
			
		/*case 84://WIP drive back to Neutral Zone 4/16 Steven C 
			time.reset();
			drive.compassDrive(0.6, navX.getYaw(), false, 180);
			
			if(time.get() >= defDriveTime){
				autonstate = 90;
				time.reset();
			}*/
		//stop things section
		case 90:
			drive.tankDrive(0, 0);
			bc.shooter.goToPositionControl(false);
			bc.gatherer.goToPositionControl(false);
			Robot.vp.closeCamera();
			Robot.vp.visionCrashed = false; //start teleop with a hopefully working camera
			break;
		}
		
		SmartDashboard.putNumber("navX Pitch", navX.getRoll());
	}

}
