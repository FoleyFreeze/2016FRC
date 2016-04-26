package org.usfirst.frc.team910.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;

public class Climber {

	CANTalon climbMotor;
	PowerDistributionPanel pdp;
	Servo releaseServo;
	AHRS navX;
	
	double CLIMB_POWER = 1.0;
	double SERVO_START_VAL = 0.0;
	double SERVO_RELEASE_VAL = 1.0;
	
	public Climber(PowerDistributionPanel pdp, AHRS navX){
		this.pdp = pdp;
		this.navX = navX;
		climbMotor = new CANTalon(IO.CLIMBER_ARM_MOTOR);
		releaseServo = new Servo(IO.CLIMB_SERVO);
	}
	
	boolean climberReleased = false;
	boolean climberClimbing = false;
	double climberRequestedShootArmPower = 0;
	Timer poweredClimbTimer = new Timer();
	double poweredClimbTime = 0;
	
	public void run(Joystick driveBoard){
		
		if(climberReleased){
			if(driveBoard.getRawButton(IO.CLIMB_1)){
				if(driveBoard.getRawButton(IO.CLIMB_2)){
					climberClimbing = true;
					
					climbMotor.set(CLIMB_POWER);
					poweredClimbTimer.stop();
					poweredClimbTime += poweredClimbTimer.get();
					poweredClimbTimer.start();
					
					if(poweredClimbTime > 1.0){
						//ONLY ENABLE THIS IF YOU KNOW WHAT YOU ARE DOING
						//keepLevel();
					} else {
						climberRequestedShootArmPower = 0;
					}
					
				} else {
					climbMotor.set(-CLIMB_POWER);
				}
			} else {
				//add the climbing time to the timer
				poweredClimbTimer.stop();
				poweredClimbTime += poweredClimbTimer.get();
				climberRequestedShootArmPower = 0;
			}
			
		} else { //if climber not yet released then check if it should be
		
			boolean releaseClimber = driveBoard.getRawButton(IO.CLIMB_2) &&
					driveBoard.getRawButton(IO.CLIMB_1) && 
					driveBoard.getRawButton(IO.JOG_SHOOTER_UP);
			
			if(releaseClimber){
				//do something crazy
				climberReleased = true;
				releaseServo.set(SERVO_RELEASE_VAL);
			} else {
				releaseServo.set(SERVO_START_VAL);
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
	
	
	double ROLL_DEADBAND = 2; //degrees
	double P_VAL = 0.05; //20 degree deviation applies full power (might be too aggressive?)
	// WARNING: UNTESTED CODE. Not responsible for breaking robots
	public void keepLevel() {
		double roll = navX.getRoll(); //get pitch from navX (its orientation means we are actually reading its roll() value)
		
		//if its significant enough that we want to do something about it
		if(Math.abs(roll) > ROLL_DEADBAND){
			//positive roll means robot is rocking backwards
			//moving the shooter arm down means lifting the back of the robot
			//therefore we apply negative shooter arm power when we have positive roll in order to pull it back up
			climberRequestedShootArmPower = -roll * P_VAL;
		} else {
			climberRequestedShootArmPower = 0;
		}
	}
	
}
