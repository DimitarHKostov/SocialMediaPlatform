package bg.sofia.uni.fmi.mjt.socialmedia;

import bg.sofia.uni.fmi.mjt.socialmedia.content.AbstractContent;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;
import bg.sofia.uni.fmi.mjt.socialmedia.content.ContentManager;
import bg.sofia.uni.fmi.mjt.socialmedia.enums.ActivityType;
import bg.sofia.uni.fmi.mjt.socialmedia.enums.ContentType;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.ContentNotFoundException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.NoUsersException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameNotFoundException;
import bg.sofia.uni.fmi.mjt.socialmedia.registrar.Registrar;
import bg.sofia.uni.fmi.mjt.socialmedia.tracker.ActivityTracker;
import bg.sofia.uni.fmi.mjt.socialmedia.tracker.Tracker;
import bg.sofia.uni.fmi.mjt.socialmedia.tracker.log.Log;
import bg.sofia.uni.fmi.mjt.socialmedia.tracker.log.activities.Activity;
import bg.sofia.uni.fmi.mjt.socialmedia.user.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class EvilSocialInator implements SocialMediaInator {
    private final Map<User, Integer> popularityTable;
    private final Registrar registrar;
    private final ContentManager contentManager;
    private final Tracker tracker;

    public EvilSocialInator() {
        this.popularityTable = new HashMap<>();
        this.registrar = new Registrar();
        this.contentManager = new ContentManager();
        this.tracker = new ActivityTracker();
    }

    @Override
    public void register(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username is null;");
        }

        if (this.registrar.isRegistered(username)) {
            throw new UsernameAlreadyExistsException("Username [" + username + "] already exists.");
        }

        User freshUser = new User(username);
        this.registrar.register(freshUser);
        this.popularityTable.put(freshUser, 0);
    }

    private Set<String> extractBasedOn(char c, String description) {
        description = description.trim();
        Set<String> set = new LinkedHashSet<>();
        int descriptionLength = description.length();

        int currentIndex = 0;
        int increment = 1;

        while (currentIndex < descriptionLength) {
            if (description.charAt(currentIndex) == c) {
                StringBuilder user = new StringBuilder();
                int tokenIndex = currentIndex;
                while (tokenIndex < descriptionLength && description.charAt(tokenIndex) != ' ') {
                    user.append(description.charAt(tokenIndex));
                    tokenIndex++;
                    increment++;
                }
                set.add(user.toString());
            }
            currentIndex += increment;
            increment = 1;
        }

        return set;
    }

    private Set<String> extractTags(String description) {
        return this.extractBasedOn('#', description);
    }

    public Set<String> extractMentions(String description) {
        Set<String> mentions = this.extractBasedOn('@', description);

        Iterator<String> it = mentions.iterator();
        while (it.hasNext()) {
            String currentMention = it.next();
            String username = currentMention.substring(1);
            if (!this.registrar.isRegistered(username)) {
                it.remove();
            }
        }

        this.updatePopularity(mentions);

        return mentions;
    }

    private void updatePopularity(Set<String> mentions) {
        for (String username : mentions) {
            username = username.substring(1);

            User mentionedUser = this.registrar.getUserByUsername(username);

            int oldNumberOfMentions = this.popularityTable.get(mentionedUser);
            this.popularityTable.replace(mentionedUser, oldNumberOfMentions + 1);
        }
    }

    private AbstractContent publish(ContentType type, User publisher,
                                    LocalDateTime publishedOn, String description) {
        Set<String> tags = this.extractTags(description);
        Set<String> mentions = this.extractMentions(description);

        return this.contentManager.produceContent(publisher, type, publishedOn, tags, mentions);
    }

    @Override
    public String publishPost(String username, LocalDateTime publishedOn, String description) {
        if (username == null) {
            throw new IllegalArgumentException("Username is null.");
        }

        if (publishedOn == null) {
            throw new IllegalArgumentException("Publish date is null.");
        }

        if (description == null) {
            throw new IllegalArgumentException("Description is null;");
        }

        if (!this.registrar.isRegistered(username)) {
            throw new UsernameNotFoundException("Username [" + username + "] not found.");
        }

        User publisher = this.registrar.getUserByUsername(username);
        AbstractContent newPost = this.publish(ContentType.POST, publisher, publishedOn, description);
        this.interact(publisher.username(), publishedOn, ActivityType.CREATE_POST, newPost.getId(), null);

        return newPost.getId();
    }

    @Override
    public String publishStory(String username, LocalDateTime publishedOn, String description) {
        if (username == null) {
            throw new IllegalArgumentException("Username is null.");
        }

        if (publishedOn == null) {
            throw new IllegalArgumentException("Publish date is null.");
        }

        if (description == null) {
            throw new IllegalArgumentException("Description is null;");
        }

        if (!this.registrar.isRegistered(username)) {
            throw new UsernameNotFoundException("Username [" + username + "] not found.");
        }

        User publisher = this.registrar.getUserByUsername(username);
        AbstractContent newStory = this.publish(ContentType.STORY, publisher, publishedOn, description);
        this.interact(username, publishedOn, ActivityType.CREATE_STORY, newStory.getId(), null);

        return newStory.getId();
    }

    private void interact(String username, LocalDateTime date, ActivityType type, String id, String text) {
        User user = this.registrar.getUserByUsername(username);
        Activity newInteraction = new Activity(date, type, id, text);
        this.tracker.addActivity(user, newInteraction);
    }

    @Override
    public void like(String username, String id) {
        if (username == null) {
            throw new IllegalArgumentException("Username is null;");
        }

        if (id == null) {
            throw new IllegalArgumentException("Id is null;");
        }

        if (!this.registrar.isRegistered(username)) {
            throw new UsernameNotFoundException("Username [" + username + "] not found.");
        }

        if (!this.contentManager.containsContentWithId(id)) {
            throw new ContentNotFoundException("Content with id: " + id + " doesnt exist.");
        }

        this.contentManager.addLikeTo(id);
        this.interact(username, LocalDateTime.now(), ActivityType.LIKE, id, null);
    }

    @Override
    public void comment(String username, String text, String id) {
        if (username == null) {
            throw new IllegalArgumentException("Username is null;");
        }

        if (id == null) {
            throw new IllegalArgumentException("Id is null;");
        }

        if (!this.registrar.isRegistered(username)) {
            throw new UsernameNotFoundException("Username [" + username + "] not found.");
        }

        if (!this.contentManager.containsContentWithId(id)) {
            throw new ContentNotFoundException("Content with id: " + id + " doesnt exist.");
        }

        this.contentManager.addCommentTo(id);
        this.interact(username, LocalDateTime.now(), ActivityType.COMMENT, id, text);
    }

    private boolean isExpired(AbstractContent content, ContentType type) {
        return switch (type) {
            case POST -> Math.abs(ChronoUnit.DAYS.between(LocalDateTime.now(), content.getPublishDate())) > 30;
            case STORY -> Math.abs(ChronoUnit.HOURS.between(LocalDateTime.now(), content.getPublishDate())) > 24;
        };
    }

    private int calculatePopularity(AbstractContent first, AbstractContent second) {
        int totalFirst = first.getNumberOfComments() + first.getNumberOfLikes();
        int totalSecond = second.getNumberOfComments() + second.getNumberOfLikes();

        return Integer.compare(totalSecond, totalFirst);
    }

    private Collection<Content> getTopNPopularContent(int n) {
        Queue<AbstractContent> queue = new PriorityQueue<>(this::calculatePopularity);
        queue.addAll(this.contentManager.getAllContents());

        Set<Content> topN = new LinkedHashSet<>();
        int contentFound = 0;

        Iterator<AbstractContent> contentIterator = queue.iterator();
        while (contentFound < n && contentIterator.hasNext()) {
            AbstractContent content = contentIterator.next();
            if (!this.isExpired(content, content.getContentType())) {
                topN.add(content);
                contentFound++;
            }
        }

        return topN;
    }

    @Override
    public Collection<Content> getNMostPopularContent(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative number of objects not allowed.");
        }

        return n > this.contentManager.size()
                ? this.getTopNPopularContent(this.contentManager.size()) : this.getTopNPopularContent(n);
    }

    private int calculateMostRecent(AbstractContent first, AbstractContent second) {
        LocalDateTime publishFirst = first.getPublishDate();
        LocalDateTime publishSecond = second.getPublishDate();

        if (publishFirst.isEqual(publishSecond)) {
            return 0;
        }

        return publishFirst.isBefore(publishSecond) ? 1 : -1;
    }

    private Collection<Content> getTopNRecentContent(String username, int n) {
        Set<Content> finalContents = new LinkedHashSet<>();
        Queue<AbstractContent> queue = new PriorityQueue<>(this::calculateMostRecent);
        queue.addAll(this.contentManager.getUserContents(username));
        int addedContents = 0;

        while (addedContents++ < n) {
            finalContents.add(queue.poll());
        }

        return finalContents;
    }

    @Override
    public Collection<Content> getNMostRecentContent(String username, int n) {
        if (username == null) {
            throw new IllegalArgumentException("Username is null.");
        }

        if (n < 0) {
            throw new IllegalArgumentException("Negative number for most recent content.");
        }

        if (!this.registrar.isRegistered(username)) {
            throw new UsernameNotFoundException("Username [" + username + "] not found.");
        }

        return n > this.contentManager.size()
                ? this.getTopNRecentContent(username, this.contentManager.size())
                : this.getTopNRecentContent(username, n);
    }

    @Override
    public String getMostPopularUser() {
        if (this.registrar.size() == 0) {
            throw new NoUsersException("No users registered.");
        }

        User mostPopular = null;
        int timesMentioned = -1;

        for (Map.Entry<User, Integer> currentUser : this.popularityTable.entrySet()) {
            if (currentUser.getValue() > timesMentioned) {
                mostPopular = currentUser.getKey();
                timesMentioned = currentUser.getValue();
            }
        }

        return mostPopular.username();
    }

    @Override
    public Collection<Content> findContentByTag(String tag) {
        Set<Content> containingTag = new HashSet<>();

        for (AbstractContent currentContent : this.contentManager.getAllContents()) {
            if (currentContent.getTags().contains(tag) && !isExpired(currentContent, currentContent.getContentType())) {
                containingTag.add(currentContent);
            }
        }

        return containingTag;
    }

    private List<String> accumulateActivityLog(Log log) {
        if (log == null) {
            return new ArrayList<>();
        }

        List<String> activities = new ArrayList<>();
        for (Activity act : log.getLog()) {
            activities.add(act.formatActivity());
        }
        return activities;
    }

    @Override
    public List<String> getActivityLog(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username is null.");
        }

        if (!this.registrar.isRegistered(username)) {
            throw new UsernameNotFoundException("Username [" + username + "] not found.");
        }

        User user = this.registrar.getUserByUsername(username);
        return this.accumulateActivityLog(this.tracker.getTracksOfUser(user));
    }
}