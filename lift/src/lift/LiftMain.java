package lift;

public class LiftMain {

	public static void main(String[] args) {
		LiftView view = new LiftView();
		Monitor m = new Monitor();

		LiftThread lift = new LiftThread(view, m);
		PassengerThread pt;

		lift.start();
		for (int i = 0; i < 20; i++) {
			pt = new PassengerThread(view, m);
			pt.start();
		}

	}

}
