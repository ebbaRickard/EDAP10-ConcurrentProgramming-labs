package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

/**
 * Program 3 for washing machine. This also serves as an example of how washing
 * programs can be structured.
 * 
 * This short program stops all regulation of temperature and water levels,
 * stops the barrel from spinning, and drains the machine of water.
 * 
 * It can be used after an emergency stop (program 0) or a power failure.
 */
public class WashingProgram2 extends ActorThread<WashingMessage> {

	private WashingIO io;
	private ActorThread<WashingMessage> temp;
	private ActorThread<WashingMessage> water;
	private ActorThread<WashingMessage> spin;

	public WashingProgram2(WashingIO io, ActorThread<WashingMessage> temp, ActorThread<WashingMessage> water,
			ActorThread<WashingMessage> spin) {
		this.io = io;
		this.temp = temp;
		this.water = water;
		this.spin = spin;
	}

	@Override
	public void run() {
		try {
			System.out.println("washing program 1 started");

			// Lock the hatch
			io.lock(true);

			// WATER FILL
			// Instruct the WaterController to fill the barrel half way with water
			water.send(new WashingMessage(this, WashingMessage.WATER_FILL, WashingIO.MAX_WATER_LEVEL / 2));
			WashingMessage ackw = receive();
			System.out.println("washing program 1 got " + ackw);

			// HEAT UP
			// Instruct the TemperatureController to heat the barrel to specified degrees
			temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 40));
			WashingMessage ack = receive();
			System.out.println("washing program 1 got " + ack);

			// SPIN
			// Instruct SpinController to rotate barrel slowly, back and forth
			// Expect an acknowledgment in response.

			System.out.println("setting SPIN_SLOW...");
			spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));
			WashingMessage ack1 = receive();
			System.out.println("washing program 1 got " + ack1);

			// Spin for five simulated minutes (one minute == 60000 milliseconds)

			Thread.sleep(20 * 60000 / Settings.SPEEDUP);

			// Instruct SpinController to stop spin barrel spin.
			// Expect an acknowledgment in response.

			System.out.println("setting SPIN_OFF...");
			spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
			WashingMessage ack2 = receive();
			System.out.println("washing program 1 got " + ack2);

			// Instruct TemperatureController to stop heating

			temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
			WashingMessage ack5 = receive();
			System.out.println("washing program 1 got " + ack5);

			// Instruct WaterController to empty the barrel
			water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
			WashingMessage ack6 = receive();
			System.out.println("washing program 1 got " + ack6);

			// Set WaterController idle
			water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
			WashingMessage ackk = receive();
			System.out.println("washing program 1 got " + ackk);

			// WATER FILL
			// Instruct the WaterController to fill the barrel half way with water
			water.send(new WashingMessage(this, WashingMessage.WATER_FILL, WashingIO.MAX_WATER_LEVEL / 2));
			ackw = receive();
			System.out.println("washing program 1 got " + ackw);

			// HEAT UP
			// Instruct the TemperatureController to heat the barrel to specified degrees
			temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 60));
			ack = receive();
			System.out.println("washing program 1 got " + ack);

			// SPIN
			// Instruct SpinController to rotate barrel slowly, back and forth
			// Expect an acknowledgment in response.

			System.out.println("setting SPIN_SLOW...");
			spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));
			ack1 = receive();
			System.out.println("washing program 1 got " + ack1);

			// Spin for five simulated minutes (one minute == 60000 milliseconds)

			Thread.sleep(30 * 60000 / Settings.SPEEDUP);

			// Instruct SpinController to stop spin barrel spin.
			// Expect an acknowledgment in response.

			System.out.println("setting SPIN_OFF...");
			spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
			ack2 = receive();
			System.out.println("washing program 1 got " + ack2);

			// Instruct TemperatureController to stop heating

			temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
			ack5 = receive();
			System.out.println("washing program 1 got " + ack5);

			// Instruct WaterController to empty the barrel
			water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
			ack6 = receive();
			System.out.println("washing program 1 got " + ack6);

			for (int i = 0; i < 5; i++) {

				// Set WaterController idle
				water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
				WashingMessage ackt = receive();
				System.out.println("washing program 1 got " + ackt);

				// WATER FILL
				// Instruct the WaterController to fill the barrel half way with water
				water.send(new WashingMessage(this, WashingMessage.WATER_FILL, WashingIO.MAX_WATER_LEVEL / 2));
				WashingMessage acko = receive();
				System.out.println("washing program 1 got " + acko);

				// SPIN
				// Instruct SpinController to rotate barrel slowly, back and forth
				// Expect an acknowledgment in response.

				System.out.println("setting SPIN_SLOW...");
				spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));
				WashingMessage ack10 = receive();
				System.out.println("washing program 1 got " + ack10);

				// Spin for five simulated minutes (one minute == 60000 milliseconds)

				Thread.sleep(2 * 60000 / Settings.SPEEDUP);

				// Instruct SpinController to stop spin barrel spin.
				// Expect an acknowledgment in response.

				System.out.println("setting SPIN_OFF...");
				spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
				WashingMessage ack8 = receive();
				System.out.println("washing program 1 got " + ack8);

				// Instruct WaterController to empty the barrel
				water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
				WashingMessage ack7 = receive();
				System.out.println("washing program 1 got " + ack7);
			}

			// SPIN
			// Instruct SpinController to rotate barrel fast, back and forth
			// Expect an acknowledgment in response.

			System.out.println("setting SPIN_FAST...");
			spin.send(new WashingMessage(this, WashingMessage.SPIN_FAST));
			WashingMessage ack10 = receive();
			System.out.println("washing program 1 got " + ack10);

			// Spin for five simulated minutes (one minute == 60000 milliseconds)

			Thread.sleep(5 * 60000 / Settings.SPEEDUP);

			// Instruct SpinController to stop spin barrel spin.
			// Expect an acknowledgment in response.

			System.out.println("setting SPIN_OFF...");
			spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
			WashingMessage ack8 = receive();
			System.out.println("washing program 1 got " + ack8);

			// Instruct WaterController to close drain
			// Set WaterController idle
			water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
			WashingMessage ackb = receive();
			System.out.println("washing program 1 got " + ackb);

			// Now that the barrel has stopped, it is safe to open the hatch.

			io.lock(false);

		} catch (InterruptedException e) {

			// If we end up here, it means the program was interrupt()'ed:
			// set all controllers to idle

			temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
			water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
			spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
			System.out.println("washing program terminated");
		}
	}

}
