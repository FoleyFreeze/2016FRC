package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;

public class Gatherer {

	CANTalon gatherer;
	CANTalon gatherArm;

	double shooterPosition;
	double SAFETYDISTANCE = 500;

	public Gatherer() {
		// constructor for gatherer and gatherArm
		gatherer = new CANTalon(IO.GATHERER);
		gatherArm = new CANTalon(IO.GATHER_ARM);

		autoAndback(true);
		gatherArm.enableBrakeMode(true);
		gatherArm.configPeakOutputVoltage(7.0, -7.0);
		gatherArm.setPID(0.05, 0.001, 0.0);
		gatherArm.setFeedbackDevice(FeedbackDevice.AnalogEncoder);
		autoAndback(true);
	}

	public void autoAndback(boolean manualControl) {

		if (manualControl) {

			gatherArm.changeControlMode(TalonControlMode.PercentVbus);

		} else {

			gatherArm.changeControlMode(TalonControlMode.Position);

		}

	}

	public void gotoPosition(double position) {

		// if going up//
		if (gatherArm.getPosition() < position) {
			if (position < shooterPosition) {
				gatherArm.set(position);
			} else {
				gatherArm.set(shooterPosition - SAFETYDISTANCE);
			}
		} else {
			gatherArm.set(position);
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
