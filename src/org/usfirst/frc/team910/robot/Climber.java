package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;

public class Climber {

	CANTalon climbMotor;
	PowerDistributionPanel pdp;
	
	double CLIMB_POWER = 1.0;
	
	public Climber(PowerDistributionPanel pdp){
		this.pdp = pdp;
		climbMotor = new CANTalon(IO.CLIMBER_ARM_MOTOR);
	}
	
	boolean climberReleased = false;
	
	public void run(Joystick driveBoard){
		
		if(climberReleased){
			if(driveBoard.getRawButton(IO.CLIMB_1)){
				if(driveBoard.getRawButton(IO.CLIMB_2)){
					climbMotor.set(CLIMB_POWER);
				} else {
					climbMotor.set(-CLIMB_POWER);
				}
			}
			
		} else { //if climber not yet released then check if it should be
		
			boolean releaseClimber = driveBoard.getRawButton(IO.CLIMB_2) &&
					driveBoard.getRawButton(IO.CLIMB_1) && 
					driveBoard.getRawButton(IO.JOG_SHOOTER_UP);
			
			if(releaseClimber){
				//do something crazy
				climberReleased = true;
			}
		}
		
		
	}
	
	
	double AMP_THRESHOLD = 40;
	double STOP_TIME = 0.75; 
	double START_TIME = 4;	
	boolean SAFE_STOP = false;
	Timer armTime = new Timer();
	
	public void driveArm(double drive){
		double amps = pdp.getCurrent(IO.CLIMBER_ARM_MOTOR);
		
		if(SAFE_STOP){
			if(armTime.get() > START_TIME){
				armTime.reset();
				climbMotor.enable();
				SAFE_STOP = false;
			} else {
				climbMotor.disable();
			}
		} else {
	 		
			if (amps > AMP_THRESHOLD){
				if(armTime.get() > STOP_TIME){
					climbMotor.disable();
					SAFE_STOP = true;
				} else {
					climbMotor.set(drive);
				}
			} else {
				armTime.reset();
				climbMotor.set(drive);
			}
		}
	}
	
}
