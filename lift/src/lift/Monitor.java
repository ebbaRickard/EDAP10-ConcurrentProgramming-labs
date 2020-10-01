package lift;

public class Monitor {

	private int floor;
	private boolean moving;
	private int passMove;
	private int direction;
	private int[] waitEntry = new int[7];
	private int[] waitExit = new int[7];
	private int load;
	private int hasNextFloor;
	private boolean doorsOpen;

	public Monitor() {
		direction = 1;
		load = 0;
		floor = 0;
		moving = false;
		passMove = 0;
		hasNextFloor = 0;
		doorsOpen = true;
	}

	public synchronized void addEntry(int fromFloor) {
		waitEntry[fromFloor] += 1;
		hasNextFloor++;
		notifyAll();
	}

	public synchronized void passTryEnter(int fromFloor, int toFloor) throws InterruptedException {
		while (fromFloor != floor || moving || load > 3) {
			wait();
		}
		passMove++;
		load++;
	}

	public synchronized void passEnter(int fromFloor, int toFloor) {
		waitExit[toFloor] += 1;
		waitEntry[fromFloor] -= 1;
		passMove--;
		notifyAll();

	}

	public synchronized void passTryExit(int toFloor) throws InterruptedException {
		while (toFloor != floor || moving) {
			wait();
		}
		passMove++;
		load--;
	}

	public synchronized void passExit(int toFloor) {
		waitExit[toFloor] -= 1;
		passMove--;
		hasNextFloor--;
		notifyAll();
	}

	public synchronized void tryMoveLift(LiftView view) throws InterruptedException {
		while ((load < 4 && waitEntry[floor] != 0) || passMove != 0 || waitExit[floor] != 0 || hasNextFloor == 0) {
			wait();
		}
		if (doorsOpen) {
			view.closeDoors();
			doorsOpen = false;
		}
		if (passInDirection()) {
			direction = -direction;
		}
		moving = true;
	}

	public synchronized void moveLift(LiftView view) throws InterruptedException {
		floor += direction();

		if ((waitEntry[floor] != 0 && load < 4) || waitExit[floor] != 0) {
			view.openDoors(floor);
			doorsOpen = true;
		}
		moving = false;
		notifyAll();
	}

	public synchronized int direction() {
		if (floor == 6) {
			this.direction = -1;
		} else if (floor == 0) {
			this.direction = 1;
		}
		return direction;
	}

	public synchronized boolean passInDirection() {
		if (direction == 1 && floor != 6) {
			for (int i = floor + 1; i < 7; i++) {
				if (load > 3) {
					if (waitExit[i] != 0) {
						return false;
					}
				}
				if ((waitEntry[i] != 0 && load < 4) || waitExit[i] != 0) {
					return false;
				}
				if (i == 6) {
					return true;
				}
			}
			return true;
		} else if (direction == -1 && floor != 0) {
			for (int k = floor - 1; k >= 0; k--) {
				if (load > 3) {
					if (waitExit[k] != 0) {
						return false;
					}
				}
				if ((waitEntry[k] != 0 && load < 4) || waitExit[k] != 0) {
					return false;
				}
				if (k == 0) {
					return true;
				}
			}
			return true;
		}
		return false;

	}

	public synchronized int thisFloor() {
		return floor;
	}

}
