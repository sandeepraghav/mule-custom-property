package com.pl.mule.provider.secure.property.internal;

import org.mule.runtime.extension.api.annotation.param.Parameter;

/**
 * This class represents an extension configuration, values set in this class are commonly used across multiple
 * operations since they represent something core from the extension.
 */
public class PLSecurePropertiesProviderConfiguration {

  @Parameter
  private String configId;
  
  
  @Parameter
  private String authType;
  
  public String getConfigId(){
    return configId;
  }
  
  public String getAuthType() {
	  return authType;
  }

}
