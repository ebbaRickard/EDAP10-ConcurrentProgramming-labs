package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;
import wash.simulation.WashingSimulator;

public class Wash {

	public static void main(String[] args) throws InterruptedException {
		WashingSimulator sim = new WashingSimulator(Settings.SPEEDUP);

		WashingIO io = sim.startSimulation();

		TemperatureController temp = new TemperatureController(io);
		WaterController water = new WaterController(io);
		SpinController spin = new SpinController(io);

		temp.start();
		water.start();
		spin.start();
		ActorThread<WashingMessage> program = new ActorThread<WashingMessage>();

		while (true) {
			int n = io.awaitButton();
			System.out.println("user selected program " + n);

			if (n == 1) {
				program = new WashingProgram1(io, temp, water, spin);
				program.start();
			} else if (n == 2) {
				program = new WashingProgram2(io, temp, water, spin);
				program.start();
			} else if (n == 3) {
				program = new WashingProgram3(io, temp, water, spin);
				program.start();
			} else {
				program.interrupt();
				program = new WashingProgram3(io, temp, water, spin);
				program.start();
			}
		}
	}
};
