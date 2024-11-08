package zahro.uni.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zahro.uni.domain.AssertUtils.zonedDataTimeSameInstant;

public class ReplyCommentsAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertReplyCommentsAllPropertiesEquals(ReplyComments expected, ReplyComments actual) {
        assertReplyCommentsAutoGeneratedPropertiesEquals(expected, actual);
        assertReplyCommentsAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertReplyCommentsAllUpdatablePropertiesEquals(ReplyComments expected, ReplyComments actual) {
        assertReplyCommentsUpdatableFieldsEquals(expected, actual);
        assertReplyCommentsUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertReplyCommentsAutoGeneratedPropertiesEquals(ReplyComments expected, ReplyComments actual) {
        assertThat(expected)
            .as("Verify ReplyComments auto generated properties")
            .satisfies(e -> assertThat(e.getReplyId()).as("check replyId").isEqualTo(actual.getReplyId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertReplyCommentsUpdatableFieldsEquals(ReplyComments expected, ReplyComments actual) {
        assertThat(expected)
            .as("Verify ReplyComments relevant properties")
            .satisfies(e -> assertThat(e.getContent()).as("check content").isEqualTo(actual.getContent()))
            .satisfies(e ->
                assertThat(e.getCreatedAt())
                    .as("check createdAt")
                    .usingComparator(zonedDataTimeSameInstant)
                    .isEqualTo(actual.getCreatedAt())
            );
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertReplyCommentsUpdatableRelationshipsEquals(ReplyComments expected, ReplyComments actual) {
        assertThat(expected)
            .as("Verify ReplyComments relationships")
            .satisfies(e -> assertThat(e.getParentCommentId()).as("check parentCommentId").isEqualTo(actual.getParentCommentId()));
    }
}
