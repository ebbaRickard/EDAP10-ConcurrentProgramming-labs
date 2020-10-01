package lift;

import java.util.Random;

public class PassengerThread extends Thread {

	private Passenger pass;
	private LiftView view;
	private Monitor m;
	private Random rand = new Random();

	public PassengerThread(LiftView view, Monitor m) {
		this.m = m;
		this.view = view;
	}

	public void run() {
		try {
			while (true) {
				pass = view.createPassenger();
				Thread.sleep(rand.nextInt(10000));

				pass.begin();

				m.addEntry(pass.getStartFloor());

				m.passTryEnter(pass.getStartFloor(), pass.getDestinationFloor());
				pass.enterLift();
				m.passEnter(pass.getStartFloor(), pass.getDestinationFloor());

				m.passTryExit(pass.getDestinationFloor());
				pass.exitLift();
				m.passExit(pass.getDestinationFloor());
				pass.end();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
