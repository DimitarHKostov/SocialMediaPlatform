package bg.sofia.uni.fmi.mjt.socialmedia.tracker.log;

import bg.sofia.uni.fmi.mjt.socialmedia.tracker.log.activities.Activity;

import java.util.Collection;

public interface Log {
    Collection<Activity> getLog();
}
