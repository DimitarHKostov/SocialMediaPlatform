package bg.sofia.uni.fmi.mjt.socialmedia.tracker;

import bg.sofia.uni.fmi.mjt.socialmedia.tracker.log.ActivityLog;
import bg.sofia.uni.fmi.mjt.socialmedia.tracker.log.Log;
import bg.sofia.uni.fmi.mjt.socialmedia.tracker.log.activities.Activity;
import bg.sofia.uni.fmi.mjt.socialmedia.user.User;

import java.util.HashMap;
import java.util.Map;

public class ActivityTracker implements Tracker {
    private final Map<User, Log> tracker;

    public ActivityTracker() {
        this.tracker = new HashMap<>();
    }

    public void addActivity(User user, Activity newActivity) {
        if (!this.tracker.containsKey(user)) {
            this.tracker.put(user, new ActivityLog());
        }

        this.tracker.get(user).getLog().add(newActivity);
    }

    public Log getTracksOfUser(User user) {
        return this.tracker.get(user);
    }
}