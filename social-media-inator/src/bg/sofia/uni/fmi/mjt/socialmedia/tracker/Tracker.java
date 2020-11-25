package bg.sofia.uni.fmi.mjt.socialmedia.tracker;


import bg.sofia.uni.fmi.mjt.socialmedia.tracker.log.Log;
import bg.sofia.uni.fmi.mjt.socialmedia.tracker.log.activities.Activity;
import bg.sofia.uni.fmi.mjt.socialmedia.user.User;

public interface Tracker {
    void addActivity(User user, Activity newActivity);

    Log getTracksOfUser(User user);
}