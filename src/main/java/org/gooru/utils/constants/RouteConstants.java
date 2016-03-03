package org.gooru.utils.constants;

public final class RouteConstants {

  // Helper constants
  private static final String API_VERSION = "v1";
  private static final String API_BASE_ROUTE = "/api/nucleus-utils/" + API_VERSION + '/';
  public static final String API_UTILS_AUTH_ROUTE = "/api/nucleus-utils/*";

  // Helper: Operations
  private static final String EMAILS = "emails";

  // Actual End Point Constants: Note that constant values may be duplicated but
  // we are going to have individual constant values to work with for each
  // point instead of reusing the same


  public static final String EP_NUCLEUS_UTILS_EMAIL = API_BASE_ROUTE + EMAILS;

  public static final long DEFAULT_TIMEOUT = 30000L;

  private RouteConstants() {
    throw new AssertionError();
  }
}
