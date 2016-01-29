package org.usfirst.frc.team910.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Ultrasonic;

public class Gatherer {
	boolean gatherdistance;
	boolean release;
	int gatherer;
	Encoder garrettcoder;
	Ultrasonic.Unit gatherersonic;{
	if (gatherdistance) {
		gatherer = 1;
	}
	else if (release){
		gatherer = -1;
	}
	else {
		gatherer = 0;
		
	} {
	}
	}
	
}
