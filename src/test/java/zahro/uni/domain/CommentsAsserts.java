package zahro.uni.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static zahro.uni.domain.AssertUtils.zonedDataTimeSameInstant;

public class CommentsAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCommentsAllPropertiesEquals(Comments expected, Comments actual) {
        assertCommentsAutoGeneratedPropertiesEquals(expected, actual);
        assertCommentsAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCommentsAllUpdatablePropertiesEquals(Comments expected, Comments actual) {
        assertCommentsUpdatableFieldsEquals(expected, actual);
        assertCommentsUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCommentsAutoGeneratedPropertiesEquals(Comments expected, Comments actual) {
        assertThat(expected)
            .as("Verify Comments auto generated properties")
            .satisfies(e -> assertThat(e.getCommentId()).as("check commentId").isEqualTo(actual.getCommentId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCommentsUpdatableFieldsEquals(Comments expected, Comments actual) {
        assertThat(expected)
            .as("Verify Comments relevant properties")
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
    public static void assertCommentsUpdatableRelationshipsEquals(Comments expected, Comments actual) {
        assertThat(expected)
            .as("Verify Comments relationships")
            .satisfies(e -> assertThat(e.getPosts()).as("check posts").isEqualTo(actual.getPosts()));
    }
}