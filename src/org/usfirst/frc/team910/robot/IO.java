package org.usfirst.frc.team910.robot;

public class IO {
	//running the competition bot
	public static final boolean COMP = true;
	
	// inputs
	public static final int LEFT_DRIVE_MOTOR = 1;
	public static final int RIGHT_DRIVE_MOTOR = 0;

	public static final int LEFT_DRIVE_A_ENCODER = 2;
	public static final int LEFT_DRIVE_B_ENCODER = 3;
	public static final int RIGHT_DRIVE_A_ENCODER = 0;
	public static final int RIGHT_DRIVE_B_ENCODER = 1;
	public static final int SHOOTER_BALL_SENSOR = 4;	

	public static final int GATHERER = 4;
	public static final int GATHER_ARM = 3;

	public static final int SHOOTER_WHEEL_L = 10;
	public static final int SHOOTER_WHEEL_R = 6;

	public static final int SHOOTER_ARM = 12;
	public static final int LOAD_WHEEL_L = 11;
	public static final int LOAD_WHEEL_R = 5;

	public static final int CLIMBER_ARM_MOTOR = 7;

	// outputs
	public static final int LEFT_JOYSTICK = 0;
	public static final int RIGHT_JOYSTICK = 1;

	public static final int GAME_PAD = 2;
	public static final int DRIVE_BOARD = 3;

	public static final int MAN_AUTO_SW = 1;
	
	public static final int JOG_SHOOTER_UP = 15 ;
	public static final int JOG_SHOOTER_DOWN = 14;
	public static final int LOWBAR = 6;
	public static final int PORT = 9; //now does first half low bar
	public static final int SALLYPORT = 11;
	public static final int FLIPPY_DE_LOS_FLOPPIES = 8;
	//public static final int DRAWBRIDGE = 7;
	public static final int LR_JOG_BTN = 7;
	public static final int LAYUP = 4;
	public static final int STOW = 2;
	public static final int FAR_SHOT = 3;
	public static final int GATHER = 5;
	public static final int PRIME = 13;
	public static final int FIRE = 12;
	public static final int WASD_W = 21;
	public static final int WASD_A = 20;
	public static final int WASD_S = 19;
	public static final int WASD_D = 18;
	public static final int FLIP_CONTROLS = 17;
	public static final int CLIMB_2 = 22;
	
	// l-joy
	public static final int AIM_CAMERA = 6;
	public static final int DO_A_180_BTN = 4;
	
	//l-joy auton selections
	public static final int AUTON_INC_SLOT = 12;
	public static final int AUTON_DEC_SLOT = 11;
	public static final int AUTON_INC_DEF = 10;
	public static final int AUTON_DEC_DEF = 9;
	public static final int AUTON_INC_EXTRA = 8;
	public static final int AUTON_DEC_EXTRA = 7;
	
	// r-joy
	public static final int COMPASS_POWER_THROTTLE = 2;
	public static final int ZERO_YAW = 3;
	

	// drivetrain calibrations
	public static final double[] COMPASS_ANGLE = { 15, 15, 30, 40, 40 };
	public static final double[] POWER_AXIS = { 0.0, 0.2, 0.6, 0.9, 1.0 };
	
	// camera distance cals
	public static final double POWER_ADJ = 0.9;
	public static final double[] DISTANCE_AXIS = {  70,   80,   94,    100,    110,    120,    150,    160 }; //in inches
	public static final double[] MOTOR_POWERS = {  0.63 * POWER_ADJ,  0.63 * POWER_ADJ,  0.63 * POWER_ADJ,  0.635 * POWER_ADJ,  0.645 * POWER_ADJ,  0.655 * POWER_ADJ,  0.685 * POWER_ADJ,  0.685 * POWER_ADJ };	//was .6,.6,.6,.635
	public static final double[] SHOOTER_ANGLE = { -32,  -30,  -24,    -20,      0,      0,      0,      0 };	//was -27,-26,-22,-20  3.30 MrC

	public static double lookup(double[] values, double[] axis, double input) {
		int index = axis.length - 1;
		for (int i = 0; i < axis.length; i++) {
			if (axis[i] >= input) {
				index = i;
				break;
			}
		}

		if (index <= 0)
			return values[index];
		else {
			double slope = (values[index] - values[index - 1]) / (axis[index] - axis[index - 1]);
			return slope * (input - axis[index]) + values[index];
		}
	}
}
