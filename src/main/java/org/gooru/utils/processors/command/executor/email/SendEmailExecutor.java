package org.gooru.utils.processors.command.executor.email;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.net.HttpURLConnection;
import java.net.URL;
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

import org.gooru.utils.MailTemplateParser;
import org.gooru.utils.constants.HelperConstants;
import org.gooru.utils.infra.ConfigRegistry;
import org.gooru.utils.infra.MailClient;
import org.gooru.utils.processors.command.executor.Executor;
import org.gooru.utils.processors.command.executor.MessageResponse;
import org.gooru.utils.processors.messageProcessor.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SendEmailExecutor implements Executor {

  private static final Logger LOG = LoggerFactory.getLogger(SendEmailExecutor.class);

  private static final ResourceBundle MAIL_TEMPLATE = ResourceBundle.getBundle("mail-templates/mail-subjects");

  private final MailClient mailClient = MailClient.instance();

  private final MailTemplateParser mailTemplateParser = MailTemplateParser.instance();

  private final ConfigRegistry configRegistry = ConfigRegistry.instance();

  private SendEmailExecutor() {
  }

  @Override
  public MessageResponse execute(MessageContext messageContext) {
    JsonObject requestBody = messageContext.requestBody();
    final String templateName = requestBody.getString(HelperConstants.MAIL_TEMPLATE_NAME);
    final String subject = MAIL_TEMPLATE.getString(templateName);
    Map<?, ?> contextData = requestBody.getJsonObject(HelperConstants.MAIL_TEMPLATE_CONTEXT).getMap();
    final String template = mailTemplateParser.getTemplate(templateName, contextData);
    final JsonArray toAddresses = requestBody.getJsonArray(HelperConstants.TO_ADDRESSES);
    final JsonArray ccAddresses = requestBody.getJsonArray(HelperConstants.CC_ADDRESSES);
    final JsonArray attachments = requestBody.getJsonArray(HelperConstants.MAIL_ATTACHMENTS);
    sendMail(subject, template, attachments, toAddresses, ccAddresses);
    return new MessageResponse.Builder().setResponseBody(null).setContentTypeJson().setStatusOkay().successful().build();
  }

  private void sendMail(String subject, String template, JsonArray attachments, JsonArray toAddresses, JsonArray ccAddress) {
    MimeMessage message = mailClient.getMimeMessage();
    Multipart multipart = new MimeMultipart();
    BodyPart bodyPartForHtml = new MimeBodyPart();
    try {
      bodyPartForHtml.setContent(template, HelperConstants.CONTENT_TEXT_HTML_TYPE);
      multipart.addBodyPart(bodyPartForHtml);
      if (attachments != null) {
        Stream<JsonObject> stream = attachments.stream().map(attachFile -> (JsonObject) attachFile);
        ((Stream<JsonObject>) stream).forEach((JsonObject attachFile) -> {
          try {
            URL url = new URL(attachFile.getString(HelperConstants.URL));
            if (url != null) {
              HttpURLConnection connection = (HttpURLConnection) url.openConnection();
              ByteArrayDataSource dataSource = null;
              BodyPart messageBodyPart = new MimeBodyPart();
              dataSource = new ByteArrayDataSource(url.openStream(), connection.getContentType());
              if (dataSource != null) {
                messageBodyPart.setDataHandler(new DataHandler(dataSource));
                messageBodyPart.setFileName(attachFile.getString(HelperConstants.FILE_NAME));
                multipart.addBodyPart(messageBodyPart);
              }
            }
          } catch (Exception e) {
            LOG.warn("File attachment is failed {}", e);
          }
        });
      }
      message.setContent(multipart);
      message.setFrom(new InternetAddress(configRegistry.getMailFromAddress(), configRegistry.getMailSenderName()));
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
      if (ccAddress != null && ccAddress.size() > 0) {
        Address[] addresses = new Address[toAddresses.size()];
        IntStream.range(0, ccAddress.size()).forEach(index -> {
          try {
            addresses[index] = new InternetAddress(toAddresses.getString(index));
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
    }

  }

  public static final Executor getInstance() {
    return Holder.INSTANCE;
  }

  private static final class Holder {
    private static final SendEmailExecutor INSTANCE = new SendEmailExecutor();

  }

}
