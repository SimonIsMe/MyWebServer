package repositories;

import entities.File;
import java.util.LinkedList;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class FileRepository {

    private static FileRepository _instance;

    private LinkedList<File> _users;

    private FileRepository() {
        this._users = new LinkedList<File>();
    }

    public static FileRepository getInstance() {
        if (_instance == null) {
            synchronized (FileRepository.class) {
                FileRepository inst = _instance;
                if (inst == null) {
                    synchronized (FileRepository.class) {
                        _instance = new FileRepository();
                    }
                }
            }
        }

        return _instance;
    }

    public File getUser(long fileId) {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.beginTransaction();
        File user = (File) session.get(File.class, fileId);
        tx.commit();
        return user;
    }

}
