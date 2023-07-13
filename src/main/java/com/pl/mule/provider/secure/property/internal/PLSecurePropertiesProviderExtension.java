package com.pl.mule.provider.secure.property.internal;

import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Export;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import static org.mule.runtime.api.component.ComponentIdentifier.builder;

/**
 * This is the main class of an extension, is the entry point from which
 * configurations, connection providers, operations
 * and sources are going to be declared.
 */
@Xml(prefix = "pl-secure-property-provider")
@Extension(name = "pl-secure-property-provider")
@Configurations(PLSecurePropertiesProviderConfiguration.class)
@Export(classes = CustomConfigurationPropertiesProviderFactory.class, resources = "META-INF/services/org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory")

public class PLSecurePropertiesProviderExtension {

    public static final String EXTENSION_NAMESPACE = "pl-secure-property-provider";
    public static final ComponentIdentifier AWS_PL_PROPERTIES_PROVIDER = builder().namespace(EXTENSION_NAMESPACE)
            .name("config").build();
}
