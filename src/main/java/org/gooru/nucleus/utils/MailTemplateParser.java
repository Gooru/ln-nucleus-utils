package org.gooru.nucleus.utils;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public final class MailTemplateParser {

  public final VelocityEngine velocityEngine;

  private static final String TEMPLATE_EXT = ".vm";

  private static final String TEMPLATE_BASE_PATH = "mail-templates/";

  private MailTemplateParser() {
    velocityEngine = new VelocityEngine();
    velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
    velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
    velocityEngine.init();
  }

  public String getTemplate(String templateName, Map<?, ?> contextData) {
    Template template = velocityEngine.getTemplate(TEMPLATE_BASE_PATH + templateName + TEMPLATE_EXT);
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
