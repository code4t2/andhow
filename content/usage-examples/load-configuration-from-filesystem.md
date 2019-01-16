---
title: Load configuration from filesystem
weight: 5
---  

#### Load configuration from somewhere on the filesystem  

Loading from the filesystem is slightly trickier than loading from the classpath because the filesystem varies between 
environments.

Full source and tests for this example can be found 
[here](https://github.com/eeverman/andhow-samples/tree/master/load-config-from-filesystem).

The path to a property file on the filesystem will vary from machine to machine, tier to tier, so it cannot be 
hardcoded - it must be a Property that can be configured.

To tell AndHow about that property, you must implement the `AndHowInit` interface. AndHow will find that implementation 
at runtime and call `getConfiguration()` on it to configure itself. You can build that configuration to have a property 
pointing to a file path - Just make sure the value for that property is loaded prior to the 
`StdPropFileOnFilesystemLoader`. The load order is listed on the [home page](../../).