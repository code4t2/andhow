---
title: main() startup example
weight: 4
---  

#### Example an app that starts up & loads arguments from main(String[] args)  

Full source and tests for this example can be found 
[here](https://github.com/eeverman/andhow-samples/tree/master/simple-using-string-args) and is an extension of the 
example on the home page.  

{{< highlight java "linenos=inline" >}}
package org.simple;

import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.property.*;

@GroupInfo(name="Launch Config", desc="More details...")
public class SimpleStringArgs {
  
 final static IntProp COUNT_DOWN_START = IntProp.builder().mustBeNonNull()
   .desc("Start the countdown from this number")
   .mustBeGreaterThanOrEqualTo(1).build();
 
 private final static StrProp LAUNCH_CMD = StrProp.builder().mustBeNonNull()
   .desc("What to say when its time to launch")
   .mustMatchRegex(".*Go.*").build();
 
 private final static BolProp NOTIFY_NEWS = BolProp.builder().defaultValue(true)
   .desc("If true, post launch events to the AP news feed").build();
 
 public String launch() {
  String launch = "";
  
  for (int i = COUNT_DOWN_START.getValue(); i >= 1; i--) {
   launch = launch += i + "...";
  }
  
  return launch + LAUNCH_CMD.getValue();
 }
 
 public static void main(String[] args) {
  
  AndHow.findConfig()
    .setCmdLineArgs(args)
    .addFixedValue(NOTIFY_NEWS, false) //turn off for cmd line use
    .build();
  
  SimpleStringArgs gs = new SimpleStringArgs();
  System.out.println(gs.launch());
 }
 
 public boolean isNewsNotified() { return NOTIFY_NEWS.getValue(); }
}
{{< / highlight >}}

This example is just like the home page example with a few changes. First, we added a new Boolean property 
`NOTIFY_NEWS.` 'Notify' might post to a news feed to let the world know about the launch: You probably want to happen 
when the full enterprise system is preparing a real launch (the default value is true), you probably don't want that to 
happen if a developer is just running this locally from command line.

Next, we added a `@GroupInfo` annotation so we could add some notes and help information about configuring the 
Properties contained in this class.  When AndHow generates a sample configuration file, this information will be 
included.

We've also made two key additions to the main method:  
 - `setCmdLineArgs(args)` injects the command line arguments into the AndHow configuration so they are included when 
 property values are read
 - `addFixedValue(NOTIFY_NEWS, false)` forces `NOTIFY_NEWS` to always be false when run from the command line because 
 we don't local testing to show up on CNN. When a 'real launch' happens, this class would probably be used in larger 
 framework and the main method not used.

One more small change: The default values were removed from `COUNT_DOWN_START` and `LAUNCH_CMD`. When this is run for 
the first time, an error is thrown because these properties are required. Any time there is a startup error, AndHow 
generates configuration samples. Here is an excerpt - Note that the `@GroupInfo` information was added:

{{< highlight log >}}
# ##########################################################################################
# Property Group 'Launch Config' - More details...
#   Defined in org.simple.SimpleStringArgs

# 
# COUNT_DOWN_START (Integer) NON-NULL - Start the countdown from this number
# The property value must be greater than or equal to 1
org.simple.SimpleStringArgs.COUNT_DOWN_START = [Integer]

# 
# LAUNCH_CMD (String) NON-NULL - What to say when its time to launch
# The property value must match the regex expression '.*Go.*'
org.simple.SimpleStringArgs.LAUNCH_CMD = [String]

# 
# NOTIFY_NEWS (Boolean)  - If true, post launch events to the AP news feed
# Default Value: true
org.simple.SimpleStringArgs.NOTIFY_NEWS = true
{{< / highlight >}}

If you try out [this sample project](https://github.com/eeverman/andhow-samples/tree/master/simple-using-string-args), 
the generated property file has already been copied to the resources directory as `/andhow.properties` - delete or 
rename it to force AndHow to generate another copy.