package repositories;

import mywebsocket.*;
import java.util.LinkedList;

/**
 *
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class User extends RepositoryObject {
    
    private String _firstName;
    private String _lastName;
    private String _emailAddress;
    
    private LinkedList<String> _changes = new LinkedList<String>();
    
    public User() {
    }
    
    public User setFirstName(String newFirstName) {
        this._firstName = newFirstName;
        this._changes.add("firstName");
        return this;
    }
    
    public User setLastName(String lastName) {
        this._lastName = lastName;
        this._changes.add("lastName");
        return this;
    }
    
    public User setEmailAddress(String emailAddress) {
        this._emailAddress = emailAddress;
        this._changes.add("emailAddress");
        return this;
    }
    
    public void save() {
        String returnToClients = "";
        
        for (int i = 0; i < this._changes.size(); i++) {
            switch(this._changes.get(i)) {
                case "firstName":
                    //  TODO
                    //  operations...
                    returnToClients += "...";
                    break;
                case "lastName":
                    //  TODO
                    //  operations...
                    returnToClients += "...";
                    break;
                case "emailAddress":
                    //  TODO
                    //  operations...
                    returnToClients += "...";
                    break;
            }
        }
        
        //  TODO
        //  sending requests to all clients with an information contains 
        //  modified data
    }
    
}
