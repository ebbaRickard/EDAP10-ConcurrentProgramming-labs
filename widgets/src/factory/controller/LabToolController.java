package factory.controller;

import factory.model.DigitalSignal;
import factory.model.WidgetKind;
import factory.simulation.Painter;
import factory.simulation.Press;
import factory.swingview.Factory;

/**
 * Implementation of the ToolController interface, to be used for the Widget
 * Factory lab.
 * 
 * @see ToolController
 */
public class LabToolController implements ToolController {
	private final DigitalSignal conveyor, press, paint;
	private final long pressingMillis, paintingMillis;
	private int b = 0;

	public LabToolController(DigitalSignal conveyor, DigitalSignal press, DigitalSignal paint, long pressingMillis,
			long paintingMillis) {
		this.conveyor = conveyor;
		this.press = press;
		this.paint = paint;
		this.pressingMillis = pressingMillis;
		this.paintingMillis = paintingMillis;
	}

	@Override
	public synchronized void onPressSensorHigh(WidgetKind widgetKind) throws InterruptedException {
		//
		// TODO: you will need to modify this method.
		//
		// Note that this method can be called concurrently with onPaintSensorHigh
		// (that is, in a separate thread).
		//
		if (widgetKind == WidgetKind.BLUE_RECTANGULAR_WIDGET) {
			off();
			press.on();
			waitOutside(pressingMillis);
			press.off();
			waitOutside(pressingMillis);
			on();
		}
	}

	@Override
	public synchronized void onPaintSensorHigh(WidgetKind widgetKind) throws InterruptedException {
		//
		// TODO: you will need to modify this method.
		//
		// Note that this method can be called concurrently with onPressSensorHigh
		// (that is, in a separate thread).
		//
		if (widgetKind == WidgetKind.ORANGE_ROUND_WIDGET) {
			off();
			paint.on();
			waitOutside(paintingMillis);
			paint.off();
			on();
		}
	}

	private void waitOutside(long millis) throws InterruptedException {
		long timeToWakeup = System.currentTimeMillis() + millis;

		while (System.currentTimeMillis() < timeToWakeup) {
			long dt = timeToWakeup - System.currentTimeMillis();
			wait(dt);
			notifyAll();
		}
	}

	private synchronized void on() {
		if (b != 1) {
			b--;
		} else {
			b--;
			conveyor.on();
		}
	}

	private synchronized void off() {
		if (b != 0) {
			b++;
		} else {
			b++;
			conveyor.off();
		}
	}

	// -----------------------------------------------------------------------

	public static void main(String[] args) {
		Factory factory = new Factory();
		ToolController toolController = new LabToolController(factory.getConveyor(), factory.getPress(),
				factory.getPaint(), Press.PRESSING_MILLIS, Painter.PAINTING_MILLIS);
		factory.startSimulation(toolController);
	}
}
