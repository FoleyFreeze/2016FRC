package org.usfirst.frc.team910.robot;


import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;

public class Gatherer {

	CANTalon gatherer;
	CANTalon gatherArm;

	double shooterPosition;
	double SAFETYDISTANCE = 25;

	public Gatherer() {
		// constructor for gatherer and gatherArm
		gatherer = new CANTalon(IO.GATHERER);
		gatherArm = new CANTalon(IO.GATHER_ARM);

		autoAndback(false);
		gatherArm.enableBrakeMode(true);
		gatherArm.configPeakOutputVoltage(7.0, -3.0);
		gatherArm.setPID(20, 0.05, 0.0); //used to be 30 , 0.05
		gatherArm.setFeedbackDevice(FeedbackDevice.AnalogEncoder);
		autoAndback(false);
	}

	public void autoAndback(boolean autoControl) {

		if (!autoControl) {

			gatherArm.changeControlMode(TalonControlMode.PercentVbus);

		} else {

			gatherArm.changeControlMode(TalonControlMode.Position);

		}
		
		

	}
	
	boolean prevVoltSwitch = false;

	public void gotoPosition(double position) {

		// if going up//
		/*if (gatherArm.getPosition() < position) {
			if (position < shooterPosition) {
				gatherArm.set(position);
			} else {
				gatherArm.set(shooterPosition - SAFETYDISTANCE);
			}
		} else {
			gatherArm.set(position);
		}*/
		gatherArm.set(position);
		
		if(gatherArm.getPosition() > BoulderController.GATHER_STOW_POS){
			gatherArm.ClearIaccum();
		}
		
		//switch the maximum voltage at the top end to allow smoother movement
		if(gatherArm.getPosition() > BoulderController.GATHER_STOW_POS + 30){
			//if(!prevVoltSwitch){
				gatherArm.configPeakOutputVoltage(2.0, -3.0);
			//}
			//prevVoltSwitch = true;
		} else {
			//if(prevVoltSwitch){
				gatherArm.configPeakOutputVoltage(7.0, -3.0);
			//}
			//prevVoltSwitch = false;
		}
	}

	public void aquireShooterPosition(double position) {
		shooterPosition = position;
	}

	public void gatherwheel(double speed) {
		// sets gatherer speed
		gatherer.set(speed);
	}

	public double getPosition() {
		return gatherArm.getPosition();
	}

	public void manualGather(double YAxisGamePadRight, boolean intakeForward, boolean intakeBackward) {
		// controls for manual gathering, use the gamepad
		if (intakeForward) {
			gatherer.set(-1);
		} else if (intakeBackward) {
			gatherer.set(1);
		} else {
			gatherer.set(0);
		}
		gatherArm.set(YAxisGamePadRight);

	}

}
