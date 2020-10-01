package lift;

public class LiftThread extends Thread {
	private LiftView view;
	private Monitor m;

	public LiftThread(LiftView view, Monitor m) {
		this.view = view;
		this.m = m;
	}

	public void run() {
		try {
			view.openDoors(m.thisFloor());
			while (true) {
				m.tryMoveLift(view);
				view.moveLift(m.thisFloor(), m.thisFloor() + m.direction());
				m.moveLift(view);

			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
