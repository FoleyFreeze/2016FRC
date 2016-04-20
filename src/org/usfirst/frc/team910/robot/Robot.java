
package org.usfirst.frc.team910.robot;

import com.kauailabs.navx.frc.AHRS;
import com.ni.vision.NIVision;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	static final boolean TEST = false;
	
	double JOGNUMBER_DEG = 0.5;
	double lr_jog_deg = 0;
	
	DriveTrain drive;
	BoulderController BC;

	Solenoid ringOfFire;
	Solenoid irSensor;
	
	Solenoid blueLights1;
	Solenoid blueLights2;
	Solenoid redLights1;
	Solenoid redLights2;
	Solenoid greenLights1;
	Solenoid greenLights2;
	
	Joystick rJoy;
	Joystick lJoy;
	Joystick driveBoard;
	Joystick GamePad;

	AHRS navX;

	//AnalogInput dSensor;

	Auton auton;
	
	PowerDistributionPanel pdp;
	
	static VisionProcessor vp = new VisionProcessor();
	boolean visionWorking = false;
	boolean visionInit = false;
	
	public void robotInit() {

		
		ringOfFire = new Solenoid(3);
		irSensor = new Solenoid(4);
		blueLights1 = new Solenoid(2);
		blueLights2 = new Solenoid(7);
		redLights1 = new Solenoid(0);
		redLights2 = new Solenoid(5);
		greenLights1 = new Solenoid(1);
		greenLights2 = new Solenoid(6);
		
		
		navX = new AHRS(SPI.Port.kMXP); // SPI.Port.kMXP
		pdp = new PowerDistributionPanel();
		
		drive = new DriveTrain(navX);
		BC = new BoulderController(pdp, drive);

		lJoy = new Joystick(IO.LEFT_JOYSTICK);

		rJoy = new Joystick(IO.RIGHT_JOYSTICK);
		GamePad = new Joystick(IO.GAME_PAD);

		driveBoard = new Joystick(IO.DRIVE_BOARD);

		//dSensor = new AnalogInput(3);

		// setup things for camera switching
		//BetterCameraServer.init("cam0", "cam1");
		//CameraServer.getInstance().startAutomaticCapture("cam2");
		time.start();

		
		
		
		//setup the autons
		auton = new Auton(navX, drive, BC, true);
		/*
		auton.chooser = new SendableChooser();
		auton.chooser.addDefault("DoNothing Auto", auton.defaultAuto);
		auton.chooser.addObject("CrossCamShootAuto", auton.crossCamShootAuto);
		auton.chooser.addObject("JustDriveAuto", auton.justDriveAuto);
		SmartDashboard.putData("Auto choices", auton.chooser);
		
		
		auton.autoSelected = (String) auton.chooser.getSelected();
		SmartDashboard.putString("Auto", auton.autoSelected);
		*/
		
		vp.findCamera();		//Search through all the possible cameras and remember which one we find. This CLOSES the camera when done!
		camTime.start();
	}

	public void autonomousInit(){  
		
	}

	/*
	 * This function is called periodically during autonomous
	 * 
	 */

	public void autonomousPeriodic() {
		// Drive over defenses
		/*
		 * switch (autonstate){
		 * 
		 * case 0: autonstate = 1; break;
		 * 
		 * case 1: drive.compassDrive(1, navX.getYaw(), false, 0);// Compass
		 * drive break;
		 * 
		 * case 2: drive.compassDrive(1, navX.getYaw(), false, 0); if
		 * (getAvgAccel() < 0.3) autonstate = 3; break;
		 * 
		 * case 3: drive.tankDrive(0, 0); break;
		 * 
		 * case 4: //lowbar position 1 (left to right) drive.compassDrive(1,
		 * navX.getYaw(), false, 0);// Compass drive //wait 10ms
		 * drive.compassDrive(-1, navX.getYaw(), false, 0); //wait 10ms
		 * drive.compassDrive(1, navX.getYaw(), false, 0); //wait 10ms
		 * drive.compassDrive(-1, navX.getYaw(), false, 0); break;
		 * 
		 * case 5: //rock-wall/rough-terrain position 2 drive.compassDrive(1,
		 * navX.getYaw(), false, 0);// Compass drive //wait 10ms
		 * drive.compassDrive(-1, navX.getYaw(), false, 0); //wait 10ms
		 * drive.compassDrive(1, navX.getYaw(), false, 0); //wait 10ms
		 * drive.compassDrive(-1, navX.getYaw(), false, 0);
		 * 
		 * case 6: //drawbridge/sallyport position 3
		 * 
		 * case 7: //moat/ramparts position 4 drive.compassDrive(1,
		 * navX.getYaw(), false, 0);// Compass drive //wait 10ms
		 * drive.compassDrive(-1, navX.getYaw(), false, 0); //wait 10ms
		 * drive.compassDrive(1, navX.getYaw(), false, 0); //wait 10ms
		 * drive.compassDrive(-1, navX.getYaw(), false, 0); case 8:
		 * //portcullis/ cheval-de-frise position 5
		 * 
		 * }
		 */

		//comment out to disable auton
		auton.runAuto();
		ringOfFire.set(true);
		irSensor.set(true);
		//blueLights1.set(true);
		//blueLights2.set(true);

	}

	// called when disabled
	public void disabledPeriodic() {
		SmartDashboard.putNumber("navX Pitch", navX.getPitch());
		SmartDashboard.putNumber("navX Yaw", navX.getYaw());
		SmartDashboard.putNumber("navX Roll", navX.getRoll());
		//SmartDashboard.putNumber("navX X", navX.getRawGyroX());
		//SmartDashboard.putNumber("navX Y", navX.getRawGyroY());
		//SmartDashboard.putNumber("navX Z", navX.getRawGyroZ());
		//SmartDashboard.putNumber("DistanceSensor", dSensor.getVoltage());
		//SmartDashboard.putNumber("accel X", navX.getRawAccelX());
		//SmartDashboard.putNumber("accel Y", navX.getRawAccelY());
		//SmartDashboard.putNumber("accel Z", navX.getRawAccelZ());
		SmartDashboard.putNumber("gather pot",BC.gatherer.gatherArm.getPosition());
		//auton.autoSelected = (String) auton.chooser.getSelected();
		//SmartDashboard.putString("Auto", auton.autoSelected);
		SmartDashboard.putNumber("shooter pot",BC.shooter.shooterArm.getPosition());
		SmartDashboard.putNumber("shooter CLE", BC.shooter.shooterArm.getClosedLoopError());
		
		SmartDashboard.putNumber("gather CLE",BC.gatherer.gatherArm.getClosedLoopError());
		SmartDashboard.putNumber("gather setpt", BC.gatherer.gatherArm.getSetpoint());
		
		SmartDashboard.putBoolean("ballSensor", BC.ballSensor.get());
		
		SmartDashboard.putNumber("LoadWheelL", pdp.getCurrent(IO.SHOOTER_WHEEL_L));
		SmartDashboard.putNumber("LoadWheelR", pdp.getCurrent(IO.SHOOTER_WHEEL_R));
		
		SmartDashboard.putNumber("L Encoder", drive.lEncoder.getDistance());
		SmartDashboard.putNumber("R Encoder", drive.rEncoder.getDistance());
		
		double ballDist = BC.ballDistSensor.getAverageVoltage();
		SmartDashboard.putNumber("BallDist", ballDist);
		
		//vp.disabled();
		
		auton.selectAuto(lJoy);
		
		SmartDashboard.putBoolean("CAMERA_CRASHED", vp.visionCrashed);
		SmartDashboard.putBoolean("CAMERA_RUNNING", vp.visionSetupWorked);
		SmartDashboard.putNumber("Camera Running Time", vp.onlineTime.get());
		
		if(rJoy.getRawButton(7)){
			vp.setupCamera();
		} else if(rJoy.getRawButton(8)){
			vp.closeCamera();
		} else if(rJoy.getRawButton(9)){
			vp.run();
		} else if(rJoy.getRawButton(10)){
			vp.setupCamera();
			vp.run();
			vp.closeCamera();
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	boolean previousMode = false;
	boolean prevFlipControls = false;
	boolean firstTime = true;
	boolean capCam = false;
	Timer time = new Timer();
	
	int cameraState = 0;
	double cameraAngle = 0;
	Timer camTime = new Timer();
	
	public void teleopPeriodic() {
		
		blueLights1.set(true);
		//blueLights2.set(true);
		//redLights1.set(true);
		//redLights2.set(true);
		//greenLights1.set(true);
		//greenLights2.set(true);
		
		ringOfFire.set(true);
		irSensor.set(true);
		/* Controls all teleop operations, including the automatic gatherer and shooter positions,
		 * manual gathering and shooting, and manual movement 
		 */
		//BetterCameraServer.start();

		// this means on = auto, off = manual. Add ! before driveBoard to flip
		boolean automaticMode = driveBoard.getRawButton(IO.MAN_AUTO_SW);
		if (automaticMode) {

			if(cameraState != 3){ //when camera state is 3, the camera runs the shooter
				BC.gatherer.goToPositionControl(automaticMode);
				BC.shooter.goToPositionControl(automaticMode);
				
				BC.runBC(driveBoard);
			}
			
			SmartDashboard.putNumber("shooterArm setpoint", BC.shooter.shooterArm.getSetpoint());

		}

		else {
			//if (driveBoard.getRawButton(IO.MAN_AUTO_SW) != previousMode) {
				// Call Mode Switch Function
				BC.gatherer.goToPositionControl(automaticMode);
				BC.shooter.goToPositionControl(automaticMode);
				BC.gatherState = 1;
				BC.buttonState = -1;
				BC.regrippingState = 0;
				BoulderController.chevalState = 0;
			//}
			// call manual position functions
			BC.gatherer.manualGather(-GamePad.getRawAxis(1) * 0.5, GamePad.getRawButton(1),GamePad.getRawButton(2));
			// BC.gatherer.gatherwheel(GamePad.getRawAxis(5));
			BC.shooter.manualShooter(-GamePad.getRawAxis(5) * 1, GamePad.getRawButton(5), GamePad.getRawAxis(3) - GamePad.getRawAxis(2));
		}
		previousMode = automaticMode;

		boolean flipControls = driveBoard.getRawButton(IO.FLIP_CONTROLS);

		double YAxisLeft = -lJoy.getY();
		double YAxisRight = -rJoy.getY();

		//if (flipControls != prevFlipControls)
			//BetterCameraServer.switchCamera();

		if (flipControls) {

			YAxisLeft = lJoy.getY();
			YAxisRight = rJoy.getY();
		} else {

			YAxisLeft = -lJoy.getY();
			YAxisRight = -rJoy.getY();
		}
		prevFlipControls = flipControls;

		//allow jog up and down
		if(driveBoard.getRawButton(IO.LR_JOG_BTN)){
			jog(driveBoard.getRawButton(IO.JOG_SHOOTER_UP),
					driveBoard.getRawButton(IO.JOG_SHOOTER_DOWN));
		} else {
			BC.jog(driveBoard.getRawButton(IO.JOG_SHOOTER_UP),
						driveBoard.getRawButton(IO.JOG_SHOOTER_DOWN));
		}

		int angle = WASDToAngle(driveBoard.getRawButton(IO.WASD_W), driveBoard.getRawButton(IO.WASD_A),
				driveBoard.getRawButton(IO.WASD_S), driveBoard.getRawButton(IO.WASD_D));

		double diff;
		//auto camera aim
		if(lJoy.getRawButton(IO.AIM_CAMERA) && navX.isConnected()){
			switch(cameraState){
			case 0: //start the camera
				vp.setupCamera();
				cameraState = 1;
				break;
			
			case 1://take and analyze picture
				vp.run();
				//keep trying until we get a good image
				if(vp.goodTarget){
					cameraAngle = vp.getAngle() + navX.getYaw();
					cameraState = 2;
					vp.getDistance();
				}
				break;
				
			case 2://align shooter
				drive.cameraAlign(cameraAngle + lr_jog_deg, navX.getYaw());
				SmartDashboard.putNumber("cameraAngle", cameraAngle - navX.getYaw());
				SmartDashboard.putBoolean("goodTarget", vp.goodTarget);
				double dist = vp.getDistance();
				if(dist == 0){
					BC.visionAngleOffset = 0;
				} else {
					BC.visionAngleOffset = IO.lookup(IO.SHOOTER_ANGLE, IO.DISTANCE_AXIS, vp.getDistance());
				}
				
				diff = cameraAngle + lr_jog_deg - navX.getYaw();
				if (diff > 180) {
					diff = -360 + diff;
				} else if (diff < -180) {
					diff = 360 + diff;
				}
				BC.prime();	
				if(Math.abs(diff) <= 0.15){ //acceptable target angle error
					cameraState = 3;
					camTime.reset();
				}
				break;
				
			case 3: //shoot if ready
				diff = cameraAngle + lr_jog_deg - navX.getYaw();
				if (diff > 180) {
					diff = -360 + diff;
				} else if (diff < -180) {
					diff = 360 + diff;
				}
				if(Math.abs(diff) > 0.15){//acceptable target angle error
					cameraState = 2;
				}
				
				if(automaticMode && BC.buttonState == 2){
					BC.farShot();
					BC.prime();
					if(camTime.get() >= 0.5){//time to prime for
						BC.shooter.fire();
						//Robot.vp.closeCamera(); //try keeping the camera open forever
						BC.prevFire = true;
					}
				}
			}
			vp.getDistance();
			
		} else { //if camera is not auto aiming then allow driving 
			drive.run(YAxisLeft, YAxisRight, (double) angle, rJoy.getTrigger(), lJoy.getTrigger(),
					rJoy.getRawButton(IO.COMPASS_POWER_THROTTLE), rJoy.getThrottle(), lJoy.getRawButton(IO.DO_A_180_BTN));
			cameraState = 0;
		}
		
		if (rJoy.getRawButton(IO.ZERO_YAW)) {
			navX.zeroYaw();
		}

		SmartDashboard.putNumber("wasd angle", angle);
		SmartDashboard.putNumber("navX Pitch", navX.getPitch());
		SmartDashboard.putNumber("navX Yaw", navX.getYaw());
		SmartDashboard.putNumber("navX Roll", navX.getRoll());
		//SmartDashboard.putNumber("navX X", navX.getRawGyroX());
		//SmartDashboard.putNumber("navX Y", navX.getRawGyroY());
		//SmartDashboard.putNumber("navX Z", navX.getRawGyroZ());
		//SmartDashboard.putNumber("DistanceSensor", dSensor.getVoltage());
		//SmartDashboard.putNumber("accel X", navX.getRawAccelX());
		//SmartDashboard.putNumber("accel Y", navX.getRawAccelY());
		//SmartDashboard.putNumber("accel Z", navX.getRawAccelZ());
		// SmartDashboard.putNumber("avgAccel", getAvgAccel());

		SmartDashboard.putNumber("pdp 3 g-arm", pdp.getCurrent(3));
		SmartDashboard.putNumber("pdp 4 g-wheel", pdp.getCurrent(4));
		SmartDashboard.putNumber("pdp 12 s-arm", pdp.getCurrent(12));
		//SmartDashboard.putNumber("pdp 6", pdp.getCurrent(6));
		//SmartDashboard.putNumber("pdp 7", pdp.getCurrent(7));
		//SmartDashboard.putNumber("pdp 8", pdp.getCurrent(8));
		
		SmartDashboard.putNumber("shooter pot",BC.shooter.shooterArm.getPosition());
		SmartDashboard.putNumber("shooter CLE", BC.shooter.shooterArm.getClosedLoopError());
		SmartDashboard.putNumber("gather pot",BC.gatherer.gatherArm.getPosition());
		SmartDashboard.putNumber("gather CLE",BC.gatherer.gatherArm.getClosedLoopError());
		SmartDashboard.putNumber("gather setpt", BC.gatherer.gatherArm.getSetpoint());
		
		SmartDashboard.putNumber("LoadWheelL", pdp.getCurrent(IO.SHOOTER_WHEEL_L));
		SmartDashboard.putNumber("LoadWheelR", pdp.getCurrent(IO.SHOOTER_WHEEL_R));
		
		SmartDashboard.putBoolean("ballSensor", BC.ballSensor.get());
		
		//SmartDashboard.putNumber("cycle time", time.get());
		
		SmartDashboard.putBoolean("CAMERA_CRASHED", vp.visionCrashed);
		SmartDashboard.putBoolean("CAMERA_RUNNING", vp.visionSetupWorked);
		SmartDashboard.putNumber("Camera Running Time", vp.onlineTime.get());
		
		time.reset();
		
		if(rJoy.getRawButton(10)){
			vp.visionCrashed = false;
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

	public int WASDToAngle(boolean W, boolean A, boolean S, boolean D) {
		//Controls Compass Drive
		if (W) {
			if (A) {
				return -45;
			} else if (D) {
				return 45;
			} else {
				return 0;

			}
		} else if (S) {
			if (A) {
				return -135;
			} else if (D) {
				return 135;
			} else {
				return 180;
			}
		} else if (A) {
			return -90;
		} else if (D) {
			return 90;
		} else {
			return -5000;
		}
	}

	double accelArray[] = { 0, 0, 0, 0, 0 };
	int accelArrayIndex = 0;

	public double getAvgAccel() {
		accelArray[accelArrayIndex] = navX.getRawAccelX() + navX.getRawAccelY();
		accelArrayIndex++;
		if (accelArrayIndex >= accelArray.length)
			accelArrayIndex = 0;

		double accel = 0;
		for (int i = 0; i < accelArray.length; i++) {
			accel += accelArray[i];
		}
		return accel / accelArray.length;
	}
	
	boolean prevJogUp = false;
	boolean prevJogDown = false;
	
	public void jog(boolean jogUp, boolean jogDown) {
		// Adds a static amount to the shooter's position, up or down
		if (jogUp && !prevJogUp) {

			lr_jog_deg += JOGNUMBER_DEG;
		} else if (jogDown && !prevJogDown) {

			lr_jog_deg -= JOGNUMBER_DEG;
		}

		prevJogUp = jogUp;
		prevJogDown = jogDown;
	}
}
