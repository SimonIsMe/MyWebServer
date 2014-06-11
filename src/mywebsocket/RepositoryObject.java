package mywebsocket;

import java.util.LinkedList;

/**
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
abstract class RepositoryObject {
    
    private boolean _isLocked = false;
    
    public void lock() {
        this._isLocked = true;
    }
    
    public void unlock() {
        this._isLocked = false;
    }
    
    public boolean isLock() {
        return this._isLocked;
    }
    
}
