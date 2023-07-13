package com.pl.mule.provider.secure.property;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import javax.inject.Inject;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.junit.Test;

public class PLSecurePropertiesProviderOperationsTestCase extends MuleArtifactFunctionalTestCase {

  /**
   * Specifies the mule config xml with the flows that are going to be executed in
   * the tests, this file lives in the test resources.
   */
  @Override
  protected String getConfigFile() {
    return "pl-mule-cyberark-secure-property-config.xml";
  }

  @Inject
  private PLCyberArkTestObject testObject;

  /***
   * Test the dummy cyberark property
   */
  @Test
  public void customPropertyProviderCyberarkSecretsManagerTest() {
    assertThat(testObject.getValueFromProperty(), is("dummy cyberark value"));
  }

}
