package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;

public class Shooter {
	CANTalon shooterWheel;
	CANTalon shooterArm;
	CANTalon loadWheels;

	double CLOSESHOT = 1000;

	double FARSHOT = 2000;

	double LAUNCH = 3000;

	double LOAD = 0;

	double REVERSE = 50;

	double FAST = 9001;

	double MARGIN = 50;

	double FIRE = 60;

	double gatherPosition;

	double SAFETYDISTANCE = 500;

	public Shooter() {
		shooterWheel = new CANTalon(IO.SHOOTER_WHEEL);
		shooterArm = new CANTalon(IO.SHOOTER_ARM);

	}

	public void autoAndback(boolean manualControl) {

		if (manualControl) {

			shooterArm.changeControlMode(TalonControlMode.PercentVbus);
			shooterWheel.changeControlMode(TalonControlMode.Speed);
		} else {

			shooterArm.changeControlMode(TalonControlMode.Position);
			shooterWheel.changeControlMode(TalonControlMode.Speed);

		}

	}

	public void gotoPosition(double position) {

		// if going down//
		if (shooterArm.getPosition() > position) {
			if (position > gatherPosition) {
				shooterArm.set(position);
			} else {
				shooterArm.set(gatherPosition + SAFETYDISTANCE);
			}
		} else {
			shooterArm.set(position);
		}
	}

	public double getPosition() {
		return shooterArm.getPosition();
	}

	public void aquireGatherPosition(double position) {
		gatherPosition = position;
	}

	int primeState;

	public void prime() {
		switch (primeState) {
		case 1:
			loadWheels.set(loadWheels.getPosition() - REVERSE);
			primeState = 2;
			break;

		case 2:
			if (loadWheels.getPosition() < loadWheels.getSetpoint()) {
				primeState = 3;
			}
			break;

		case 3:

			shooterWheel.set(FAST);
			break;
		}
	}

	public void fire() {
		if (shooterWheelL.getSpeed() > FAST - MARGIN) {
			loadWheelL.set(loadWheelL.getPosition() + FIRE);
			loadWheelR.set(loadWheelR.getPosition() + FIRE);
		}

	}

	public void manualShooter(double YAxisGamepadRight, boolean GamepadLBumper, double LoadWheelAxis) {
		if (YAxisGamepadRight < 0) {
			YAxisGamepadRight /= 1;
		}
		shooterArm.set(YAxisGamepadRight);
		loadWheelL.set(-LoadWheelAxis);
		}

	}

	public void setLoadWheels(double speed) {

	}

	public void manualShooter(double YAxisGamepadRight, boolean GamepadLBumper) {

		shooterArm.set(YAxisGamepadRight);

		if (GamepadLBumper) {

			shooterWheel.set(FAST);

		} else {
			shooterWheel.set(0);
		}
	}


}
