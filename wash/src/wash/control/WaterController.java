package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {

	private WashingIO io;
	private double level;

	public WaterController(WashingIO io) {
		this.io = io;
	}

	@Override
	public void run() {
		int command = 0;
		boolean filled = false;
		boolean drained = true;
		ActorThread<WashingMessage> sender = new ActorThread<WashingMessage>();

		try {

			while (true) {

				WashingMessage m = receiveWithTimeout(10);

				if (m != null) {
					System.out.println("WaterController got " + m);
					command = m.getCommand();
					sender = m.getSender();

					if (command == WashingMessage.WATER_IDLE) {
						io.drain(false);
						io.fill(false);
						sender.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
					} else if (command == WashingMessage.WATER_FILL) {
						level = m.getValue();
						io.fill(true);
						filled = false;
					} else if (command == WashingMessage.WATER_DRAIN) {
						io.drain(true);
						level = 0;
						drained = false;
					}
				} else {
					if (command == WashingMessage.WATER_FILL) {

						if (io.getWaterLevel() > level - 0.1 && !filled) {
							io.fill(false);
							filled = true;
							sender.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
						}

					} else if (command == WashingMessage.WATER_DRAIN) {
						if (io.getWaterLevel() == 0 && !drained) {
							drained = true;
							sender.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
						}
					}
				}

			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
