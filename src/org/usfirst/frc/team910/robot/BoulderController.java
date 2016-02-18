package org.usfirst.frc.team910.robot;

//import edu.wpi.first.wpilibj.CANTalon;

	
public class BoulderController {
	
	double SHOOT_LAYUP_POS = 0;
	double GATHER_LAYUP_POS = 0;
	double SHOOT_STOW_POS = 0;
	double GATHER_STOW_POS = 0;
	double SHOOT_FARSHOT_POS = 0;
	double GATHER_FARSHOT_POS = 0;
	Shooter shooter;
	Gatherer gatherer;

	public BoulderController() {
		shooter = new Shooter();
		gatherer = new Gatherer();
	}
	int button;

	public void runBC(boolean layupBtn, boolean stowBtn, boolean farShotBtn, boolean gatherBtn, boolean primeBtn, boolean fireBtn, double button){		
	
		if (button == 0){ 
			//set positions to layup on gatherer and shooter arms//
			layup();
		}
		
		else if (button == 1){
			//set positions to stow on gatherer and shooter arms//
			stow();
		}
		
		else if (button == 2){
			//set positions to farshot on gatherer and shooter arms//
			farShot();
		}
	
		else if (button == 3){
			//set positions to gather on gatherer and shooter arms//
			gather();
		}
		
		else if (button == 4){
			//set positions to prime on gatherer and shooter arms//
			prime();
		}
		
		else if (button == 5){
			//set positions to fire on gatherer and shooter arms//
			fire();
		}
		
		
	}	

	public void layup() {
		if (gatherer.inTheWay()) {
			gatherer.gotoPosition(GATHER_LAYUP_POS);
		} else {
			gatherer.gotoPosition(GATHER_LAYUP_POS);
			shooter.gotoPosition(SHOOT_LAYUP_POS);
		}
		shooter.gotoPosition(SHOOT_LAYUP_POS);
		gatherer.gotoPosition(GATHER_LAYUP_POS);

	}

	public void stow(){
		//make sure shooter is not in the way of the gatherer
				//boolean if in the way 
						//if in way move shooter arm 
						// if not move both to stow positions
		//gather retreats upwards to fit inside the bumper
		// shooter arm rotates to fit inside the bumper
	
	  if (shooter.inTheWay()){
		  shooter.gotoPosition(SHOOT_STOW_POS);
		
	  } else {
		  shooter.gotoPosition(SHOOT_STOW_POS);
		  gatherer.gotoPosition(GATHER_STOW_POS);
	  }
	
	
	}

	public void farShot() {
		if (gatherer.inTheWay()) {
			gatherer.gotoPosition(GATHER_FARSHOT_POS);
		} else {
			gatherer.gotoPosition(GATHER_FARSHOT_POS);
			shooter.gotoPosition(SHOOT_FARSHOT_POS);
		}
		shooter.gotoPosition(SHOOT_FARSHOT_POS);
		gatherer.gotoPosition(GATHER_FARSHOT_POS);
	}

	public void gather() {

	}

	public void prime() {

	}

	public void fire() {

	}

	public void scoreLow() {

	}

	public void lowBar() {

	}

}
