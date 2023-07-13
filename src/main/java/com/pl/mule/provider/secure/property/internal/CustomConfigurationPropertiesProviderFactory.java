/*
 * (c) 2003-2018 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.pl.mule.provider.secure.property.internal;

import static com.pl.mule.provider.secure.property.internal.PLSecurePropertiesProviderExtension.AWS_PL_PROPERTIES_PROVIDER;

import java.io.StringReader;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.config.api.dsl.model.ConfigurationParameters;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

/**
 * Builds the provider for a custom-properties-provider:config element.
 *
 * @since 1.0
 */
public class CustomConfigurationPropertiesProviderFactory implements ConfigurationPropertiesProviderFactory {

	public static final String EXTENSION_NAMESPACE = "pl-secure-property-provider";
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomConfigurationPropertiesProviderFactory.class);

	// Add all the various secret vault implementations here
	// AWS
	private final static String CUSTOM_AWS_SECRET_MANAGER_PROPERTIES_PREFIX = "pl-sp-aws-getSecretValue::";
	// Cyberark
	private final static String CUSTOM_CYBERARK_SECRET_MANAGER_PROPERTIES_PREFIX = "pl-sp-cyberark-getSecretValue::";

	// Conjure
	private final static String CUSTOM_CONJURE_SECRET_MANAGER_PROPERTIES_PREFIX = "pl-sp-conjure-getSecretValue::";

	@Override
	public ComponentIdentifier getSupportedComponentIdentifier() {
		return AWS_PL_PROPERTIES_PROVIDER;
	}

	private AwsCredentialsProvider getSDKCredProvider(String credType) {
		return ProfileCredentialsProvider.create();
	}

	@Override
	public ConfigurationPropertiesProvider createProvider(ConfigurationParameters parameters,
			ResourceProvider externalResourceProvider) {

		// Use authtype if needed to manage code for multuple secret vaults
		String authType = parameters.getStringParameter("authType");

		return new ConfigurationPropertiesProvider() {

			@Override
			public Optional<ConfigurationProperty> getConfigurationProperty(String configurationAttributeKey) {
				if (configurationAttributeKey.startsWith(CUSTOM_AWS_SECRET_MANAGER_PROPERTIES_PREFIX)) {
					String effectiveKey = configurationAttributeKey
							.substring(CUSTOM_AWS_SECRET_MANAGER_PROPERTIES_PREFIX.length());
					String[] secretParts = effectiveKey.split(":");
					String propertyValue = getAWSSecretValue(secretParts[0], secretParts[1], secretParts[2], authType);
					LOGGER.info("AWS propertyValue =" + propertyValue);
					if (effectiveKey != null) {
						return Optional.of(new ConfigurationProperty() {

							@Override
							public Object getSource() {
								return "custom provider source";
							}

							@Override
							public Object getRawValue() {
								return propertyValue;
							}

							@Override
							public String getKey() {
								return effectiveKey;
							}
						});
					}
				} else if (configurationAttributeKey.startsWith(CUSTOM_CYBERARK_SECRET_MANAGER_PROPERTIES_PREFIX)) {
					String effectiveKey = configurationAttributeKey
							.substring(CUSTOM_CYBERARK_SECRET_MANAGER_PROPERTIES_PREFIX.length());
					// Call Cyberark Connection to retrieve secret
					String propertyValue = getCyberArkSecretValue(effectiveKey);
					LOGGER.info("CyberArk propertyValue =" + propertyValue);
					if (effectiveKey != null) {
						return Optional.of(new ConfigurationProperty() {

							@Override
							public Object getSource() {
								return "custom provider source";
							}

							@Override
							public Object getRawValue() {
								return propertyValue;
							}

							@Override
							public String getKey() {

								return effectiveKey;
							}
						});
					}
				} else if (configurationAttributeKey.startsWith(CUSTOM_CONJURE_SECRET_MANAGER_PROPERTIES_PREFIX)) {
					String effectiveKey = configurationAttributeKey
							.substring(CUSTOM_CONJURE_SECRET_MANAGER_PROPERTIES_PREFIX.length());
					// Call Conjure Connection to retrieve secret
					String propertyValue = getConjureSecretValue(effectiveKey);
					LOGGER.info("Conjure propertyValue =" + propertyValue);
					if (effectiveKey != null) {
						return Optional.of(new ConfigurationProperty() {

							@Override
							public Object getSource() {
								return "custom provider source";
							}

							@Override
							public Object getRawValue() {
								return propertyValue;
							}

							@Override
							public String getKey() {
								return effectiveKey;
							}
						});
					}
				}
				return Optional.empty();
			}

			/***
			 * Implementaion for getting secret from AWS Secrets Manager
			 * 
			 * @param region
			 * @param secretName
			 * @param key
			 * @param authType
			 * @return
			 */
			public String getAWSSecretValue(String region, String secretName, String key, String authType) {

				// This is how you can access the configuration parameter of the
				// <custom-properties-provider:config> element.
				// String accessKeyValue = parameters.getStringParameter("accessKey");
				// String secretKeyValue = parameters.getStringParameter("accessKey");

				LOGGER.info("**********Creating AWS Secret Manager Properties Provider*********");
				Region regionObj = Region.of(region);
				// Create a Secrets Manager client
				SecretsManagerClient client = SecretsManagerClient.builder()
						.region(regionObj)
						.build();
				GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
						.secretId(secretName)
						.build();
				GetSecretValueResponse getSecretValueResponse;

				try {
					getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
				} catch (Exception e) {
					// For a list of exceptions thrown, see
					// https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
					throw e;
				}
				return getSecretValueResponse.secretString();
			}

			/***
			 * Dummy implementation for getting secret from Conjure
			 * 
			 * @param effectiveKey
			 * @return
			 */
			public String getCyberArkSecretValue(String effectiveKey) {
				String[] secretParts = effectiveKey.split(":");
				LOGGER.info("Cyberark effective keys" + secretParts);
				// TODO: Implement code to get secret for CyberArk
				return "dummy cyberark value";
			}

			/***
			 * 
			 * @param effectiveKey
			 * @return
			 */
			public String getConjureSecretValue(String effectiveKey) {
				String[] secretParts = effectiveKey.split(":");
				LOGGER.info("Conjure effective keys" + secretParts);
				// TODO: Implement code to get secret for Conjure
				return "dummy conjure value";
			}

			@Override
			public String getDescription() {
				// TODO change to a meaningful name for error reporting.
				return "PL Secure Property Provider";
			}
		};
	}

}
