package com.amalitech.kanbantaskmanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A standard, generic wrapper for all API responses in the application.
 * This class ensures that clients receive a consistent JSON structure
 * for both successful and failed operations.
 *
 * @param <T> The type of the primary data payload included in a successful response.
 */
@Getter
public class ApiResponse<T> {
    @JsonProperty("success")
    private final boolean success;

    @JsonProperty("message")
    private final String message;

    @JsonProperty("data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    @JsonProperty("timestamp")
    private final String timestamp;

    /**
     * A list of specific error details, typically used for validation failures.
     * This will be null for successful responses.
     * The field is omitted from the JSON output if it is null.
     */
    @JsonProperty("errors")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<ApiError> errors;

    /**
     * Private constructor to enforce object creation through static factory methods.
     *
     * @param success   A boolean indicating if the operation was successful.
     * @param message   A summary message for the response.
     * @param data      The data payload for the response.
     * @param timestamp The server timestamp.
     * @param errors    A list of specific errors, if any.
     */
    private ApiResponse(boolean success, String message, T data, String timestamp, List<ApiError> errors) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
        this.errors = errors;
    }

    /**
     * Creates a standard success response with a data payload.
     *
     * @param data The data to be included in the response.
     * @param <T>  The type of the data payload.
     * @return A new {@link ApiResponse} instance for a successful operation.
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data,
                LocalDateTime.now().toString(), null);
    }

    /**
     * Creates a success response with a custom message and a data payload.
     *
     * @param data    The data to be included in the response.
     * @param message A custom success message.
     * @param <T>     The type of the data payload.
     * @return A new {@link ApiResponse} instance for a successful operation.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data,
                LocalDateTime.now().toString(), null);
    }

    /**
     * Creates a standard error response with a list of specific error details.
     *
     * @param message A general error message summarizing the failure.
     * @param errors  A list of specific validation or business logic errors.
     * @param <T>     The generic type for the response (data will be null).
     * @return A new {@link ApiResponse} instance for a failed operation.
     */
    public static <T> ApiResponse<T> error(String message, List<ApiError> errors) {
        return new ApiResponse<>(false, message, null,
                LocalDateTime.now().toString(), errors);
    }

    /**
     * Creates a simple error response with only a message.
     *
     * @param message A general error message summarizing the failure.
     * @param <T>     The generic type for the response (data will be null).
     * @return A new {@link ApiResponse} instance for a failed operation.
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null,
                LocalDateTime.now().toString(), null);
    }
}



