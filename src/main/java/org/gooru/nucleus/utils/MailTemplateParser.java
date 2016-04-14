package org.gooru.nucleus.utils;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.gooru.nucleus.utils.infra.ConfigRegistry;

public final class MailTemplateParser {

    public final VelocityEngine velocityEngine;

    private static final String TEMPLATE_EXT = ".vm";

    private ConfigRegistry configRegistery = ConfigRegistry.instance();

    private MailTemplateParser() {
        velocityEngine = new VelocityEngine();
        Properties properties = new Properties();
        properties.setProperty("file.resource.loader.path", configRegistery.getMailTemplatePath());
        velocityEngine.init(properties);
    }

    public String getTemplate(String templateName, Map<?, ?> contextData) {
        Template template = velocityEngine.getTemplate(templateName + TEMPLATE_EXT);
        VelocityContext context = new VelocityContext(contextData);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    public static MailTemplateParser instance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final MailTemplateParser INSTANCE = new MailTemplateParser();
    }
}
