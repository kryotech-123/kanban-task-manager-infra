package com.amalitech.kanbantaskmanagement.dto.response;

/**
 * Represents a single, specific error, often related to field validation.
 *
 * @param field   The specific field that caused the error, if applicable.
 * @param message The detailed error message for the specific field or issue.
 */
public record ApiError(String field, String message) {
}