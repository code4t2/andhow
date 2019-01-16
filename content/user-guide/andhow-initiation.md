---
title: AndHow Startup / Initiation
weight: 9
---  

<strong><center> strong.valid.simple Application Configuration </center></strong>

### AndHow Initiation  
Initiation is AndHow's startup/bootstrap process where it does the following:
 - Discovers its own configuration
 - Discovers all declared AndHow Properties (even those in dependencies)
 - Loads values for those properties from various sources using the configured `Loaders`
 - Validates all property values
 
##### Implicit Initiation  

Implicit initiation happens as a side effect of reading a property value, which is what happens in the 
[GettingStarted](../../#sample-usage-example) example:
{{< highlight java >}}
  for (int i = COUNT_DOWN_START.getValue(); i >= 1; i--) { ... } 
{{< / highlight >}}

The call to Property.getValue() forces AndHow to initialize so that it can provide the value. This is the simplest 
way to use AndHow and is OK for small projects, but it is not the best practice.

##### Explicit Initiation  

Explicit initiation happens when your code directs AndHow to construct its one and only instance. We could extend the 
GettingStarted example to load property values from the `String[] args` passed to the main method by explicitly 
initiating AndHow in the main method:

{{< highlight java "linenos=inline" >}}
  public static void main(String[] args) {
      AndHow.findConfig().setCmdLineArgs(args).build();
      //...
  }
{{< / highlight >}}

The added line does the following:
 - `findConfig()` finds the configuration that AndHow will use for its initiation, which can be customized
 - `setCmdLineArgs(args)` speaks for itself...
 - `build()` causes AndHow to do its **one time only** initiation  

We will get into more detail on how to configure the initiation process and what happens in each of its steps, but 
first lets take a look at why initiation takes place only once and why that is so important.

### Attempting to reinitialize AndHow will throw a `RuntimeException`  

From the code snippets above, imagine the value for `COUNT_DOWN_START` is read, causing an implicit initiation, then 
the main method is called, which attempts an explicit initialization. If a value for `COUNT_DOWN_START` was passed to 
the main method, the value read before main() was called would be incorrect or at least unintended.

This a serious problem and AndHow protects applications from this situation by throwing a RuntimeException anytime an 
explicit initiation is attempted when AndHow is already initialized. In practice this means that applications should 
follow best practices and have a well defined entry point where AndHow and other one-time initiation takes place. 
Common entry points are:

 - A main method in single startup class for desktop application / executable jar  
 - A ServletContextListener with a contextInitialized method for a web application  

Using explicit initiation at your application's entry point ensures that configuration is deterministic and that your 
application will [fail fast](https://martinfowler.com/ieeeSoftware/failFast.pdf) if rogue code attempts to read a 
property value before you have configured AndHow's initiation.

### AndHow Initiation Steps  

Lets walk through each of the initiation steps

##### Configuration Discovery  

AndHow will use the 
[`StdConfig`](https://github.com/eeverman/andhow/blob/master/andhow-core/src/main/java/org/yarnandtail/andhow/StdConfig.java) 
to configure itself, which is an instance of 
[`AndHowConfiguration`](https://github.com/eeverman/andhow/blob/master/andhow-core/src/main/java/org/yarnandtail/andhow/AndHowConfiguration.java)
, unless there is an implementation of 
[`AndHowInit`](https://github.com/eeverman/andhow/blob/master/andhow-core/src/main/java/org/yarnandtail/andhow/AndHowInit.java) 
is on the classpath. `AndHowInit` is an interface with a single method: `getConfiguration()` which returns 
`AndHowConfiguration`. Common initiation needs, like injecting `String[] args` or adding fixed values can be handled 
in-line with explicit initiation (example above). For more detailed control, subclassing `StdConfig` and providing an 
`AndHowInit` implementation is needed.

 > ##### How does AndHow scan for `AndHowInit` implementations?  
 > 
 > AndHow uses a similar process for Property discovery and configuration discovery. At compile time, the 
 > [`AndHowCompileProcessor`](https://github.com/eeverman/andhow/blob/master/andhow-annotation-processor/src/main/java/org/yarnandtail/andhow/compile/AndHowCompileProcessor.java) 
 > 'sees' all classes as they are being compiled. If it sees a non-abstract `AndHowInit`, the processor adds a 
 > provider-configuration file at: `META-INF/services/org.yarnandtail.andhow.AndHowInit` which just contains the full 
 > name of the implementation. At runtime, the Java 
 > [`ServiceLoader`](https://docs.oracle.com/javase/9/docs/api/java/util/ServiceLoader.html) is able to scan all jars 
 > and resources on the classpath for a specific service provider interface (`org.yarnandtail.andhow.AndHowInit` in 
 > this case) and return instances of that service. Scanning all jars on the classpath for any classes implementing a 
 > specific interface is not easily or performantly possible; Scanning jars for the presence of a specific file is.
 > 
 > TLDR - AndHow is able to find your custom AndHowInit implementation anywhere on the classpath.

_unfinished documentation . . ._