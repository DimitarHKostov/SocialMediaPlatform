package bg.sofia.uni.fmi.mjt.socialmedia.content;

import bg.sofia.uni.fmi.mjt.socialmedia.enums.ContentType;

import java.time.LocalDateTime;

public abstract class ContentFactory {
    public static AbstractContent getInstance(ContentType type, LocalDateTime publishedOn) {
        if (type == ContentType.POST) {
            return new Post(publishedOn);
        }

        return new Story(publishedOn);
    }
}
