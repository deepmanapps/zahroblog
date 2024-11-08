package zahro.uni.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PostsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Posts getPostsSample1() {
        return new Posts().postId(1L).title("title1").content("content1");
    }

    public static Posts getPostsSample2() {
        return new Posts().postId(2L).title("title2").content("content2");
    }

    public static Posts getPostsRandomSampleGenerator() {
        return new Posts().postId(longCount.incrementAndGet()).title(UUID.randomUUID().toString()).content(UUID.randomUUID().toString());
    }
}
