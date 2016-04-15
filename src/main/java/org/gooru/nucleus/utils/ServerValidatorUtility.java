package org.gooru.nucleus.utils;

import io.vertx.core.json.JsonObject;

import java.util.ResourceBundle;

import org.gooru.nucleus.utils.constants.HttpConstants;
import org.gooru.nucleus.utils.processors.exceptions.AccessDeniedException;
import org.gooru.nucleus.utils.processors.exceptions.BadRequestException;
import org.gooru.nucleus.utils.processors.exceptions.NotFoundException;
import org.gooru.nucleus.utils.processors.exceptions.UnauthorizedException;

public final class ServerValidatorUtility {

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages");

    private ServerValidatorUtility() {
        throw new AssertionError();
    }

    public static void addValidatorIfNullError(final JsonObject errors, final String fieldName, final Object data,
        final String code, final String... placeHolderReplacer) {
        if (data == null) {
            addError(errors, fieldName, code, placeHolderReplacer);
        }
    }

    public static void addValidatorIfNullOrEmptyError(final JsonObject errors, final String fieldName,
        final String data, final String code, final String... placeHolderReplacer) {
        if (data == null || data.trim().length() == 0) {
            addError(errors, fieldName, code, placeHolderReplacer);
        }
    }

    public static void addValidator(final JsonObject errors, final Boolean data, final String fieldName,
        final String code, final String... placeHolderReplacer) {
        if (data) {
            addError(errors, fieldName, code, placeHolderReplacer);
        }
    }

    public static void rejectIfNull(final Object data, final String code, final int errorCode,
        final String... placeHolderReplacer) {
        if (data == null) {
            exceptionHandler(errorCode, code, placeHolderReplacer);
        }
    }

    public static void rejectIfNullOrEmpty(final String data, final String code, final int errorCode,
        final String... placeHolderReplacer) {
        if (data == null || data.trim().length() == 0) {
            exceptionHandler(errorCode, code, placeHolderReplacer);
        }
    }

    public static void reject(final Boolean data, final String code, final int errorCode,
        final String... placeHolderReplacer) {
        if (data) {
            exceptionHandler(errorCode, code, placeHolderReplacer);
        }
    }

    private static void exceptionHandler(final int errorCode, final String code, final String... placeHolderReplacer) {
        if (errorCode == HttpConstants.HttpStatus.NOT_FOUND.getCode()) {
            throw new NotFoundException(generateErrorMessage(code, placeHolderReplacer));
        } else if (errorCode == HttpConstants.HttpStatus.FORBIDDEN.getCode()) {
            throw new AccessDeniedException(generateErrorMessage(code, placeHolderReplacer));
        } else if (errorCode == HttpConstants.HttpStatus.UNAUTHORIZED.getCode()) {
            throw new UnauthorizedException(generateErrorMessage(code, placeHolderReplacer));
        } else if (errorCode == HttpConstants.HttpStatus.BAD_REQUEST.getCode()) {
            throw new BadRequestException(generateErrorMessage(code, placeHolderReplacer));
        }
    }

    public static String generateMessage(final String messageCode) {
        return MESSAGES.getString(messageCode);
    }

    public static String generateErrorMessage(final String errorCode, final String... params) {
        String errorMsg = MESSAGES.getString(errorCode);
        if (params != null) {
            for (int index = 0; index < params.length; index++) {
                errorMsg = errorMsg.replace("{" + index + '}', params[index]);
            }
        }
        return errorMsg;
    }

    public static void rejectError(final JsonObject errors, final int errorCode) {
        if (errors != null && !errors.isEmpty()) {
            if (errorCode == HttpConstants.HttpStatus.BAD_REQUEST.getCode()) {
                throw new BadRequestException(errors.toString());
            }
        }
    }

    public static void throwASInternalServerError() {
        throw new RuntimeException("internal api error");
    }

    public static void throwASInternalServerError(String message) {
        throw new RuntimeException(message);
    }

    public static void addError(JsonObject errors, String fieldName, String code, String... placeHolderReplacer) {
        errors.put(fieldName, generateErrorMessage(code, placeHolderReplacer));
    }

}
