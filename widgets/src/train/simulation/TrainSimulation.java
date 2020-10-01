package train.simulation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import train.model.Route;
import train.model.Segment;
import train.view.TrainView;

public class TrainSimulation {

	public static void main(String[] args) {
		TrainView view = new TrainView();
		TrainSimulation s = new TrainSimulation();
		Monitor m = s.new Monitor();

		TrainThread t;
		for (int i = 0; i < 20; i++) {
			t = s.new TrainThread(m, view);
			t.start();
		}

	}

	public class TrainThread extends Thread {
		TrainView view;
		Monitor m;

		public TrainThread(Monitor m, TrainView view) {
			this.view = view;
			this.m = m;

		}

		public void run() {
			LinkedList<Segment> q = new LinkedList<Segment>();

			Route route = view.loadRoute();
			Segment seg;
			try {
				for (int i = 0; i < 4; i++) {
					seg = route.next();
					q.add(seg);
					m.enterSeg(seg);
					seg.enter();
				}

				while (true) {
					seg = route.next();
					m.enterSeg(seg);
					q.add(seg);
					seg.enter();
					seg = q.poll();
					seg.exit(); // Varför kan man inte ha denna i monitorn?
					m.exitSeg(seg);

				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public class Monitor {
		private Set<Segment> s;

		public Monitor() {
			this.s = new HashSet<Segment>();
		}

		public synchronized void enterSeg(Segment seg) throws InterruptedException {
			while (s.contains(seg)) {
				wait();
			}
			s.add(seg);
		}

		public synchronized void exitSeg(Segment seg) {
			s.remove(seg);
			notifyAll(); // I just denna klass fungerar det att använda bara notify?
		}
	}
}