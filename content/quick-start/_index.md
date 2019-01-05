---
title: Quick Start
---  

<strong><center> strong.valid.simple Application Configuration </center></strong>

## A quick walk-thru of using AndHow configuration in your application  

 >  A range of sample applications are available as a separate project, 
 [andhow-samples](https://github.com/eeverman/andhow-samples). Starting with the 
 [simple example](https://github.com/eeverman/andhow-samples/tree/master/simple-using-string-args) is a great starting 
 point for trying out AndHow.  
 
## Add AndHow as a dependency via Maven (available on Maven Central)  

{{< highlight xml "linenos=inline" >}}
<dependency>
    <groupId>org.yarnandtail</groupId>
    <artifactId>andhow</artifactId>
    <version>0.4.0</version>
</dependency>
{{< / highlight >}}  

## Declare Properties  

{{< highlight java "linenos=inline" >}}
public final static IntProp MY_INT = IntProp.builder().build();
private final static StrProp MY_STR = StrProp.builder().build();
{{< / highlight >}}  

As the example shows, AndHow properties can be public or private and are type specific with [several property types 
available](https://github.com/eeverman/andhow/tree/master/andhow-core/src/main/java/org/yarnandtail/andhow/property). 
Properties can be declared in the class or interface where they are used and can include validation rules, description 
and other metadata.

AndHow will automatically discover your declared properties [(details on this process)](../user-guide/andhow-initiation)
, but there is one caveat: You cannot use reflection to create a Property - this will hide it from AndHow's automatic 
discovery process.

## Use Propertys in your code  

{{< highlight java "linenos=inline" >}}
Integer anInt = MY_INT.getValue();
String aString = MY_STR.getValue();
{{< / highlight >}}  

This part is easy:  Calling `getValue()` on any Property fetches its typed value. If AndHow has not already been 
initialized, the first attempt to fetch any value will cause it to initialize and load the configured values for all 
properties. One caveat: Don’t attempt to fetch an AndHow value in static initialization block or assign an AndHow value 
to a static variable, e.g., don't do this:

{{< highlight java "linenos=inline" >}}
static Integer anInt = MY_INT.getValue(); /* NO - Don't assign a fetched value to a static */
static { MY_STR.getValue(); } /* NO - Don't fetch in a static initiation block */ 
{{< / highlight >}}  

Both of the two statements above would force AndHow to attempt to load values at class load time, prior to any possible 
application configuration which might tell AndHow where to find values to load. This situation can be caught and 
prevented during compile time and [will be handled in a future release](https://github.com/eeverman/andhow/issues/292).

## Configuring Values in a Properties File  

Next we need to actually configure values for our properties. AndHow will read configuration from many different 
sources, but the simplest way is to just read them from a properties file named `andhow.properties` at the root of the 
classpath. If the example properties above were in the sample.MyClass class, that properties file could look like this:

{{< highlight java "linenos=inline" >}}
sample.MyClass.MY_INT : 5
sample.MyClass.MY_STR : Some String...
{{< / highlight >}}  

## Configuring Values... in lots of other ways  

A key feature of AndHow is that it can read configuration from many different sources such as JNDI, environmental 
variables, command line arguments, system properties, etc... Internally, AndHow has a standard list of loaders it uses, 
each capable of loading values from a specific configuration source. To get the most out of AndHow, be sure to look at 
[the complete list of standard loaders and their basic behaviors](../user-guide/value-loaders).

## Property names, values, nulls and whitespace  

Unlike most other configuration utilities, did you notice we didn't create a 'name' for the Properties we created? That 
is because Properties are referred to by the Java semantic path of the Property declaration. For example, a property 
declared as `Boom` in the class `three.two.One` would be referred to by the name `three.two.One.Boom` in configuration 
files.

Property names are case insensitive, primarily for Windows compatibility. Proper case names are still used internally 
for reporting and when creating configuration sample files. One exception is when loading properties from JNDI, which 
is inherently case sensitive. By default AndHow removes whitespace and trims values to null. More details on [whitespace 
and null handling here](../user-guide/key-concepts).

## A useful shortcut:  Auto-created sample configuration files  

For most applications, the simplest way to get started is to run the application without any configuration. Assuming 
that at least one required Property with no default value, AndHow will create a sample `andhow.properties` file and 
throw an error at startup like this:

{{< highlight log >}}
========================================================================
Drat! There were AndHow startup errors.  Sample configuration files will be written to: '/some_local_tmp_directory/andhow-samples/'
========================================================================
{{< / highlight >}}

Edit the values to be what you need and place the generated file on your classpath at `/andhow.properties` - AndHow 
should find and read that file at startup. From there you can expand to using the other loaders as needed.

## AndHow Initialization

Initiation is AndHow's startup/bootstrap process where it discovers all `Property` instances (even those in 
dependencies), loads values for them, and validates them. Think about the implications of that for a moment: AndHow 
finds all `Property` instances throughout your entire application, including those in third party jars. How is that 
even possible?

The AndHow initiation and discovery process is one of AndHow's key innovation - [Full details on how that process works 
are here](../user-guide/andhow-initiation).