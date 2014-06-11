package repositories;

import mywebsocket.*;
import java.util.ArrayList;

/**
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class UserRepository {

    private static UserRepository _instance;

    private ArrayList<User> _users;

    private UserRepository() {
        this._users = new ArrayList<User>();
    }

    public static UserRepository getInstance() {
        if (_instance == null) {
            synchronized (UserRepository.class) {
                UserRepository inst = _instance;
                if (inst == null) {
                    synchronized (UserRepository.class) {
                        _instance = new UserRepository();
                    }
                }
            }
        }

        return _instance;
    }

    public User getUser(int userId) {
        if (this._users.get(userId) == null) {
            //  TODO
            //  ładowanie z DB
        }
        return this._users.get(userId);
    }

}
