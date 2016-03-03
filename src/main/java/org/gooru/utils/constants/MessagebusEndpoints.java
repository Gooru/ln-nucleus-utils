package org.gooru.utils.constants;

/**
 * It contains the definition for the "Message Bus End Points" which are
 * addresses on which the consumers are listening.
 */
public final class MessagebusEndpoints {
  public static final String MBEP_AUTH = "org.gooru.utils.message.bus.auth";
  public static final String MBEP_METRICS = "org.gooru.utils.message.bus.metrics";
  public static final String MBEP_EMAIL = "org.gooru.utils.message.bus.email";

  private MessagebusEndpoints() {
    throw new AssertionError();
  }
}
