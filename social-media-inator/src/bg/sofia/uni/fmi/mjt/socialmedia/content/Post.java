package bg.sofia.uni.fmi.mjt.socialmedia.content;

import bg.sofia.uni.fmi.mjt.socialmedia.enums.ContentType;

import java.time.LocalDateTime;

public class Post extends AbstractContent {
    public Post(LocalDateTime publishDate) {
        super(publishDate);
        this.type = ContentType.POST;
    }
}
