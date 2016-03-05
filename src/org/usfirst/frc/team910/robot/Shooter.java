package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;

public class Shooter {
	CANTalon shooterWheelL;
	CANTalon shooterWheelR;
	CANTalon shooterArm;
	CANTalon loadWheelL;
	CANTalon loadWheelR;

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
		shooterWheelL = new CANTalon(IO.SHOOTER_WHEEL_L);
		shooterWheelR = new CANTalon(IO.SHOOTER_WHEEL_R);
		shooterArm = new CANTalon(IO.SHOOTER_ARM);
		loadWheelL = new CANTalon(IO.LOAD_WHEEL_L);
		loadWheelL.enableBrakeMode(true);
		loadWheelR = new CANTalon(IO.LOAD_WHEEL_R);
		loadWheelR.enableBrakeMode(true);
		loadWheelR.reverseOutput(false);
		// shooterWheelL.changeControlMode(TalonControlMode.Speed);
		shooterWheelR.changeControlMode(TalonControlMode.Follower);
		shooterWheelR.set(IO.SHOOTER_WHEEL_L);
		shooterWheelR.reverseOutput(false);
		shooterWheelR.enableBrakeMode(false);
		shooterWheelL.enableBrakeMode(false);
	}
//switches the shooter arm from working according to power or position according to whether the robot is in manual control mode
	public void autoAndback(boolean manualControl) {

		if (manualControl) {

			shooterArm.changeControlMode(TalonControlMode.PercentVbus);

		} else {

			shooterArm.changeControlMode(TalonControlMode.Position);

		}

	}
//sets stops for the shooter arm so it dosen't go too high or too low
	private void setMotorPosition(double position) {
		//Sets save points where the shooter should not pass, including positions near the floor and celing, respectively
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
//adds a static amount to or from the shooter arm to slightly raise or lower it
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
		//Obtains shooter position
		return shooterArm.getPosition();
	}

	public void aquireGatherPosition(double position) {
		gatherPosition = position;
	}

	int primeState;
//gets loads wheels to push the ball slightly forward until they are past a certain position. Once past this positions the shooter wheels speed up to full speed.
	public void prime() {
		//This sets the shooter wheels to an optimal speed before sending the ball threw them
		switch (primeState) {
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
		}
	}
//when the speed of the shooter wheels pass a certain range of speed, the shooter wheels accelerate and push the ball into the shooter wheels.
	public void fire() {
		//The shooter wheels set
		if (shooterWheelL.getSpeed() > FAST - MARGIN) {
			loadWheelL.set(loadWheelL.getPosition() + FIRE);
			loadWheelR.set(loadWheelR.getPosition() + FIRE);
		}

	}
//manual shooter mode. maps the shooter arm to the y axis of the right gamepad analogue stick. maps the load wheel position to its own axis. 
	public void manualShooter(double YAxisGamepadRight, boolean GamepadLBumper, double LoadWheelAxis) {
		//the gamepad's right joy can manually change the shooters position, the left bumper activates the shooter at full speed
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

		} else {
			shooterWheelL.set(0);
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

	public void setLoadWheels(double speed) {
		//sets the speed of the load wheels
		loadWheelL.set(-speed);

		if (speed < 0) {
			loadWheelR.set(0);
		} else {
			loadWheelR.set(speed);
		}
	}

}
