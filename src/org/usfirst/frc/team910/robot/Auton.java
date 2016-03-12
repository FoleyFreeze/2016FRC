package org.usfirst.frc.team910.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Auton {
	AHRS navX;
	DriveTrain drive;
	VisionProcessor vp;
	BoulderController bc;
	
	
	final String defaultAuto = "Do Nothing";
	final String crossCamShootAuto = "Cross, Camera, Shoot";
	String autoSelected;
	SendableChooser chooser;

	public Auton(AHRS navX, DriveTrain drive, VisionProcessor vp, BoulderController bc) {
		this.navX = navX;
		this.drive = drive;
		this.vp = vp;
		this.bc = bc;
	}

	Timer time = new Timer();
	int autonstate = 0;
	double cameraAngle = 0;
	
	public void emptyAuto(){
		
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
			//drive for 7ft or 3 seconds
			if (time.get() >= 3 || drive.getDistance() > 84) {
				autonstate = 2;
			}
			break;

		case 2:
			drive.tankDrive(0.0, 0.0);
			time.reset();
			autonstate = 3;
			break;
			
		case 3://look for camera target
			vp.run();
			//keep trying until we get a good image
			if(vp.goodTarget){
				cameraAngle = vp.getAngle() + navX.getYaw();
				autonstate = 4;
				time.reset();
			}
			break;
			
		case 4://once target is found, turn to face it
			drive.shooterAlign(cameraAngle, navX.getYaw());
			SmartDashboard.putNumber("cameraAngle", cameraAngle);
			SmartDashboard.putBoolean("goodTarget", vp.goodTarget);
			if(time.get() > 2){
				autonstate = 5;
				time.reset();
			}
			break;
			
		case 5://prime shooter and stop tank
			drive.tankDrive(0, 0);
			bc.shooter.manualShooter(0, true, 0);
			if(time.get() > 1){
				autonstate = 6;
				time.reset();
			}
			break;
			
		case 6://fire
			bc.shooter.manualShooter(0, true, 1);
			if(time.get() > 0.75){
				autonstate = 7;
				time.reset();
			}
			break;
			
		case 7://stop
			bc.shooter.manualShooter(0, false, 0);
			break;
		}
	}
	
	public void runAuto(){
		autoSelected = (String) chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);

		switch (autoSelected) {
		case defaultAuto:
			emptyAuto();
			break;

		case crossCamShootAuto:
			crossCamShootAuto();
			break;
		}
	}
	
	
}
