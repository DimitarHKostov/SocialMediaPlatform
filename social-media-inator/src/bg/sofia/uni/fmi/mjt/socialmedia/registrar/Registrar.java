package bg.sofia.uni.fmi.mjt.socialmedia.registrar;

import bg.sofia.uni.fmi.mjt.socialmedia.user.User;

import java.util.HashMap;
import java.util.Map;

public class Registrar {
    private final Map<Integer, User> users;

    public Registrar() {
        this.users = new HashMap<>();
    }

    private int hashingFunction(String username) {
        int hash = 0;

        for (int i = 0; i < username.length(); i++) {
            hash = (hash << 5) - hash + username.charAt(i);
        }

        return hash;
    }

    public void register(User newUser) {
        int hashCode = this.hashingFunction(newUser.username());
        this.users.put(hashCode, newUser);
    }

    public boolean isRegistered(String username) {
        return this.users.containsKey(this.hashingFunction(username));
    }

    public User getUserByUsername(String username) {
        return this.users.get(this.hashingFunction(username));
    }

    public int size() {
        return this.users.size();
    }
}
