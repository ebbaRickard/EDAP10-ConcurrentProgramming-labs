import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import client.view.ProgressItem;
import client.view.StatusWindow;
import client.view.WorklistItem;
import network.Sniffer;
import network.SnifferCallback;
import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreaker implements SnifferCallback {

	private final JPanel workList;
	private final JPanel progressList;

	private final JProgressBar mainProgressBar;
	private final ExecutorService pool = Executors.newFixedThreadPool(2);

	// -----------------------------------------------------------------------

	private CodeBreaker() {
		StatusWindow w = new StatusWindow();

		workList = w.getWorkList();
		progressList = w.getProgressList();
		mainProgressBar = w.getProgressBar();
		w.enableErrorChecks();
	}

	// -----------------------------------------------------------------------

	public static void main(String[] args) {

		/*
		 * Most Swing operations (such as creating view elements) must be performed in
		 * the Swing EDT (Event Dispatch Thread).
		 * 
		 * That's what SwingUtilities.invokeLater is for.
		 */

		SwingUtilities.invokeLater(() -> {
			CodeBreaker codeBreaker = new CodeBreaker();
			new Sniffer(codeBreaker).start();
		});
	}

	// -----------------------------------------------------------------------

	/** Called by a Sniffer thread when an encrypted message is obtained. */
	@Override
	public void onMessageIntercepted(String message, BigInteger n) {
		SwingUtilities.invokeLater(() -> {
			JButton b = new JButton("Break");
			JPanel w = new WorklistItem(n, message); // Skickar med b för att lägga till den grafiska komponenten
			w.add(b);

			JButton c = new JButton("Cancel");
			ProgressItem p = new ProgressItem(n, message);
			p.add(c);

			JButton r = new JButton("Remove");

			workList.add(w);
			b.addActionListener(e -> {
				workList.remove(w);
				progressList.add(p);
				mainProgressBar.setMaximum(mainProgressBar.getMaximum() + 1000000);
				ProgressTracker tracker = new Tracker(mainProgressBar, p);

				Future<?> task = pool.submit(() -> {
					try {
						String plaintext = Factorizer.crack(message, n, tracker);
						SwingUtilities.invokeLater(() -> p.getTextArea().setText(plaintext));
						SwingUtilities.invokeLater(() -> p.remove(c));
						SwingUtilities.invokeLater(() -> p.add(r));

					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				});

				c.addActionListener(ev -> task.cancel(true));

				r.addActionListener(eve -> {
					progressList.remove(p);
					mainProgressBar.setValue(mainProgressBar.getValue() - 1000000);
					mainProgressBar.setMaximum(mainProgressBar.getMaximum() - 1000000);
				});
			});

		});

	}

	private static class Tracker implements ProgressTracker {
		private int totalProgress = 0;
		private JProgressBar progressBar;
		private JProgressBar mainProgressBar;

		private Tracker(JProgressBar mainProgressBar, ProgressItem p) {
			this.progressBar = p.getProgressBar();
			this.mainProgressBar = mainProgressBar;

		}

		@Override
		public void onProgress(int ppmDelta) {
			totalProgress += ppmDelta;
			SwingUtilities.invokeLater(() -> {
				progressBar.setValue(totalProgress);
				mainProgressBar.setValue(mainProgressBar.getValue() + ppmDelta);
			});

		}

	}

}
