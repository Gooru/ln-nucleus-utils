package org.gooru.nucleus.utils.constants;

public final class RouteConstants {

    // Helper constants
    private static final String API_VERSION = "v1";
    private static final String API_BASE_ROUTE = "/api/nucleus-utils/" + API_VERSION + '/';
    public static final String API_UTILS_AUTH_ROUTE = "/api/nucleus-utils/*";

    // Helper: Operations
    private static final String EMAILS = "emails";
    private static final String USER_ERROR = "user-error";

    // Actual End Point Constants: Note that constant values may be duplicated
    // but
    // we are going to have individual constant values to work with for each
    // point instead of reusing the same

    public static final String EP_NUCLEUS_UTILS_EMAIL = API_BASE_ROUTE + EMAILS;
    public static final String EP_NUCLEUS_UTILS_USER_ERROR = API_BASE_ROUTE + USER_ERROR;

    public static final long DEFAULT_TIMEOUT = 30000L;

    private RouteConstants() {
        throw new AssertionError();
    }
}
