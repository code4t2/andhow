---
title: Home
weight: 0
---  

# AndHow

<strong><center> strong.valid.simple Application Configuration </center></strong>

AndHow is an easy to use configuration framework with strong typing and detailed validation for web apps, command line 
or any application environment.

### Key Features  

<input type="checkbox" checked disabled> Strong Typing  
<input type="checkbox" checked disabled> Detailed validation  
<input type="checkbox" checked disabled> Simple to use  
<input type="checkbox" checked disabled> Use Java public & private to control configuration visibility  
<input type="checkbox" checked disabled> Validates all property values at startup to Fail Fast  
<input type="checkbox" checked disabled> Loads values from multiple sources (JNDI, env vars, prop files, etc)  
<input type="checkbox" checked disabled> Generate configuration sample files based on application properties  

### Questions / Discussion / Contact  

[Join the discussion](https://sites.google.com/view/andhow/join-discussion) on the 
[user forum](https://groups.google.com/d/forum/andhowuser) or the Slack group (See details on the 
[Join](https://sites.google.com/view/andhow/join-discussion) page).  
Do you have a question or need some help? [Join the discussion!](https://sites.google.com/view/andhow/join-discussion)

### Use it via Maven (available on Maven Central)  

{{< highlight xml "linenos=inline,hl_lines=2-3" >}}
<dependency>
    <groupId>org.yarnandtail</groupId>
    <artifactId>andhow</artifactId>
    <version>0.4.0</version>
</dependency>
{{< / highlight >}}

_AndHow can be used in projects with Java 8 and above, however, Java 9 and above have
[some restrictions.](https://sites.google.com/view/andhow/user-guide/java9)_

#### Sample Usage Example

{{< highlight java "linenos=inline,hl_lines=7 19" >}}
  package org.simple;
  
  import org.yarnandtail.andhow.property.*;
  
  public class GettingStarted {
    
   // Section <1> - Declaring AndHow properties
   final static IntProp COUNT_DOWN_START = IntProp.builder().mustBeNonNull()
      .desc("Start the countdown from this number")
      .mustBeGreaterThanOrEqualTo(1).defaultValue(2).build();
   
    private final static StrProp LAUNCH_CMD = StrProp.builder().mustBeNonNull()
      .desc("What to say when its time to launch")
      .mustMatchRegex(".*Go.*").defaultValue("GoGoGo!").build();
   
   public String launch() {
    String launch = "";
    
    // Section <2> - Using AndHow Properties
    for (int i = COUNT_DOWN_START.getValue(); i >= 1; i--) {
      launch = launch += i + "...";
    }
  return launch + LAUNCH_CMD.getValue();
 }

 public static void main(String[] args) {
    GettingStarted gs = new GettingStarted();
    System.out.println(gs.launch());
 }
}
{{< / highlight >}}
From the code example above:  

##### Section <1> - Declaring AndHow properties  

Properties must be `final static`, but may be `private` or any other scope. `builder` methods simplify adding 
validation, description, defaults and other metadata. Properties are strongly typed, so default values and validation 
are specific to the type, for instance, `StrProp` has regex validation rules for `String`s while the `IntProp` has 
greater-than and less-than rules available.  

##### Section <2> - Using AndHow Properties  

AndHow Properties are used just like static final constants with an added `.getValue()` on the end to fetch the value.
Strong typing means that calling `COUNT_DOWN_START.getValue()` returns an `Integer` while calling
`LAUNCH_CMD.getValue()` returns a `String`.

##### How do I actually configure some values?  

The example has default values for each property. With no other configuration available, invoking the main method uses 
the default values and prints: `3...2...1...GoGoGo!` The simplest way to configure some other values would be to create 
a properties file named `andhow.properties` on your classpath, but _don't do that quite yet - AndHow will create that 
file for you._  

What happens if we remove one of the default values from the code above? Both properties must be non-null, so removing 
the default causes the validation rules to be violated at startup.  Here is an excerpt of the console output when that 
happens:

{{< highlight log >}}
========================================================================
Drat! There were AndHow startup errors. Sample configuration files will be written to: /some_local_tmp_directory/andhow-samples/'
========================================================================
REQUIRMENT PROBLEMS
Property org.simple.GettingStarted.COUNT_DOWN_START: This Property must be non-null
========================================================================
{{< / highlight >}}

AndHow throws a RuntimeException to stop application startup and uses property metadata to generate specific error 
messages and (helpfully) sample configuration files. Here is an excerpt of the generated properties file:

{{< highlight log >}}
# ######################################################################
# Property Group org.simple.GettingStarted

# COUNT_DOWN_START (Integer) NON-NULL - Start the countdown from this number
# The property value must be greater than or equal to 1
org.simple.GettingStarted.COUNT_DOWN_START = [Integer]

# LAUNCH_CMD (String) NON-NULL - What to say when its time to launch
# The property value must match the regex expression '.*Go.*'
org.simple.GettingStarted.LAUNCH_CMD = [String]
{{< / highlight >}}

AndHow uses the property metadata to create a detailed and well commented sample configuration file for your project. 
Insert some real values into that file and place it on your classpath at `/andhow.properties` and it will automatically 
be discovered and loaded at startup.

Thats it!  To get started with AndHow, check out the 
[Quick Start page](https://sites.google.com/view/andhow/quick-start).