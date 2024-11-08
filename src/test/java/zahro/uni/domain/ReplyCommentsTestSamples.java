package zahro.uni.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ReplyCommentsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ReplyComments getReplyCommentsSample1() {
        return new ReplyComments().replyId(1L).content("content1");
    }

    public static ReplyComments getReplyCommentsSample2() {
        return new ReplyComments().replyId(2L).content("content2");
    }

    public static ReplyComments getReplyCommentsRandomSampleGenerator() {
        return new ReplyComments().replyId(longCount.incrementAndGet()).content(UUID.randomUUID().toString());
    }
}
