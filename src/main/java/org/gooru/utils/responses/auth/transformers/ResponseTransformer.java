package org.gooru.utils.responses.auth.transformers;

import io.vertx.core.json.JsonObject;

import java.util.Map;

public interface ResponseTransformer {
  void transform();

  JsonObject transformedBody();

  Map<String, String> transformedHeaders();

  int transformedStatus();
}
