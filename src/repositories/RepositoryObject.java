package repositories;

import mywebsocket.*;

/**
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public abstract class RepositoryObject {
    
    protected boolean _isLocked = false;
    
    public boolean isExist() {
        //  TODO
        return true;
    }
    
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
