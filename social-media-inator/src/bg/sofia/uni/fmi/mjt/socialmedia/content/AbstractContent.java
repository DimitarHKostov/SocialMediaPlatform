package bg.sofia.uni.fmi.mjt.socialmedia.content;

import bg.sofia.uni.fmi.mjt.socialmedia.enums.ContentType;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractContent implements Content {
    private final LocalDateTime publishDate;
    private int numberOfLikes;
    private int numberOfComments;
    private Set<String> tags;
    private Set<String> mentions;
    protected String id;
    protected ContentType type;

    public static int sequenceNumber = 0;

    public AbstractContent(LocalDateTime publishDate) {
        this.numberOfLikes = 0;
        this.numberOfComments = 0;
        this.tags = new LinkedHashSet<>();
        this.mentions = new LinkedHashSet<>();
        this.publishDate = publishDate;
    }

    private void setId(String username) {
        this.id = username + "-" + this.contentQuantity();
    }

    private int contentQuantity() {
        sequenceNumber++;
        return sequenceNumber - 1;
    }

    public void attachData(String username, Set<String> tags, Set<String> mentions) {
        this.setId(username);
        this.tags = tags;
        this.mentions = mentions;
    }

    public LocalDateTime getPublishDate() {
        return this.publishDate;
    }

    public void addAnotherLike() {
        this.numberOfLikes++;
    }

    public void addAnotherComment() {
        this.numberOfComments++;
    }

    public ContentType getContentType() {
        return this.type;
    }

    @Override
    public int getNumberOfLikes() {
        return this.numberOfLikes;
    }

    @Override
    public int getNumberOfComments() {
        return this.numberOfComments;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Collection<String> getTags() {
        return this.tags;
    }

    @Override
    public Collection<String> getMentions() {
        return this.mentions;
    }
}
