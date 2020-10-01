import java.util.concurrent.Semaphore;

public class AlarmClock {

	private int hour, minute, second;
	private int hourA, minuteA, secondA;
	private Semaphore mutex = new Semaphore(1);
	private boolean ison = false;

	public void setTime(int hour, int minute, int second) throws InterruptedException {
		mutex.acquire();
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		mutex.release();
	}

	public int[] getTime() throws InterruptedException {
		mutex.acquire();
		int[] time = { hour, minute, second };
		mutex.release();
		return time;
	}

	public void setAlarmTime(int h, int m, int s) throws InterruptedException {
		mutex.acquire();
		hourA = h;
		minuteA = m;
		secondA = s;
		ison = true;
		mutex.release();
	}

	public boolean getAlarmStatus() throws InterruptedException {
		mutex.acquire();
		boolean temp = ison;
		mutex.release();
		return temp;
	}

	public void setAlarmStatus(boolean cond) throws InterruptedException {
		mutex.acquire();
		ison = cond;
		mutex.release();
	}

	public boolean checkAlarm() throws InterruptedException {
		mutex.acquire();
		if (hourA == hour && minuteA == minute && secondA == second && ison) {
			mutex.release();
			return true;
		}
		mutex.release();
		return false;
	}

	public int[] tick() throws InterruptedException {
		mutex.acquire();
		if (second == 59) {
			if (minute == 59) {
				if (hour == 23) {
					hour = 0;
					minute = 0;
					second = 0;
				} else {
					hour++;
					minute = 0;
					second = 0;
				}
			} else {
				minute++;
				second = 0;
			}
		} else {
			second++;
		}
		int[] time = { hour, minute, second };
		mutex.release();

		return time;
	}

}