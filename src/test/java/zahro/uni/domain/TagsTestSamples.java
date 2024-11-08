package zahro.uni.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TagsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Tags getTagsSample1() {
        return new Tags().tagId(1L).tagName("tagName1");
    }

    public static Tags getTagsSample2() {
        return new Tags().tagId(2L).tagName("tagName2");
    }

    public static Tags getTagsRandomSampleGenerator() {
        return new Tags().tagId(longCount.incrementAndGet()).tagName(UUID.randomUUID().toString());
    }
}
