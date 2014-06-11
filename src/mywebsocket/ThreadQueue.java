
package mywebsocket;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class ThreadQueue extends Thread {
    
    public static final int SLEEP_TIME = 100;
    
    private LinkedList<JobToDo> _queue = new LinkedList<JobToDo>();
    
    public void addToQueue(JobToDo jobToDo) {
        this._queue.add(jobToDo);
    }
    
    public int getQueueLength() {
        return this._queue.size();
    }
    
    public void run() {
        while (true) {
            if (this._queue.isEmpty()) {
                continue;
            }
            
            JobToDo jobToDo = this._queue.removeFirst();
            jobToDo.run();
            
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadQueue.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
