package org.gooru.nucleus.utils.constants;

/**
 * It contains the definition for the "Message Bus End Points" which are
 * addresses on which the consumers are listening.
 */
public final class MessagebusEndpoints {
    public static final String MBEP_AUTH = "org.gooru.nucleus.utils.message.bus.auth";
    public static final String MBEP_METRICS = "org.gooru.nucleus.utils.message.bus.metrics";
    public static final String MBEP_EMAIL = "org.gooru.nucleus.utils.message.bus.email";
    public static final String MBEP_LOGGER = "org.gooru.nucleus.utils.message.bus.logger";

    private MessagebusEndpoints() {
        throw new AssertionError();
    }
}
