package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;

public class Shooter {
	CANTalon shooterWheelL;
	CANTalon shooterWheelR;
	CANTalon shooterArm;
	CANTalon loadWheelL;
	CANTalon loadWheelR;

	double jogoffset = 0;

	double shooterjogoffset = 0;
	
	double JOGNUMBER = 5;
	
	double SHOOTERJOGNUMBER = 0;
	
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
	
	double MAX_RAMP_RATE = 0.03; // one second 0 to 1 ramp
	
	PowerDistributionPanel pdp;
	Timer time = new Timer();

	public Shooter(PowerDistributionPanel pdp) {
		this.pdp = pdp;
		time.start();
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
		shooterArm.setPID(8, 0, 0);//flipped for comp bot
		shooterArm.setInverted(true);//flipped for comp bot
		shooterArm.reverseOutput(true);//flipped for comp bot
		shooterArm.setFeedbackDevice(FeedbackDevice.AnalogEncoder);
		shooterArm.configPeakOutputVoltage(9.0, -7.5); //up , down
		shooterArm.setAllowableClosedLoopErr(5);
		shooterArm.configNominalOutputVoltage(1.5, -1.5);
		autoAndback(false);
	}

	public void autoAndback(boolean autoControl) {

		if (!autoControl) {

			shooterArm.changeControlMode(TalonControlMode.PercentVbus);
			shooterArm.setInverted(true); //flipped for comp bot

		} else {

			shooterArm.changeControlMode(TalonControlMode.Position);
			shooterArm.reverseOutput(true);//flipped for comp bot
		}

	}

	boolean prevVoltSwitch = false;
	
	private void setMotorPosition(double position) {
		//Sets save points where the shooter should not pass, including positions near the floor and celing, respectively
		final double CEIL = BoulderController.SHOOTER_MAX_HEIGHT + 25;
		final double FLOOR = 0;
		if (position < FLOOR) {
			shooterArm.set(FLOOR);
		} else if (position > CEIL) {
			shooterArm.set(CEIL);
		} else {
			shooterArm.set(position);
		}
		
		//switch the minimum voltage at the top end of the 4bar to allow smoother movement
		if(shooterArm.getPosition() > BoulderController.SHOOTER_MIN_VOLT_SWITCH){
			if(!prevVoltSwitch){
				shooterArm.configNominalOutputVoltage(0.0, 0.0);
			}
			prevVoltSwitch = true;
		} else {
			if(prevVoltSwitch){
				shooterArm.configNominalOutputVoltage(3.0, -3.0);
			}
			prevVoltSwitch = false;
		}
	}

	public void gotoPosition(double position) {

		// if going down//
		/*if (shooterArm.getPosition() > position + jogoffset) {
			if (position + jogoffset > gatherPosition) {
				setMotorPosition(position + jogoffset);
			} else {
				setMotorPosition(gatherPosition + SAFETYDISTANCE);
			}
		} else {
			setMotorPosition(position + jogoffset);
		}*/
		setMotorPosition(position + jogoffset);
	}

	public double getPosition() {
		//Obtains shooter position
		return shooterArm.getPosition();
	}

	public void aquireGatherPosition(double position) {
		gatherPosition = position;
	}

	int primeState;

	public void prime(double shooterSpeed, double prevShoter, boolean shooterjogUp, boolean shooterjogDown) {
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
		shooterWheelL.set(1);//flipped for comp
		shooterWheelR.set(1);
		double driveShooter;
		if (shooterSpeed > 0) {// for positive powers
			if (shooterSpeed > prevShoter + MAX_RAMP_RATE) {// if increasing power,
													// slowly ramp
				driveShooter = prevShoter + MAX_RAMP_RATE;
				shooterWheelL.set(driveShooter);
			} else {// if decreasing power, just do it
				driveShooter = shooterSpeed;
				shooterWheelL.set(driveShooter);
			}	
		} else {// for negative powers
			if (shooterSpeed < prevShoter - MAX_RAMP_RATE) {// if increasing negative
													// power, slowly ramp
				driveShooter = prevShoter - MAX_RAMP_RATE;
				shooterWheelL.set(driveShooter);
			} else {// if decreasing power, just do it
				driveShooter = shooterSpeed;
				shooterWheelL.set(driveShooter);
			}
			boolean prevshooterjogUp = false;
			boolean prevshooterjogDown = false;
				if (shooterjogUp && !prevshooterjogUp) {

					shooterjogoffset += SHOOTERJOGNUMBER;
				} else if (shooterjogDown && !prevshooterjogDown) {

					shooterjogoffset -= SHOOTERJOGNUMBER;
				}

			prevshooterjogUp = shooterjogUp;
			prevshooterjogDown = shooterjogDown;
				
				
				
			}
		}
	

	public void fire() {
		//The shooter wheels set
		/*if (shooterWheelL.getSpeed() > FAST - MARGIN) {
			loadWheelL.set(loadWheelL.getPosition() + FIRE);
			loadWheelR.set(loadWheelR.getPosition() + FIRE);
		}*/
		loadWheelL.set(1);//flipped for comp
		loadWheelR.set(-1);//flipped for comp

	}

	public void manualShooter(double YAxisGamepadRight, boolean GamepadLBumper, double LoadWheelAxis) {
		//the gamepad's right joy can manually change the shooters position, the left bumper activates the shooter at full speed
		if (YAxisGamepadRight < 0) {
			YAxisGamepadRight /= 2;
		}
		shooterArm.set(YAxisGamepadRight); //flipped for comp bot
		loadWheelR.set(-LoadWheelAxis);//flipped for comp

		if (LoadWheelAxis < 0) {
			loadWheelL.set(0);
		} else {
			loadWheelL.set(LoadWheelAxis);//flipped for comp
		}
		if (GamepadLBumper) {

			shooterWheelL.set(1); //flipped for comp
			shooterWheelR.set(1);

		} else {
			shooterWheelL.set(0);
			shooterWheelR.set(0);
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
		loadWheelR.set(-speed);//flipped for comp

		if (speed < 0) {
			loadWheelL.set(0);
		} else {
			loadWheelL.set(speed);//flipped for comp
		}
	}
	
	double AMP_THRESHOLD = 20;
	double STOP_TIME = 5;
	double START_TIME = 5;
	boolean SAFE_STOP = false;
	
	public void driveArm(double drive){
		double amps = pdp.getCurrent(IO.SHOOTER_ARM);
		
		if(SAFE_STOP){
			if(time.get() > START_TIME){
				time.reset();
				shooterArm.enable();
				SAFE_STOP = false;
			}
		} else {
	 		
			if (amps > AMP_THRESHOLD){
				if(time.get() > STOP_TIME){
					shooterArm.disable();
					SAFE_STOP = true;
				} else {
					shooterArm.set(drive);
				}
			} else {
				time.reset();
				shooterArm.set(drive);
			}
		}
	}
	public void shooterRamp()
}
