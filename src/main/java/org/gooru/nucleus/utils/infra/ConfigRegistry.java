package org.gooru.nucleus.utils.infra;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import org.gooru.nucleus.utils.bootstrap.startup.Initializer;
import org.gooru.nucleus.utils.constants.ConfigConstants;

public class ConfigRegistry implements Initializer {

    private JsonObject config;

    @Override
    public void initializeComponent(Vertx vertx, JsonObject config) {
        synchronized (Holder.INSTANCE) {
            this.config = config;
        }
    }

    public String getMailFromAddress() {
        return config.getString(ConfigConstants.MAIL_FROM_ADDRESS);
    }

    public String getMailSenderName() {
        return config.getString(ConfigConstants.MAIL_SENDER_NAME);
    }

    public String getMailBCCAddress() {
        return config.getString(ConfigConstants.MAIL_BCC_ADDRESS);
    }

    public String getMailTemplatePath() {
        return config.getString(ConfigConstants.MAIL_TEMPLATES_PATH);
    }
    
    public String getMailLinkBaseUrl() {
        return config.getString(ConfigConstants.MAIL_LINKS_BASE_URL);
    }

    public static ConfigRegistry instance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final ConfigRegistry INSTANCE = new ConfigRegistry();
    }
}
