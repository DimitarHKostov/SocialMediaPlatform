package bg.sofia.uni.fmi.mjt.socialmedia.content;

import bg.sofia.uni.fmi.mjt.socialmedia.enums.ContentType;

import java.time.LocalDateTime;

public class Story extends AbstractContent {
    public Story(LocalDateTime publishDate) {
        super(publishDate);
        this.type = ContentType.STORY;
    }
}
