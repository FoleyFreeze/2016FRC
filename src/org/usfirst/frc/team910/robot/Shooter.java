package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;

public class Shooter {
	CANTalon shooterWheelL;
	CANTalon shooterWheelR;
	CANTalon shooterArm;
	CANTalon loadWheelL;
	CANTalon loadWheelR;
	
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
	
	double MAX_RAMP_RATE = 0.02; // 1.5 second 0 to 1 ramp
	
	
	PowerDistributionPanel pdp;
	Timer time = new Timer();

	public Shooter(PowerDistributionPanel pdp) {
		this.pdp = pdp;
		time.start();
		armTime.start();
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
		//shooterArm.setPID(12, 0, 0);//was 8
		shooterArm.setInverted(true);//flipped for comp bot
		shooterArm.reverseOutput(true);//flipped for comp bot
		shooterArm.setFeedbackDevice(FeedbackDevice.AnalogEncoder);
		
		if(IO.COMP){
			shooterArm.configPeakOutputVoltage(9.0, -7.5); //up , down
			shooterArm.setPID(8, 0, 0);//was 8
		} else {
			shooterArm.configPeakOutputVoltage(9.5, -9.5);//was 9.0 -7.5
			shooterArm.setPID(10, 0, 0);//was 8
		}
		
		shooterArm.setAllowableClosedLoopErr(2); //3.28 was 5
		shooterArm.configNominalOutputVoltage(1.0, -1.0);//3.28 was 1,-1
		goToPositionControl(false);
	}

	public void goToPositionControl(boolean autoControl) {

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
		final double CEIL = BoulderController.SHOOTER_MAX_HEIGHT;
		final double FLOOR = 0;
		if (position < FLOOR) {
			//shooterArm.set(FLOOR);
			driveArm(FLOOR);
		} else if (position > CEIL) {
			//shooterArm.set(CEIL);
			driveArm(CEIL);
		} else {
			//shooterArm.set(position);
			driveArm(position);
		}
		
		//switch the minimum voltage at the top end of the 4bar to allow smoother movement
		if(shooterArm.getPosition() > BoulderController.SHOOTER_MIN_VOLT_SWITCH){
			if(!prevVoltSwitch){
				shooterArm.configNominalOutputVoltage(0.0, 0.0);
			}
			prevVoltSwitch = true;
		} else {
			if(prevVoltSwitch){
				shooterArm.configNominalOutputVoltage(0.0, -0.0); //was 1 -1
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
		setMotorPosition(position/* + jogoffset*/);
		
		//SmartDashboard.putNumber("***********ShooterPosition", position); 	//3.28 MrC

	}

	public double getPosition() {
		//Obtains shooter position
		return shooterArm.getPosition();
	}

	public void aquireGatherPosition(double position) {
		gatherPosition = position;
	}

	int primeState;
	double prevShooter = 0.0;

	public void prime(double shooterSpeed, boolean layup) {
		
		if(!layup){ //moved IO.POWER_ADJ to here instead of in every array, also allows POWER_ADJ to be jogged
			double shooterPower = IO.POWER_ADJ * IO.lookup(IO.MOTOR_POWERS, IO.DISTANCE_AXIS, Robot.vp.getDistance());
			SmartDashboard.putNumber("lookupPower", shooterPower);
			if(Robot.vp.getDistance() == 0){
				shooterPower = shooterSpeed;
			}
			shooterSpeed = shooterPower;
		}
		
		/*
		if (shooterSpeed > 0) {// for positive powers
			if (shooterSpeed > prevShooter + MAX_RAMP_RATE) {// if increasing power,
													// slowly ramp
				driveShooter = prevShooter + MAX_RAMP_RATE;
			} else {// if decreasing power, just do it
				driveShooter = shooterSpeed;
			}	
		} else {// for negative powers
			if (shooterSpeed < prevShooter - MAX_RAMP_RATE) {// if increasing negative
													// power, slowly ramp
				driveShooter = prevShooter - MAX_RAMP_RATE;
			} else {// if decreasing power, just do it
				driveShooter = shooterSpeed;
			}
				
		}*/
		
		prevShooter = shooterSpeed;
		shooterWheelL.set(shooterSpeed);
		shooterWheelR.set(shooterSpeed);
		
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
	
	public void backupFire(){
		loadWheelL.set(-0.3);
		loadWheelR.set(0.3);
	}

	public void manualShooter(double YAxisGamepadRight, boolean GamepadLBumper, double LoadWheelAxis) {
		//the gamepad's right joy can manually change the shooters position, the left bumper activates the shooter at full speed
		
		//dont allow full power in manual mode
		YAxisGamepadRight /= 1.5;
		
//		//shooterArm.set(YAxisGamepadRight); //flipped for comp bot
		driveArm(YAxisGamepadRight);
		loadWheelR.set(-LoadWheelAxis);//flipped for comp
		loadWheelL.set(LoadWheelAxis);//flipped for comp
		
		if (GamepadLBumper) {

			shooterWheelL.set(1); 
			shooterWheelR.set(1);

		} else {
			shooterWheelL.set(0);
			shooterWheelR.set(0);
		}
	}



	public void setLoadWheels(double speed) {
		//sets the speed of the load wheels
		loadWheelR.set(-speed);//flipped for comp
		loadWheelL.set(speed);//flipped for comp
	}
	
	double AMP_THRESHOLD = 30;
	double STOP_TIME = 0.5; // TODO : make larger for competition
	double START_TIME = 10;	// TODO : make smaller for competition
	boolean SAFE_STOP = false;
	Timer armTime = new Timer();
	
	public void driveArm(double drive){
		double amps = pdp.getCurrent(IO.SHOOTER_ARM);
		
		if(SAFE_STOP){
			if(armTime.get() > START_TIME){
				armTime.reset();
				shooterArm.enable();
				SAFE_STOP = false;
			} else {
				shooterArm.disable();
			}
		} else {
	 		
			if (amps > AMP_THRESHOLD){
				if(armTime.get() > STOP_TIME){
					shooterArm.disable();
					SAFE_STOP = true;
				} else {
					shooterArm.set(drive);
				}
			} else {
				armTime.reset();
				shooterArm.set(drive);
			}
		}
	}
}
