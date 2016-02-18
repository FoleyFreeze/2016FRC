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
	
	public void gotoPosition(double position){
		
	}

	public void Position(boolean close, boolean loading) {
		if (loading) {
			shooterArm.set(LOAD);
		}

		else if (close) {
			shooterArm.set(CLOSESHOT);

		}
		else {
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


