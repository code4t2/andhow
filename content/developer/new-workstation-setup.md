---
title: New Workstation Setup
weight: 15
---  

### Reference workstation platform  

Main development has been done on this platform:

 - [Java JDK 1.8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) 
 - [Netbeans 8.2](https://netbeans.apache.org/download/index.html) (SE, EE or 'All' version)
 - [Maven 3.5.2](https://maven.apache.org/install.html) (min 3.2.2 required)
 - [Git 2.8.1](https://git-scm.com/downloads) or better
 - A [Github](https://github.com/) account is required to submit pull requests to merge you code changes into the 
 canonical repository  
 
MacOS 10.13 was used as the OS, but this is not an actual requirement. Linux and Windows systems should work but are 
untested.

**Java JDK8 is a requirement** for building AndHow 0.4.x, however, the 0.5.0 release will only support JDK9 and above. 
This hard line is due to [Jigsaw](https://www.baeldung.com/project-jigsaw-java-modularity), introduced in JDK9, which 
allows modularization of the JDK and applications. AndHow depends on the JDK tools.jar, which was removed in JDK9 and 
replaced with the `jdk.compile` module. The add-modules mechanism of JDK9 forces the built jar to be Java9 compatible - 
its not possible to compile with JDK9, add a module and build a Java8 compatible jar.

_Fret Not!_ The resulting JDK8 AndHow jar can be used in Java9 and newer projects, so this limitation only affects 
development of AndHow itself, not projects that use it.

**Netbeans 8.2 is not a requirement**, but works well for JDK8 Java development. Any other compatible IDE (or no IDE) 
could be used. Netbeans ships with an older version of Maven - after installing a newer version, be sure to update 
Netbeans to use it: _Setting/Preferences|Java|Maven|Maven Home_ Note that on 'nix systems, the correct Maven home is 
the libexec directory of the Maven installation.

**Maven 3.5.2 is a requirement**, though any recent version of Maven will work. Maven provides dependencies and build 
scripting for this and many other Java projects.

**Git 2.8.1 is a requirement**, though any recent version will work. Git is the source code management system for this 
project and is in wide use.

### Checking out the code  

AndHow development follows a typical fork-and-branch aka fork-and-pull workflow.  The basics of that are:

 - As a developer, you will have your own private fork of the AndHow project on GitHub  
 - You will clone code from your fork to your local machine  
 - Work for each task/feature/bug fix is done in a new branch just for that task  
 - Completed work is submitted for review via creating a pull request, requesting that changes from a development 
 branch be merged into the canonical repository.  

If this workflow is new to you, there are lots of 
[good write-ups](https://blog.scottlowe.org/2015/01/27/using-fork-branch-git-workflow/) on the topic. Assuming you have 
already forked AndHow, here is the quick version of getting a local copy ready for development:

{{< highlight sh "hl_lines=1 3 9 10" >}}
> git --version
git version 2.15.2 [Or something similar]
> git clone https://github.com/[your user name]/andhow.git
Cloning into 'andhow'...
remote: Counting objects: 10545, done.
remote: Compressing objects: 100% (73/73), done.
remote: Total 10545 (delta 19), reused 60 (delta 7), pack-reused 10445
Receiving objects: 100% (10545/10545), 13.
> cd andhow
> git remote add upstream https://github.com/eeverman/andhow.git
{{< / highlight >}}