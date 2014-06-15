
package mywebsocket;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class ThreadQueue extends Thread {
    
    public static final int SLEEP_TIME = 10;
    
    private LinkedList<JobToDo> _queue = new LinkedList<JobToDo>();
    private boolean _pause = false;
    
    public void addToQueue(JobToDo jobToDo) {
        this._queue.add(jobToDo);
    }
    
    public int getQueueLength() {
        return this._queue.size();
    }
    
    public void run() {
        this.setName("ThreadQueue");
        
        while (true) {            
            
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadQueue.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (this._pause) {
                this.yield();
            }
            
            if (this._queue.isEmpty()) {
                continue;
            }
            
            JobToDo jobToDo = this._queue.removeFirst();
            
            if (this._queue.isEmpty()) {
                this.pauseThread();
            }
            
            (new Thread(jobToDo)).start();
        }
    }
    
    public void pauseThread() {
        this._pause = true;
    }
    
    public void resumeThread() {
        this._pause = false;
    }
    
}
