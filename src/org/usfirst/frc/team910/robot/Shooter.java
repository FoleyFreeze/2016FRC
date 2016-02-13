package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;

public class Shooter {
	CANTalon shooterWheel;
	CANTalon shooterArm;

	double PRIMESPEED = 10000;

	double CLOSESHOT = 1000;

	double FARSHOT = 2000;

	double LAUNCH = 3000;

	double LOAD = 0;

	double gatherPosition;

	double SAFETYDISTANCE = 500;

	public Shooter() {
		shooterWheel = new CANTalon(IO.SHOOTER_WHEEL);
		shooterArm = new CANTalon(IO.SHOOTER_ARM);

		shooterWheel.changeControlMode(TalonControlMode.Speed);
		shooterArm.changeControlMode(TalonControlMode.Position);

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

	public void Position(boolean close, boolean loading) {
		if (loading) {
			shooterArm.set(LOAD);
		}

		else if (close) {
			shooterArm.set(CLOSESHOT);

		} else {
			shooterArm.set(FARSHOT);
		}
	}

	public void Launch(boolean prime) {

		if (prime) {
			shooterWheel.set(PRIMESPEED);
		}

		else {
			shooterWheel.set(0);
		}

	}

	public void fire() {

	}

	public boolean inTheWay() {
		return false;
	}

}
