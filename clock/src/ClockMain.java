import java.time.LocalTime;
import java.util.concurrent.Semaphore;

import clock.AlarmClockEmulator;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;

public class ClockMain {

	public static void main(String[] args) throws InterruptedException {
		AlarmClockEmulator emulator = new AlarmClockEmulator();

		AlarmClock ac = new AlarmClock();
		ClockInput in = emulator.getInput();
		ClockOutput out = emulator.getOutput();

		Thread timeUpdate = new Thread() {
			public void run() {
				long t = System.currentTimeMillis();
				int[] time;
				int i = 1;

				while (true) {
					try {
						time = ac.tick();
						out.displayTime(time[0], time[1], time[2]);
						Thread.sleep(1000 * i + t - System.currentTimeMillis());
						i++;
					} catch (InterruptedException e) {
						System.out.println("This is not supposed to happen!");
						e.printStackTrace();
					}
				}
			}
		};

		Thread userThread = new Thread() {
			public void run() {
				UserInput userInput;

				while (true) {
					try {
						in.getSemaphore().acquire();
						userInput = in.getUserInput();
						System.out.println(userInput.getChoice());
						int choice = userInput.getChoice();

						if (choice == 1) {
							ac.setTime(userInput.getHours(), userInput.getMinutes(), userInput.getSeconds());
						} else if (choice == 2) {
							out.setAlarmIndicator(true);
							ac.setAlarmTime(userInput.getHours(), userInput.getMinutes(), userInput.getSeconds());
						} else if (choice == 3) {
							out.setAlarmIndicator(!ac.getAlarmStatus());
							ac.setAlarmStatus(!ac.getAlarmStatus());
						}

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		Thread alarmThread = new Thread() {
			public void run() {
				while (true) {
					try {
						if (ac.checkAlarm()) {
							long t0 = System.currentTimeMillis();
							for (int i = 1; i < 21; i++) {
								out.alarm();
								Thread.sleep(t0 + 1000*i - System.currentTimeMillis());
								if (!ac.getAlarmStatus()) {
									break;
								}
							}
							ac.setAlarmStatus(false);
							out.setAlarmIndicator(false);
						}

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		ac.setTime(LocalTime.now().getHour(), LocalTime.now().getMinute(), LocalTime.now().getSecond());
		int[] time = ac.getTime();
		out.displayTime(time[0], time[1], time[2]);

		timeUpdate.start();
		userThread.start();
		alarmThread.start();

	}
}
