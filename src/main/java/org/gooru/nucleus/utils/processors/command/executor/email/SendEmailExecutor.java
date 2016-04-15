package org.gooru.nucleus.utils.processors.command.executor.email;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.gooru.nucleus.utils.MailTemplateParser;
import org.gooru.nucleus.utils.ServerValidatorUtility;
import org.gooru.nucleus.utils.constants.HelperConstants;
import org.gooru.nucleus.utils.constants.MessageCodeConstants;
import org.gooru.nucleus.utils.infra.ConfigRegistry;
import org.gooru.nucleus.utils.infra.MailClient;
import org.gooru.nucleus.utils.processors.command.executor.Executor;
import org.gooru.nucleus.utils.processors.command.executor.MessageResponse;
import org.gooru.nucleus.utils.processors.messageProcessor.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class SendEmailExecutor implements Executor {

    private static final Logger LOG = LoggerFactory.getLogger(SendEmailExecutor.class);

    private ResourceBundle MAIL_TEMPLATE = null;

    private final MailClient mailClient = MailClient.instance();

    private final MailTemplateParser mailTemplateParser = MailTemplateParser.instance();

    private final ConfigRegistry configRegistry = ConfigRegistry.instance();

    private SendEmailExecutor() {
        try {
            File file = new File(configRegistry.getMailTemplatePath());
            URL[] urls = { file.toURI().toURL() };
            ClassLoader loader = new URLClassLoader(urls);
            MAIL_TEMPLATE = ResourceBundle.getBundle("mail-subjects", Locale.ROOT, loader);
        } catch (MalformedURLException e) {
            LOG.error("failed to load mail subjects resource bundle");
        }
    }

    @Override
    public MessageResponse execute(MessageContext messageContext) {
        JsonObject requestBody = messageContext.requestBody();
        final String templateName = requestBody.getString(HelperConstants.MAIL_TEMPLATE_NAME);
        JsonObject contextJsonData = requestBody.getJsonObject(HelperConstants.MAIL_TEMPLATE_CONTEXT);
        Map<String, Object> contextData = null;
        String subject = null;
        String template = null;
        if (contextJsonData != null) {
            contextData = contextJsonData.getMap();
        } else {
            contextData = new HashMap<>();
        }
        contextData.put(HelperConstants.MAIL_BASE_URL, configRegistry.getMailLinkBaseUrl());
        if (templateName != null) {
            subject = MAIL_TEMPLATE.getString(templateName);
            template = mailTemplateParser.getTemplate(templateName, contextData);
        } else {
            subject = requestBody.getString(HelperConstants.MAIL_SUBJECT);
            template = requestBody.getString(HelperConstants.MAIL_TEMPLATE_CONTENT);
        }
        ServerValidatorUtility.reject(subject == null, MessageCodeConstants.UT001, 400);
        ServerValidatorUtility.reject(template == null, MessageCodeConstants.UT002, 400);
        final JsonArray toAddresses = requestBody.getJsonArray(HelperConstants.TO_ADDRESSES);
        ServerValidatorUtility.reject(toAddresses == null, MessageCodeConstants.UT003, 400);
        final JsonArray ccAddresses = requestBody.getJsonArray(HelperConstants.CC_ADDRESSES);
        final JsonArray attachments = requestBody.getJsonArray(HelperConstants.MAIL_ATTACHMENTS);
        sendMail(subject, template, attachments, toAddresses, ccAddresses);
        return new MessageResponse.Builder().setResponseBody(null).setContentTypeJson().setStatusOkay().successful()
            .build();
    }

    private void sendMail(String subject, String template, JsonArray attachments, JsonArray toAddresses,
        JsonArray ccAddresses) {
        MimeMessage message = mailClient.getMimeMessage();
        Multipart multipart = new MimeMultipart();
        BodyPart bodyPartForHtml = new MimeBodyPart();
        try {
            bodyPartForHtml.setContent(template, HelperConstants.CONTENT_TEXT_HTML_TYPE);
            multipart.addBodyPart(bodyPartForHtml);
            if (attachments != null) {
                Stream<JsonObject> stream = attachments.stream().map(attachFile -> (JsonObject) attachFile);
                stream.forEach((JsonObject attachFile) -> {
                    try {
                        URL url = new URL(attachFile.getString(HelperConstants.URL));
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        ByteArrayDataSource dataSource = null;
                        BodyPart messageBodyPart = new MimeBodyPart();
                        dataSource = new ByteArrayDataSource(url.openStream(), connection.getContentType());
                        messageBodyPart.setDataHandler(new DataHandler(dataSource));
                        messageBodyPart.setFileName(attachFile.getString(HelperConstants.FILE_NAME));
                        multipart.addBodyPart(messageBodyPart);
                    } catch (Exception e) {
                        LOG.warn("File attachment is failed {}", e);
                    }
                });
            }
            message.setContent(multipart);
            message
                .setFrom(new InternetAddress(configRegistry.getMailFromAddress(), configRegistry.getMailSenderName()));
            message.setSubject(subject);
            Address[] toAddress = new Address[toAddresses.size()];
            IntStream.range(0, toAddresses.size()).forEach(index -> {
                try {
                    toAddress[index] = new InternetAddress(toAddresses.getString(index));
                } catch (Exception e) {
                    LOG.warn("Failed to set the recipient address {}", e);
                }
            });
            message.setRecipients(Message.RecipientType.TO, toAddress);
            if (ccAddresses != null && ccAddresses.size() > 0) {
                Address[] addresses = new Address[ccAddresses.size()];
                IntStream.range(0, ccAddresses.size()).forEach(index -> {
                    try {
                        addresses[index] = new InternetAddress(ccAddresses.getString(index));
                    } catch (Exception e) {
                        LOG.warn("Failed to set the cc address {}", e);
                    }
                });

                message.setRecipients(Message.RecipientType.CC, addresses);
            }
            if (configRegistry.getMailBCCAddress() != null) {
                message.setRecipients(Message.RecipientType.BCC, configRegistry.getMailBCCAddress());
            }
            mailClient.sendMail(message);
        } catch (Exception e) {
            LOG.warn("Failed to send the mail {}", e);
            ServerValidatorUtility.throwASInternalServerError();
        }
    }

    public static Executor getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final SendEmailExecutor INSTANCE = new SendEmailExecutor();

    }

}
