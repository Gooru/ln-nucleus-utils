package org.gooru.nucleus.utils.bootstrap.startup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gooru.nucleus.utils.infra.ConfigRegistry;
import org.gooru.nucleus.utils.infra.MailClient;
import org.gooru.nucleus.utils.infra.RedisClient;

public class Initializers implements Iterable<Initializer> {
  private final Iterator<Initializer> internalIterator;

  public Initializers() {
    final List<Initializer> initializers = new ArrayList<>();
    initializers.add(RedisClient.instance());
    initializers.add(MailClient.instance());
    initializers.add(ConfigRegistry.instance());
    internalIterator = initializers.iterator();
  }

  @Override
  public Iterator<Initializer> iterator() {
    return new Iterator<Initializer>() {

      @Override
      public boolean hasNext() {
        return internalIterator.hasNext();
      }

      @Override
      public Initializer next() {
        return internalIterator.next();
      }

    };
  }

}
