package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {

	private WashingIO io;
	private double mu = 0.478;
	private double ml = 0.00952;
	private double temp = 0;

	public TemperatureController(WashingIO io) {
		this.io = io;
	}

	@Override
	public void run() {
		try {

			int command = 0;
			boolean tempReached = false;
			ActorThread<WashingMessage> sender = new ActorThread<WashingMessage>();

			while (true) {
				WashingMessage m = receiveWithTimeout(10);

				if (m != null) {
					command = m.getCommand();
					sender = m.getSender();

					if (command == WashingMessage.TEMP_IDLE) {
						io.heat(false);
						m.getSender().send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
					} else if (command == WashingMessage.TEMP_SET) {
						tempReached = false;
						temp = m.getValue();
						System.out.println(temp);

					}

				} else {
					if (command == WashingMessage.TEMP_SET) {
						if (io.getTemperature() >= temp - 2 + ml && !tempReached) {
							sender.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
							tempReached = true;
						}
						if (io.getTemperature() <= temp - 2 + ml) {
							io.heat(true);
						} else if (io.getTemperature() >= temp - mu) {
							io.heat(false);
						}

					}
				}
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
