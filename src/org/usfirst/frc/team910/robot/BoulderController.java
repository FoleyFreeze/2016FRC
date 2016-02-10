package org.usfirst.frc.team910.robot;

public class BoulderController {

	double SHOOT_LAYUP_POS = 0;
	double GATHER_LAYUP_POS = 0;

	Shooter shooter;
	Gatherer gatherer;

	public void runBC(boolean layup, boolean stow, boolean farShot, boolean gather, boolean prime, boolean fire) {

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

	public void stow() {

	}

	public void farShot() {

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
