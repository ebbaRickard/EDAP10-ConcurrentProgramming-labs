package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class SpinController extends ActorThread<WashingMessage> {

	private WashingIO io;

	public SpinController(WashingIO io) {
		this.io = io;
	}

	@Override
	public void run() {
		try {

			boolean spinLeft = false;
			int command = 0;

			while (true) {
				// wait for up to a (simulated) minute for a WashingMessage
				WashingMessage m = receiveWithTimeout(60000 / Settings.SPEEDUP);

				// if m is null, it means a minute passed and no message was received
				if (m != null) {
					System.out.println("got " + m);
					command = m.getCommand();

					if (command == WashingMessage.SPIN_OFF) {
						io.setSpinMode(WashingIO.SPIN_IDLE);
					} else if (command == WashingMessage.SPIN_SLOW) {
						io.setSpinMode(WashingIO.SPIN_LEFT);
						spinLeft = true;
					} else if (command == WashingMessage.SPIN_FAST) {
						io.setSpinMode(WashingIO.SPIN_FAST);
					}

					m.getSender().send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));

				} else {
					if (command == WashingMessage.SPIN_SLOW) {
						if (spinLeft) {
							io.setSpinMode(WashingIO.SPIN_RIGHT);
						} else {
							io.setSpinMode(WashingIO.SPIN_LEFT);
						}
						spinLeft = !spinLeft;
					}
				}

			}
		} catch (InterruptedException unexpected) {
			// we don't expect this thread to be interrupted,
			// so throw an error if it happens anyway
			throw new Error(unexpected);
		}
	}
}
