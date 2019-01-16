---
title: Projects using Java 9+
weight: 11
---  

### Special considerations with Java 9 and Above  

With Java 9 and the introduction of [Jigsaw](https://www.baeldung.com/project-jigsaw-java-modularity) and the 
[module system](https://www.oracle.com/corporate/features/understanding-java-9-modules.html), Java no longer allows 
arbitrary reflection: If a module is not open, its not possible to use reflection to inspect private members of the 
classes in the module.

AndHow uses reflection to get references to the configuration Properties declared in your classes. Without the ability 
to reflect into your application code, AndHow can't do its job.

The good news is that AndHow has been tested and works on Java 9 - Java 11 for projects that do not use Java modules. 
AndHow _should_ work on Java 9+ projects that use modules, but have them marked as `open`. The next major release of 
AndHow will switch to using the Java module system so that AndHow can be explicitly allowed to access private modules 
via the _opens to_ mechanism.