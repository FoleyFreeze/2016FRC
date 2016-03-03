package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;

public class Shooter {
	CANTalon shooterWheelL;
	CANTalon shooterWheelR;
	CANTalon shooterArm;
	CANTalon loadWheelL;
	CANTalon loadWheelR;

	double jogoffset = 0;

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

	double SAFETYDISTANCE = 25;

	public Shooter() {
		shooterWheelL = new CANTalon(IO.SHOOTER_WHEEL_L);
		shooterWheelR = new CANTalon(IO.SHOOTER_WHEEL_R);
		shooterArm = new CANTalon(IO.SHOOTER_ARM);
		loadWheelL = new CANTalon(IO.LOAD_WHEEL_L);
		loadWheelL.enableBrakeMode(true);
		loadWheelR = new CANTalon(IO.LOAD_WHEEL_R);
		loadWheelR.enableBrakeMode(true);
		loadWheelR.reverseOutput(false);
		// shooterWheelL.changeControlMode(TalonControlMode.Speed);
		//shooterWheelR.changeControlMode(TalonControlMode.Follower);
		//shooterWheelR.set(IO.SHOOTER_WHEEL_L);
		shooterWheelR.changeControlMode(TalonControlMode.PercentVbus);
		shooterWheelL.changeControlMode(TalonControlMode.PercentVbus);
		shooterWheelR.reverseOutput(false);
		shooterWheelR.enableBrakeMode(false);
		shooterWheelL.enableBrakeMode(false);
		
		shooterArm.changeControlMode(TalonControlMode.Position);
		//shooterArm.setProfile(0);
		shooterArm.setPID(20, 0, 0);
		shooterArm.setFeedbackDevice(FeedbackDevice.AnalogEncoder);
		shooterArm.configPeakOutputVoltage(9.0, -7.5); //up , down
		shooterArm.setAllowableClosedLoopErr(10);
		shooterArm.configNominalOutputVoltage(5.0, -5.0);
		autoAndback(false);
	}

	public void autoAndback(boolean manualControl) {

		if (!manualControl) {

			shooterArm.changeControlMode(TalonControlMode.PercentVbus);

		} else {

			shooterArm.changeControlMode(TalonControlMode.Position);

		}

	}

	private void setMotorPosition(double position) {
		final double CEIL = 1000;
		final double FLOOR = 0;
		if (position < FLOOR) {
			shooterArm.set(FLOOR);
		} else if (position > CEIL) {
			shooterArm.set(CEIL);
		} else {
			shooterArm.set(position);
		}
	}

	public void gotoPosition(double position) {

		// if going down//
		if (shooterArm.getPosition() > position + jogoffset) {
			if (position + jogoffset > gatherPosition) {
				setMotorPosition(position + jogoffset);
			} else {
				setMotorPosition(gatherPosition + SAFETYDISTANCE);
			}
		} else {
			setMotorPosition(position + jogoffset);
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
		/*switch (primeState) {
		case 1:
			loadWheelL.set(loadWheelL.getPosition() - REVERSE);
			primeState = 2;
			break;

		case 2:
			if (loadWheelL.getPosition() < loadWheelL.getSetpoint()) {
				primeState = 3;
			}
			break;

		case 3:

			shooterWheelL.set(FAST);
			break;
		}*/
		shooterWheelL.set(1);
		shooterWheelR.set(1);
	}

	public void fire() {
		/*if (shooterWheelL.getSpeed() > FAST - MARGIN) {
			loadWheelL.set(loadWheelL.getPosition() + FIRE);
			loadWheelR.set(loadWheelR.getPosition() + FIRE);
		}*/
		loadWheelL.set(1);
		loadWheelR.set(1);

	}

	public void manualShooter(double YAxisGamepadRight, boolean GamepadLBumper, double LoadWheelAxis) {
		if (YAxisGamepadRight < 0) {
			YAxisGamepadRight /= 2;
		}
		shooterArm.set(YAxisGamepadRight);
		loadWheelL.set(-LoadWheelAxis);

		if (LoadWheelAxis < 0) {
			loadWheelR.set(0);
		} else {
			loadWheelR.set(LoadWheelAxis);
		}
		if (GamepadLBumper) {

			shooterWheelL.set(1);
			shooterWheelR.set(1);

		} else {
			shooterWheelL.set(0);
			shooterWheelR.set(0);
		}
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

	public void setLoadWheels(double speed) {
		loadWheelL.set(-speed);

		if (speed < 0) {
			loadWheelR.set(0);
		} else {
			loadWheelR.set(speed);
		}
	}

}
