package org.usfirst.frc.team910.robot;

public class IO {
	// inputs
	public static final int LEFT_DRIVE_MOTOR = 1;
	public static final int RIGHT_DRIVE_MOTOR = 0;

	public static final int LEFT_DRIVE_A_ENCODER = 2;
	public static final int LEFT_DRIVE_B_ENCODER = 3;
	public static final int RIGHT_DRIVE_A_ENCODER = 0;
	public static final int RIGHT_DRIVE_B_ENCODER = 1;

	public static final int GATHERER = 4;
	public static final int GATHER_ARM = 3;

	public static final int SHOOTER_WHEEL = 7;
	public static final int SHOOTER_ARM = 8;

	public static final int CLIMBER_ARM_MOTOR = 7;

	// outputs
	public static final int LEFT_JOYSTICK = 0;
	public static final int RIGHT_JOYSTICK = 1;

	public static final int GAME_PAD = 2;
	public static final int DRIVE_BOARD = 3;

	public static final int MAN_AUTO_SW = 12;

	// drivetrain calibrations
	public static final double[] COMPASS_ANGLE = {15, 15, 30, 40, 40};
	public static final double[] POWER_AXIS = {0.0, 0.2, 0.6, 0.9, 1.0};

	public static double lookup(double[] values, double[] axis, double input) {
		int index = axis.length -1;
		for (int i = 0; i < axis.length; i++) {
			if (axis[i] >= input) {
				index = i;
				break;
			}
		}

		if (index <= 0)
			return values[index];
		else {
			double slope = (values[index] - values[index - 1]) / (axis[index] - axis[index-1]);
			return slope * (input - axis[index]) + values[index];
		}
	}
}
