---
title: Developer
---  

 > This page describes how to develop and modify the AndHow project.  If you are looking for information for how to use 
 > AndHow in your project, head back to the [Home](../) or [Quick Start](../quick-start) pages.

### Join in the Fun  

Wow, I'm honored that you are considering helping out with this project. Lets work together - I know we will make 
something great!

### First Steps  

 - 'Star' the AndHow project in GitHub. This raises the profile of AndHow a bit and may help others to become 
   contributors.
 - [Join the AndHow discussion](../join-discussion) so you can ask questions and discuss development topics

### Contributing  

There are lots of ways to contribute to AndHow:  

 - Report a bug or suggest a feature on the [issue tracker](https://github.com/eeverman/andhow/issues)  
 - Take on an existing issue - Start by assigning it to yourself so we know you have taken it. If you are new to the 
 project, be sure to check out the issues marked 
 [good first issue](https://github.com/eeverman/andhow/issues?q=is%3Aissue+is%3Aopen+label%3A%22good+first+issue%22).  
 - Offer to help write or edit the documentation on this site  
 - Write tests or improve the Javadocs  
 
### Project Tenets and Development Guidelines  

**AndHow must use no runtime dependencies.** AndHow is intended to be a low level dependency that can be used in any 
application or other utility. If AndHow has its own set of dependencies, that can lead to version conflicts and bloat 
when included in other projects. AndHow does have dependencies for testing and at compile time (the _tools.jar_ / 
_jdk.compile_ module), but none of these are dependencies at runtime.

**AndHow must have good quality and effective test coverage.** As a low level utility, we don't want user's to have to 
second guess if it is working correctly. 
[Effective test coverage does not mean that test coverage numbers must approach 100%](https://martinfowler.com/bliki/TestCoverage.html), 
but the tests should give confidence that the code functions as intended and is capable of catching new bugs.

As a contributor, please:

 - Write tests for new functionality  
 - Write tests for untested code if you are modifying the code  
 - ...always feel free to contribute tests for untested or poorly tested code  

Because AndHow operates as an annotation processor at compile time and as a singleton during runtime, there are unique 
challenges to testing - some functionally simply cannot be tested while the jar is being built. Caveats aside, the 
reported test coverage is 70%, which is probably a lower than it should be - see the list of 
[individual file coverage](https://codecov.io/gh/eeverman/andhow/list/master/).

### Setting up a development workstation  

...is detailed on the [New Workstation Setup page](/new-workstation-setup)