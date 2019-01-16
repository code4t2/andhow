---
title: Value Loaders
weight: 8
---

<strong><center> strong.valid.simple Application Configuration </center></strong>

During AndHow's initialization, AndHow attempts to load values for each configuration property in your application. By 
default, AndHow uses its standard list of `Loader`s to load values from various configuration sources, such as JNDI, 
properties files or system properties.  AndHow sequentially calls `load()` on each `Loader` and values are set for each 
Property on a __first win basis__, meaning that the first loader to find a non-null value for a Property sets its value. 
Values found later by other loaders are ignored.

Below is the standard list of configuration sources and loaders, in order, from first to last:

  1. Fixed values - values explicitly set in code during initiation  - `StdFixedValueLoader`
  2. `String[]` arguments from the `static void main()` method - `StdMainStringArgsLoader`
  3. System Properties read from `System.getPropertiesa()` - `StdSysPropLoader`
  4. Environmental Variables read from `System.getenv()` - `StdEnvVarLoader`
  5. JNDI context variables - `StdJndiLoader`
  6. Java properties file on the filesystem (path spec'ed as an AndHow property) - `StdPropFileOnFilesystemLoader`
  7. Java properties file on the classpath (defaults to `/andhow.properties`) - `StdPropFileOnClasspathLoader`

The default set and order of loaders will work for most applications, but its easy to change the order or insert [other 
loaders](https://github.com/eeverman/andhow/tree/master/andhow-core/src/main/java/org/yarnandtail/andhow/load) into the 
the load sequence - there is an example of that at the bottom of this page.

In general you do not directly work with the Standard loaders because they are internally created  by AndHow. Instead, 
the StdConfig API is used to configure how AndHow creates the standard loaders.

#### The Standard Loaders in detail, in Order

##### StdFixedValueLoader - Fixed values loader

The fixed value loader is used to set values directly in code.

###### Typical Use Case  

Since this loader uses fixed values in code, it is only useful for specifying configuration of your application during 
testing. Configuration values can be directly set in code so that your application can be tested in a specific 
configured state. Since this is the first loader, a property value set by this load will override configuration values 
found by any other loader.

###### Basic Behaviors  

 - Pre-trims String values: No
 - Complains about unrecognized properties: NA - This is not possible
 - Default behavior:  None - This loader is only active if values are directly set as shown below

###### Loader Details and Configuration  

One simple way to set fixed values is shown below. AndHow will discover this class implementing the `AndHowInit` 
interface during initialization to read your configuration. That looks like this:

{{< highlight java "linenos=inline" >}}
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.property.StrProp;

public class SetFixedValues implements AndHowInit {
  @Override public AndHowConfiguration getConfiguration() {
    return  StdConfig.instance()
      .addFixedValue(MY_PROP, "some value");  //MY_PROP is some visible property
  }
}
{{< / highlight >}}

Alternatively you can use `AndHow.findConfig()` at an application entry point such as the main method.

TODO: This example should be for a test, since that is the only appropriate usage for this loader.

##### StdMainStringArgsLoader - String[] arguments loader  

Reads an array of Strings containing key value pairs in the form `key=value`, and loads the value for any key that 
matches a `Property`. This is normally used to read in command line arguments.

###### Typical Use Case

An executable jar or desktop application accepts command line arguments at startup to configure the application. This 
loader loads the `String[]` args passed to the `main(String[] args)`, which Java passes from the command line. This is 
the most common usage, however, your application may retrieve an array of `String`'s from anywhere to pass to AndHow at 
startup.

###### Basic Behaviors

 - Pre-trims String values: Yes
 - Complains about unrecognized properties: No
 - Default behavior:  None - This loader is only active if command line arguments are passed in as shown below

###### Loader Details and Configuration

AndHow is not 'there' to somehow intercept command line arguments - Your application code will need to do that and pass 
those arguments to AndHow, like this:

{{< highlight java "linenos=inline" >}}
import org.yarnandtail.andhow.*;

public class MyAppClass {
  public static void main(String[] args) {
    AndHow.findConfig()
      .setCmdLineArgs(args)
      .build();
  }
}
{{< / highlight >}}

Passing values from the command line looks like this:

{{< highlight cmd >}}
java -jar MyJarName.jar full.name.of.MY_PROPERTY=someValue
{{< / highlight >}}

[Here is a complete example of using command line arguments](../usage-examples/main-startup-example).

##### StdSysPropLoader - System Properties Loader  

Reads the Java system properties and loads the value for any system property who's name matches a Property.

###### Typical Use Case  

An application might receive all or some of its configuration from Java system properties that are set when the JVM 
starts. Java system properties can be set in a startup script, which could be customized for each environment. This 
provides a relatively easy way for deployment automation or system administrators to control application configuration 
values across many servers.

###### Basic Behaviors

 - Pre-trims String values: No (Individual Properties may still trim values)
 - Complains about unrecognized properties: No
 - Default behavior: AndHow always attempts to read Java system properties and will assign property values if any of 
 the system properties match known property names.

###### Loader Details and Configuration  

This loader loads properties from `java.lang.System.getProperties()`. Over the lifecycle of the JVM, values of system 
properties can change so this loader is working from a snapshot of the system properties it finds at the time of AndHow 
initialization. Once loaded, AndHow property values never change.

For `FlgProp` properties (true/false flags), the `StdSysPropLoader` will set the Property's value to true if a matching 
environment variable is found, even if the value of the property is empty. System properties can be cleared via 
`java.lang.System.clearProperty(name)`, which is how a flag value could be unset prior to AndHow loading.

Passing system properties on command line looks like this:

{{< highlight cmd >}}
java -Dfull.name.of.MY_PROPERTY=someValue -jar MyJarName.jar
{{< / highlight >}}

##### StdEnvVarLoader - Environmental Variables read from `System.getenv()`  

Reads the operating system defined environment variables and loads the value for any environmental variable who's name 
matches a `Property`.

###### Typical Use Case

An application might receive all or some of its configuration from OS defined environmental variables that are passed 
to the JVM when the JVM starts. Environment variables can be set in the OS and augmented in a startup script, which 
could be customized for each environment.  Similar to Java system properties, this provides a relatively easy way for 
deployment automation or system administrators to control application configuration values across many servers.

###### Basic Behaviors

 - Pre-trims String values: No (Individual Properties may still trim values)
 - Complains about unrecognized properties: No
 - Default behavior:  AndHow always attempts to read environment variables and will assign property values if any of 
 them match known property names.

###### Loader Details and Configuration

This loader loads values from `System.getenv()`. Those environmental values are provided by the host environment (the 
OS) as a static snapshot to the JVM at startup as a String-to-String map. The underlying OS environment variables can 
change, however, the JVM is unaware of it. Similarly, <b>_AndHow property values never change once loaded_</b>.

For `FlgProp` properties (true/false flags), the `StdEnvVarLoader` will set the Property's value to true if a matching 
environment variable is found, even if the value of the property is empty.

The Windows OS uses ALL CAPS for environment variables, while all others OS's are case sensitive - This is the primary 
reason AndHow is case insensitive by default. Each OS has a different way to set an environmental variables. 

##### StdJndiLoader - JNDI context variables  

Attempts to look up the name of each known Property in the JNDI environment and loads the value for any that are found.

###### Typical Use Case

A web or service application runs in an application container such as [Tomcat](https://tomcat.apache.org/). Application 
containers provide a JNDI environment which can be used to configure applications running in their environment.

###### Basic Behaviors

 - Pre-trims String values: No (Individual Properties may still trim values)
 - Complains about unrecognized properties: No
 - Default behavior:  Always attempts to look up each Property in the JNDI environment.
 - Complains about missing JNDI environment:  No (by default)
 - Is case sensitive: Yes (This is one of the only loaders that is case sensitive)

###### Loader Details and Configuration

While most other loaders are case insensitive, <b>_the JNDI loader is case sensitive_</b> because the JNDI API is case 
sensitive. Also, while most other loaders consume a configuration resource (e.g. a properties file) and read all the 
names and values, the JNDI loader works the other way: It goes through the list of known Properties looks up each 
property name in the JNDI context. This because its not possible to somehow read the entire JNDI environment.

JNDI implementations vary in how they name properties, so the loader will try several common name forms, for example, 
the JNDI loader will attempt to look up the following JNDI names for a property named `org.foo.My_Prop`:

 - `java:comp/env/org/foo/My_Prop`
 - `java:comp/env/org.foo.My_Prop`
 - `java:org/foo/My_Prop`
 - `java:org.foo.My_Prop`
 - `org/foo/My_Prop`
 - `org.foo.My_Prop`

This list has two different styles of property names: dot separated 'AndHow style' and slash separated JNDI style. 
Additionally, AndHow looks for three different roots (the part that comes before the variable name): `java:comp/env/` 
is used by Tomcat and several other application servers, `java:` is used by some non-container environments and at 
least one application server (Glassfish) uses no root at all. In all, AndHow will search for each property under six 
different names (2 X 3). AndHow will throw an error at startup if it finds multiple names in the JNDI context that 
refer to the same property.

Specifying JNDI environment variables varies by environment, but here is an example of specifying some properties in a 
Tomcat `context.xml` file:

{{< highlight xml >}}
<Context>
. . .
<Environment name="org/simple/GettingStarted/COUNT_DOWN_START" value="3" type="java.lang.Integer" override="false"/>
<Environment name="org/simple/GettingStarted/LAUNCH_CMD" value="GoGoGo!" type="java.lang.String" override="false"/>
. . .
</Context>
{{< / highlight >}}

In the example above, Tomcat will automatically prepend `java:comp/env/` to the name it associates with each value. As 
the example shows, JNDI values can be typed. If AndHow finds the value to already be the type it expects (e.g. an 
Integer), great! If AndHow finds a String and needs a different type, AndHow will do the conversion. Any other type of 
conversion (e.g. from a Short to an Integer) will result in an exception.

If your JNDI environment uses a non-default different root, it can be added [using one of the built-in Properties for 
the JNDI loader](https://github.com/eeverman/andhow/blob/dc4f845769489204b335a0d4e19c959c786d1835/andhow-core/src/main/java/org/yarnandtail/andhow/load/std/StdJndiLoader.java#L224)
. Those property values would need to be loaded prior to the JNDI loader, so using system properties, for example, 
would work. Here is an example of adding the custom JNDI root `java:xyz/` as a system property on command line:

{{< highlight cmd >}}
java -Dorg.yarnandtail.andhow.load.std.StdJndiLoader.CONFIG.ADDED_JNDI_ROOTS=java:xyz/ -jar MyJarName.jar
{{< / highlight >}}

##### StdPropFileOnFilesystemLoader - Java properties file on the filesystem  

Parses and loads Properties from a Java .property file on the file system. Since file systems vary, there is no default 
filepath that AndHow attempts to load from.

###### Typical Use Case  

A service application might load minimal configuration from system properties or environmental variables, loading the 
bulk of its configuration from a properties file on the file system. The properties file is not part of the application
, so it survives redeployments. A single environmental or system property could then be used to specify the properties 
file.

###### Basic Behaviors

 - Pre-trims String values: No (Individual Properties may still trim values)
 - Complains about unrecognized properties: Yes
 - Complains if the .properties file is missing: Yes (if a file path is configured)
 - Default behavior:  None - If not explicitly configured, no default loading is attempted.

###### Loader Details and Configuration

This loader reads properties files using the `java.util.Properties`, which silently ignores duplicate property entries 
(i.e., the same key appearing multiple times). When there are duplicate property keys in a properties file, only the 
last assigned value is used. Full details on how Java parses properties files can be found in the [properties file 
specification](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html#load-java.io.Reader-).

This loader is only active if it is configured as shown below:

{{< highlight java "linenos=inline" >}}
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.property.StrProp;

public class UsePropertyFileOnFilesystem implements AndHowInit {
  public static final StrProp MY_FILEPATH = StrProp.builder()
   .desc("Path to a properties file on the file system. "
     + "If a path is configured, startup will FAIL if the file is missing.").build();

  @Override
  public AndHowConfiguration getConfiguration() {
    return  StdConfig.instance()
      .setFilesystemPropFilePath(MY_FILEPATH);
  }
}
{{< / highlight >}}

The code above adds the property `MY_FILEPATH` (the name is arbitrary) which is used to configure the 
`StdPropFileOnFilesystemLoader` with a file location. When AndHow initializes, the `StdPropFileOnFilesystemLoader` 
checks to see if a value has been loaded for `MY_FILEPATH` by any prior loader. If a value is present, the loader tries 
to load from the configured file system path. If no value is configured, this loader is skipped.

##### StdPropFileOnClasspathLoader - Java properties file on the classpath  

Parses and loads Properties from a Java `.property` file on the classpath. By default, this loader will look for a file 
named `andhow.properties` at the root of the classpath.

###### Typical Use Case  

A service application might load the majority of its configuration from system properties or environmental variables, 
however, some sane default configuration values can be bundled with the application. By default, AndHow will discover 
and load a file named `andhow.properties` at the root of the classpath via the `StdPropFileOnClasspathLoader`.

###### Basic Behaviors  

 - Pre-trims String values: No (Individual Properties may still trim values)
 - Complains about unrecognized properties: Yes
 - Complains if the .properties file is missing: No
 - Default behavior:  Attempts to read `andhow.properties` from the root of the classpath

###### Loader Details and Configuration  

This loader reads properties files using the `java.util.Properties`, which silently ignores duplicate property entries 
(i.e., the same key appearing multiple times). When there are duplicate property keys in a properties file, only the 
last assigned value is used. Full details on how Java parses properties files can be found in the properties file 
specification.

Configuring the name or classpath of the properties file can be used to enable different configuration profiles based 
on the environment. For instance, a system property could specify that `/test.properties` be used on a test server and 
`/production.properties` on a production server. An example of configuring the property file path:

{{< highlight java "linenos=inline" >}}
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.property.StrProp;

public class UsePropertyFileOnClasspath implements AndHowInit {
  public static final StrProp MY_CLASSPATH = StrProp.builder()
   .desc("Path to a properties file on the classpath. "
     + "If the file is not present, it is not considered an error.").build();

  @Override
  public AndHowConfiguration getConfiguration() {
    return  StdConfig.instance()
      .setClasspathPropFilePath(MY_CLASSPATH);
  }
}
{{< / highlight >}}

The code above adds the property `MY_CLASSPATH` (the name is arbitrary) which is used to configure the 
`StdPropFileOnClasspathLoader` with a custom property file location. When AndHow initializes, the 
`StdPropFileOnClasspathLoader` checks to see if a value has been loaded for `MY_CLASSPATH` by any prior loader. If a 
value is present, the loader tries to load from the configured classpath. If no value is configured, the default 
classpath is assumed.

##### Adding and Changing the Loader Order  

Its easy to edit or change the order of the loaders:

{{< highlight java "linenos=inline" >}}
package org.example;

import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.load.std.*;

public class ModifyStdLoaderOrder implements AndHowInit {
  @Override public AndHowConfiguration getConfiguration() {
    return StdConfig.instance()
      .setStandardLoaders(StdEnvVarLoader.class, StdJndiLoader.class);
  }
}
{{< / highlight >}}

The example class above implements the AndHowInit interface. At startup, AndHow will discover this class and use it to 
configure itself. In this case, the standard list of seven loaders has been replaced with just two. Its also possible 
to leave the standard list intact and insert loaders between the standard loaders:

{{< highlight java "linenos=inline" >}}
@Override public AndHowConfiguration getConfiguration() {
  PropFileOnClasspathLoader pfl = new PropFileOnClasspathLoader();
  pfl.setFilePath("/my.properties");
  
  return  StdConfig.instance()
    .insertLoaderBefore(StdJndiLoader.class, pfl);
}
{{< / highlight >}}

Only a single instance of each of the Standard loaders is allowed - these are the loaders with names start with 'Std' 
and implement the `StandardLoader` interface, however, it is OK to insert any number of any other loader. 