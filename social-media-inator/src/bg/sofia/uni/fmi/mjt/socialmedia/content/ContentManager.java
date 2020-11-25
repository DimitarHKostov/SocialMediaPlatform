package bg.sofia.uni.fmi.mjt.socialmedia.content;

import bg.sofia.uni.fmi.mjt.socialmedia.enums.ContentType;
import bg.sofia.uni.fmi.mjt.socialmedia.user.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ContentManager {
    private final Map<Integer, AbstractContent> contents;
    private final Map<AbstractContent, User> contentOwner;

    public ContentManager() {
        this.contents = new HashMap<>();
        this.contentOwner = new HashMap<>();
    }

    public Set<AbstractContent> getAllContents() {
        Set<AbstractContent> items = new HashSet<>();
        for (Map.Entry<Integer, AbstractContent> currentContent : this.contents.entrySet()) {
            items.add(currentContent.getValue());
        }
        return items;
    }

    public Set<AbstractContent> getUserContents(String username) {
        Set<AbstractContent> setOfContents = new HashSet<>();

        int usernameHash = this.hashingFunction(username);
        for (Map.Entry<AbstractContent, User> entry : this.contentOwner.entrySet()) {
            int currentEntryHash = this.hashingFunction(entry.getValue().username());
            if (usernameHash == currentEntryHash) {
                setOfContents.add(entry.getKey());
            }
        }

        return setOfContents;
    }

    private int hashingFunction(String str) {
        int hash = 0;

        for (int i = 0; i < str.length(); i++) {
            hash = (hash << 5) - hash + str.charAt(i);
        }

        return hash;
    }

    public int size() {
        return this.contents.size();
    }

    public boolean containsContentWithId(String id) {
        return this.contents.containsKey(this.hashingFunction(id));
    }

    public AbstractContent produceContent(User publisher, ContentType type, LocalDateTime publishedOn,
                                          Set<String> tags, Set<String> mentions) {
        if (type == ContentType.POST) {
            AbstractContent newPost = ContentFactory.getInstance(ContentType.POST, publishedOn);
            newPost.attachData(publisher.username(), tags, mentions);
            this.contents.put(this.hashingFunction(newPost.getId()), newPost);
            this.contentOwner.put(newPost, publisher);
            return newPost;
        }

        AbstractContent newStory = ContentFactory.getInstance(ContentType.STORY, publishedOn);
        newStory.attachData(publisher.username(), tags, mentions);
        this.contents.put(this.hashingFunction(newStory.getId()), newStory);
        this.contentOwner.put(newStory, publisher);
        return newStory;
    }

    public void addLikeTo(String id) {
        this.contents.get(this.hashingFunction(id)).addAnotherLike();
    }

    public void addCommentTo(String id) {
        this.contents.get(this.hashingFunction(id)).addAnotherComment();
    }
}
