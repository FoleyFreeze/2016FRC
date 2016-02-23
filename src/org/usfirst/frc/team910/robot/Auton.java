package org.usfirst.frc.team910.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Timer;

public class Auton {
	AHRS navX;
	DriveTrain drive;

	public Auton(AHRS navX, DriveTrain drive) {
		this.navX = navX;
		this.drive = drive;
	} 

	Timer time = new Timer();
	int autonstate = 0;

	public void defaultAuto() {
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
			if (time.get() >= 3 || drive.getDistance() > 84) {
				autonstate = 2;
			}
			break;

		case 2:
			drive.tankDrive(0.0, 0.0);
			time.reset();
		}
	}
}
