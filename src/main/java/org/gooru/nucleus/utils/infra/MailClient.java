package org.gooru.nucleus.utils.infra;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.security.Security;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.gooru.nucleus.utils.ServerValidatorUtility;
import org.gooru.nucleus.utils.bootstrap.startup.Initializer;
import org.gooru.nucleus.utils.constants.ConfigConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MailClient implements Initializer {

    private Session mailSession;

    private static final Logger LOG = LoggerFactory.getLogger(MailClient.class);

    @Override
    public void initializeComponent(Vertx vertx, JsonObject config) {
        synchronized (Holder.INSTANCE) {
            Security.addProvider(new BouncyCastleProvider());
            final Properties properties = new Properties();
            JsonObject mailConfigProps = config.getJsonObject(ConfigConstants.MAIL_CONFIG_PROPERTIES);
            mailConfigProps.forEach(prop -> properties.put(prop.getKey(), prop.getValue()));
            JsonObject mailAuthProps = config.getJsonObject(ConfigConstants.MAIL_AUTH_PROPERTIES);
            this.mailSession = Session.getDefaultInstance(properties, new MailAuthenticator(mailAuthProps));
        }
    }

    public MimeMessage getMimeMessage() {
        return new MimeMessage(this.mailSession);
    }

    public void sendMail(Message message) {
        Transport transport;
        try {
            transport = this.mailSession.getTransport();
            transport.connect();
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            LOG.error("Failed to send mail {}", e);
            ServerValidatorUtility.throwASInternalServerError();
        }
    }

    public static MailClient instance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        static final MailClient INSTANCE = new MailClient();
    }

    private static class MailAuthenticator extends javax.mail.Authenticator {
        private final JsonObject mailAuthProps;

        public MailAuthenticator(JsonObject mailAuthProps) {
            this.mailAuthProps = mailAuthProps;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(mailAuthProps.getString(ConfigConstants.MAIL_AUTH_USERNAME),
                mailAuthProps.getString(ConfigConstants.MAIL_AUTH_PASSWORD));
        }
    }
}
