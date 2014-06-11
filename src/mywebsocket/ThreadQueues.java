package mywebsocket;

import java.util.LinkedList;

/**
 *
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */

public class ThreadQueues {
    
    public static final int START_NUMBER_OF_QUEUES = 4;
    private LinkedList<ThreadQueue> _threadQueues = new LinkedList<ThreadQueue>();
    private static ThreadQueues _instance;
    
    private ThreadQueues() {
        for (int i = 0; i < START_NUMBER_OF_QUEUES; i++) {
            
            ThreadQueue threadQueue = new ThreadQueue();
            threadQueue.run();
            
            this._threadQueues.add(threadQueue);
        }
    }
    
    public static ThreadQueues getInstance() {
        if (_instance == null) {
            synchronized (ThreadQueues.class) {
                ThreadQueues inst = _instance;
                if (inst == null) {
                    synchronized (ThreadQueues.class) {
                        _instance = new ThreadQueues();
                    }
                }
            }
        }
        
        return _instance; 
    }
    
    public int addJobToDo(JobToDo jobToDo) {
        int min = this._threadQueues.get(0).getQueueLength();
        int id = 0;
        
        for (int i = 1; i < this._threadQueues.size(); i++) {
            if (min > this._threadQueues.get(i).getQueueLength()) {
                min = this._threadQueues.get(i).getQueueLength();
                id = i;
            }
        }
        
        this._threadQueues.get(id).addToQueue(jobToDo);
        
        return id;
    }
    
}
