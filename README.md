# Mule 4 Custom Properties Provider for Multiple Secrets Manager

This mule custom properties extension allows Mule applications to retrieve sensitive configuration properties such as keystore passwords, database password etc directly from Secret Vaults includ AWS Secrets Manager. The key benifit of this approach is that you don't have to put these sensitive property value in clear text in the configuration files, which is a major security issue. Some of the other benefit of this approach are:
-  You can make use of life cycle lambda functions or any other custom process to rotate the secrets in an automated way
- No need to manage encryption keys as we have to if we use secure configuration properties

The down side is ofcourse this solution requires access to secrets manager 

## How it works?
Below example illustrates the usage. 
The **getting secret ** ```"${pl-sp-aws-getSecretValue::us-west-2:mule-demo-secret:mypassword}```**  The extension code parses the variables and reads the values from above expression and use those properties to make connection to AWS Secrets manager or other vault technologies . 
Above expression is parsed into multiple properties which are used to extract secret value from AWS :
  1. pl-sp-aws-getSecretValue : This tag prefix is used to identify that this is referring to AWS Secrets Manager 
  2. us-west-2				  : This is AWS region used for connecting to AWS 
  3. mule-demo-secret		  : This is AWS Secret name 
  4. mypassword				  : This is AWS Secret key 

## Initializing the Custom Security Plugin
Below XML fragment in Mule config would initialize the custom propery extension 

```xml

	<pl-secure-property-provider:config
		 authType="aws" configId="secret-test"
		name="aws-secret2" />

```
## Accessing Secret Property Value 
Use the following format to get the secret manager specific secret key value

```xml
		<set-payload
			value="${pl-sp-aws-getSecretValue::us-west-2:mule-demo-secret:mypassword}" />
```

## Customizing the Module
Follow these steps to customize the extension package name:
1.  Import the   into your favorite IDE. 
2.  Open the  `pom.xml`  file:
    
    1.  Define the GAV (`groupId`,  `artifactId`, and  `version`) of your module.
        
    2.  Define the  `name`  of your module.

3.  Change the package name (`com.<your-package>.mule.provider`) of your code.
    
4.  Add the custom property tag namespace into Mulesoft Project XML :

```
  
        
         xmlns:pl-secure-property-provider="http://www.mulesoft.org/schema/mule/<your extension name>"
	
		 Example : 

		 xmlns:pl-secure-property-provider="http://www.mulesoft.org/schema/mule/pl-secure-property-provider" 

		 -----		
		
		 http://www.mulesoft.org/schema/mule/<your extension name> 
		 http://www.mulesoft.org/schema/mule/<your extension name>/current/<your extension name>.xsd">
		
		 Example:

		 http://www.mulesoft.org/schema/mule/pl-secure-property-provider 
		 http://www.mulesoft.org/schema/mule/pl-secure-property-provider/current/mule-pl-secure-property-provider.xsd

```


Install the module locally using  `mvn clean install`  to make the module accessible from Studio.

## Using the Custom Properties Provider in a Mule Application

To use the custom properties provider:

1.  Create an application in Studio.
    
2.  Add the dependency to you new module:
    
    1.  Open the  `pom.xml`  file.
        
    2.  Within the  `<dependencies>`  tag, add a new dependency using the GAV that you put in your module.
        
    3.  Remember to add  `<classifier>mule-plugin</classifier>`  because it is a Mule module.
        
    4.  Save your changes.      

Now, open the application XML file and in the  **Global Elements**  tab and click  **Create**. Under  **Connector Configuration**, you should see an option for selecting the configuration from your custom module, for example
![](https://github.com/pl/mule-aws-extension/blob/v1.0.0/images/globalelement.PNG)

You can now configure your new component and start using properties with the prefix defined in your module.

![](https://github.com/pl/mule-aws-extension/blob/v1.0.0/images/config.PNG)

## Debugging

1. Use the Studio log to see if custom property extension is getting loaded 
2. Run Junit test case to see if code is able to load the extension plugin 
3. Make sure all the maven dependencies are added to pom.xml
4. If there are problem loading plugin into Mulesoft Studio , make sure namespaces are added to mule project xml .

<i>Note: This code is augmented using existing code https://github.com/pl/mule-awssm-extension to demostrate how we can add support for multiple secrets managers using single custom property extension . 


## Sample Mule Project


```xml

<?xml version="1.0" encoding="UTF-8"?>

<mule
	xmlns:pl-secure-property-provider="http://www.mulesoft.org/schema/mule/pl-secure-property-provider"
	xmlns:file="http://www.mulesoft.org/schema/mule/file"
	xmlns:ee="http://www.mulesoft.org/schema/mule/ee/core"
	xmlns:http="http://www.mulesoft.org/schema/mule/http"
	xmlns="http://www.mulesoft.org/schema/mule/core"
	xmlns:doc="http://www.mulesoft.org/schema/mule/documentation"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.mulesoft.org/schema/mule/core http://www.mulesoft.org/schema/mule/core/current/mule.xsd
http://www.mulesoft.org/schema/mule/http http://www.mulesoft.org/schema/mule/http/current/mule-http.xsd
http://www.mulesoft.org/schema/mule/ee/core http://www.mulesoft.org/schema/mule/ee/core/current/mule-ee.xsd
http://www.mulesoft.org/schema/mule/file http://www.mulesoft.org/schema/mule/file/current/mule-file.xsd
http://www.mulesoft.org/schema/mule/pl-secure-property-provider 
http://www.mulesoft.org/schema/mule/pl-secure-property-provider/current/mule-pl-secure-property-provider.xsd">
	<http:listener-config name="HTTP_Listener_config"
		doc:name="HTTP Listener config"
		doc:id="a19b7458-8b93-4d85-a277-e3f4cd02d148">
		<http:listener-connection host="0.0.0.0"
			port="8081" />
	</http:listener-config>



	<pl-secure-property-provider:config
		 authType="aws" configId="secret-test"
		name="aws-secret2" />



	<file:config name="File_Config" doc:name="File Config"
		doc:id="c90df4d3-e95f-4738-8187-ab186faba55d">
		<file:connection workingDir="/Users/sraghav/Downloads" />
	</file:config>
	
	<flow name="test-custom-property-extension"
		doc:id="5b4681e9-1d2c-4514-8b6c-5969fd161a88">
		<http:listener doc:name="Listener"
			doc:id="c2438ba6-2517-4953-b1fd-c3a0cfd30c5b" path="/test"
			outputMimeType='application/flatfile; missingvalues=zeroes; structureident=DTCCREQ; schemapath=myTest.ffd; recordparsing=lenient'
			config-ref="HTTP_Listener_config" />

		<set-payload
			value="${pl-sp-aws-getSecretValue::us-west-2:mule-demo-secret:mypassword}" />
		<logger level="INFO" doc:name="Logger"
			doc:id="e5e9bc8f-b1db-446c-9af2-56206044b4bd" message="#[payload]" />
			
				<set-payload
			value="${pl-sp-cyberark-getSecretValue::us-west-2:mule-demo-secret:mypassword}" />
		<logger level="INFO" doc:name="Logger"
			doc:id="dcdf35bb-c0eb-4af0-82d9-8180cc7d5bbb" message="#[payload]" />
			
				<set-payload
			value="${pl-sp-conjure-getSecretValue::us-west-2:mule-demo-secret:mypassword}" />
		<logger level="INFO" doc:name="Logger"
			doc:id="2efeb49a-a498-4af7-a9ae-c0310f5992b5" message="#[payload]" />

	</flow>
</mule>


```
