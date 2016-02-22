package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;

public class Shooter {
	CANTalon shooterWheel;
	CANTalon shooterArm;
	CANTalon loadWheels;

	double jogoffset;

	double JOGNUMBER = 15;

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
		if (shooterArm.getPosition() > position + jogoffset) {
			if (position + jogoffset > gatherPosition) {
				shooterArm.set(position + jogoffset);
			} else {
				shooterArm.set(gatherPosition + SAFETYDISTANCE);
			}
		} else {
			shooterArm.set(position + jogoffset);
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
		if (shooterWheel.getSpeed() > FAST - MARGIN) {
			loadWheels.set(loadWheels.getPosition() + FIRE);

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

	public void drawBridge() {

		// bring shooter down high so tail extends high up
		// drive forward until tail is over drawbridge
		// bring shooter down so tail goes down over drawbridge and hooks
		// robot reverses bringing down drawbridge as shooter goes down so hook
		// pulls down drawbridge
		// pin drawbridge to the ground
		// drive forward over drawbridge

		// America is the greatest.

	}

	boolean prevJogUp = false;
	boolean prevJogDown = false;

	public void jog(boolean jogUp, boolean jogDown) {
		if (jogUp && !prevJogUp) {

			jogoffset += JOGNUMBER;
		} else if (jogDown && !prevJogDown) {

			jogoffset -= JOGNUMBER;
		}

		prevJogUp = jogUp;
		prevJogDown = jogDown;
	}

}
