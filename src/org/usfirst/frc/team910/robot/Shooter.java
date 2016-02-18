package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;

public class Shooter {
	CANTalon shooterWheel;
	CANTalon shooterArm;
	CANTalon loadWheels;

	double PRIMESPEED = 10000;

	double CLOSESHOT = 1000;

	double FARSHOT = 2000;

	double LAUNCH = 3000;

	double LOAD = 0;

	double REVERSE = 50;

	double FAST = 9001;
	
	double MARGIN = 50;
	
	double FIRE = 60;

	
	
	public void autoAndback(boolean manualControl){
		
		if(manualControl){
	
			shooterArm.changeControlMode(TalonControlMode.PercentVbus);
			shooterWheel.changeControlMode(TalonControlMode.Speed);
		}
			else {
				
				shooterArm.changeControlMode(TalonControlMode.Position);
				shooterWheel.changeControlMode(TalonControlMode.Speed);
				
			}
			
		}
	
	
	public Shooter() {
		shooterWheel = new CANTalon(IO.SHOOTER_WHEEL);
		shooterArm = new CANTalon(IO.SHOOTER_ARM);


	}

	
	public void gotoPosition(double position) {

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

	/*
	 * public void Launch(boolean prime) {
	 * 
	 * if (prime) { shooterWheel.set(PRIMESPEED); }
	 * 
	 * else { shooterWheel.set(0); }
	 * 
	 * }
	 */

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
		if (shooterWheel.getSpeed() > FAST - MARGIN);{
		loadWheels.set(loadWheels.getPosition() + FIRE);
		
		}
		

	}

	public boolean inTheWay() {
	
	public void fire(){
		
		
		
		
		
		
	}
	
	public boolean inTheWay(){
		return false;
	}

	
	public void setLoadWheels(double speed){
		
	}


	public void manualShooter(double YAxisGamepadRight, boolean GamepadLBumper){
		
		shooterArm.changeControlMode(TalonControlMode.PercentVbus);
		
		shooterArm.set(YAxisGamepadRight);
		
		if(GamepadLBumper == true) {
			
			 shooterWheel.set(PRIMESPEED);
			 
		}
		
			 else{
				 
				 shooterWheel.set(LOAD);
			 }
		}
	}


