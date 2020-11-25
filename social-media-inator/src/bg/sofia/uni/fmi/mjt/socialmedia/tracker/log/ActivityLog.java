package bg.sofia.uni.fmi.mjt.socialmedia.tracker.log;


import bg.sofia.uni.fmi.mjt.socialmedia.tracker.log.activities.Activity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Queue;

public class ActivityLog implements Log {
    private Queue<Activity> actions;

    public ActivityLog() {
        this.actions = new PriorityQueue<>(this::calculatePriority);
    }

    private int calculatePriority(Activity first, Activity second) {
        LocalDateTime publishFirst = first.getPublishDate();
        LocalDateTime publishSecond = second.getPublishDate();

        if (publishFirst.isEqual(publishSecond)) {
            return 0;
        }

        return publishFirst.isBefore(publishSecond) ? 1 : -1;
    }

    @Override
    public Collection<Activity> getLog() {
        return this.actions;
    }
}
