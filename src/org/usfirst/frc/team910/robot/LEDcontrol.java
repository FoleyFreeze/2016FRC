package org.usfirst.frc.team910.robot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;

public class LEDcontrol {

	
	Solenoid light1;
	//red
	Solenoid light2;
	//green
	Solenoid light3;
	//blue 
	
	public LEDcontrol() {
	light1 = new Solenoid(0);
	light2 = new Solenoid(1);
	light3 = new Solenoid(2);
	
	}
	
	
	
	public void testmachine(Joystick lJoy) {
		
		if(lJoy.getRawButton(1)){
			
			light1.set(true);
			light2.set(false);
			light3.set(false);
		}
		if(lJoy.getRawButton(2)){
			light1.set(false);
			light2.set(true);
			light3.set(false);
		if(lJoy.getRawButton(3)){
				
			light1.set(false);
			light2.set(false);
			light3.set(true);
			}
		if(lJoy.getRawButton(4)){
			
			light1.set(true);
			light2.set(true);
			light3.set(false);
		}
		if(lJoy.getRawButton(5)){
			
			light1.set(true);
			light2.set(false);
			light3.set(true);
		}

		}

		}
			
		}
		
		
	
	

