package net.hypixel.nerdbot.tickets.validation;

import lombok.experimental.UtilityClass;
import net.hypixel.nerdbot.marmalade.validation.Preconditions;
import net.hypixel.nerdbot.tickets.config.TicketConfig;

/**
 * Utility class for validating ticket-related input.
 * All validation methods throw {@link IllegalArgumentException} on invalid input.
 */
@UtilityClass
public class TicketValidation {

    private static final int MIN_DESCRIPTION_LENGTH = 10;
    private static final int MAX_DESCRIPTION_LENGTH = 4_000;

    /**
     * Validates that the category ID exists in the configuration.
     *
     * @param categoryId the category ID to validate
     * @param config     the ticket configuration
     *
     * @throws IllegalArgumentException if the category is null, blank, or doesn't exist
     */
    public static void validateCategoryId(String categoryId, TicketConfig config) {
        Preconditions.notBlank(categoryId, "Category ID");

        if (config.getCategoryById(categoryId).isEmpty()) {
            throw new IllegalArgumentException("Invalid category: " + categoryId);
        }
    }

    /**
     * Validates that a user is not blacklisted from creating tickets.
     *
     * @param userId the user ID to check
     * @param config the ticket configuration
     *
     * @throws IllegalArgumentException if the user is blacklisted
     */
    public static void validateUserNotBlacklisted(String userId, TicketConfig config) {
        if (config.isUserBlacklisted(userId)) {
            throw new IllegalArgumentException(config.getBlacklistMessage());
        }
    }

    /**
     * Validates a ticket description with default length constraints.
     *
     * @param description the description to validate
     *
     * @throws IllegalArgumentException if the description is invalid
     */
    public static void validateDescription(String description) {
        validateDescription(description, MIN_DESCRIPTION_LENGTH, MAX_DESCRIPTION_LENGTH);
    }

    /**
     * Validates a ticket description with custom length constraints.
     *
     * @param description the description to validate
     * @param minLength   minimum required length
     * @param maxLength   maximum allowed length
     *
     * @throws IllegalArgumentException if the description is invalid
     */
    public static void validateDescription(String description, int minLength, int maxLength) {
        Preconditions.notBlank(description, "Description");

        String trimmed = description.trim();
        Preconditions.minLength(trimmed, minLength, "Description");
        Preconditions.maxLength(trimmed, maxLength, "Description");
    }
}
