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
	
	String[] stationSelection = { "0 Do Nothing" , "1 Low Bar", "2nd Def", "3rd Def", "4th Def", "5th Def" };
	int stationIndex = 0;
	String[] defenseType = { "0 Drive Over", "1 Portcullis", "2 Cheval" };
	int defenseIndex = 0;
	String[] extraCredit = { "0 None", "1 No Shooting", "2 Spy Box" };
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
		
		if(stationIndex < 0) stationIndex = 0;
		if(stationIndex >= stationSelection.length) stationIndex = stationSelection.length - 1;
		if(defenseIndex < 0) defenseIndex = 0;
		if(defenseIndex >= defenseType.length) defenseIndex = defenseType.length - 1;
		if(extraIndex < 0) extraIndex = 0;
		if(extraIndex >= extraCredit.length) extraIndex = extraCredit.length - 1;
		
		SmartDashboard.putString("Auton Station", stationSelection[stationIndex]);
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

	

	public void runAuto() {
		// autoSelected = (String) chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		// System.out.println("Auto selected: " + autoSelected);

		/*
		 * switch (autoSelected) { case defaultAuto: //emptyAuto();
		 * justDriveAuto(); //comment out if no drive break;
		 * 
		 * case crossCamShootAuto: crossCamShootAuto(); break;
		 * 
		 * case justDriveAuto: justDriveAuto();//comment out if no drive break;
		 * 
		 * default: //emptyAuto(); justDriveAuto();//comment out if no drive
		 * break; }
		 */

		// emptyAuto();
		// justDriveAuto();
		lowBarAuto();
	}

}
