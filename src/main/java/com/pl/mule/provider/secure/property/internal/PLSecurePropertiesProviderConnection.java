package com.pl.mule.provider.secure.property.internal;


/**
 * This class represents an extension connection just as example (there is no real connection with anything here c:).
 */
public final class PLSecurePropertiesProviderConnection {

  private final String id;

  public PLSecurePropertiesProviderConnection(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void invalidate() {
    // do something to invalidate this connection!
  }
}
