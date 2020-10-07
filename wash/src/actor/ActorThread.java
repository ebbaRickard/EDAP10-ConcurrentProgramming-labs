package actor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ActorThread<M> extends Thread {
	
	private BlockingQueue<M> queue;
	
	public ActorThread() {
		queue = new ArrayBlockingQueue<M>(1000);
	}

    /** Called by another thread, to send a message to this thread. */
    public void send(M message) {
    	queue.add(message);
    }
    
    /** Returns the first message in the queue, or blocks if none available. */
    protected M receive() throws InterruptedException {
    	return queue.take();
    }
    
    /** Returns the first message in the queue, or blocks up to 'timeout'
        milliseconds if none available. Returns null if no message is obtained
        within 'timeout' milliseconds. */
    protected M receiveWithTimeout(long timeout) throws InterruptedException {
        return queue.poll(timeout, TimeUnit.MILLISECONDS);
    }
}