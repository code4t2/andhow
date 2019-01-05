---
title: Key Concepts
---

<strong><center> strong.valid.simple Application Configuration </center></strong>

#### AndHow initializes and loads property values only once at startup  

Initiation is AndHow's startup/bootstrap process where it discovers discovers all Properties, loads values, and 
validates them. This will only be done once in the lifecycle of the application and loaded values will never change.

There is more detail about the initiation process on the [Initiation page](../andhow-initiation).

#### AndHow respects Java privacy modifiers  

Internally, AndHow maps the property itself to the property's value.  Thus, when a property is declared with a 
non-public modifier:

{{< highlight java >}}
private final static StrProp PROP = StrProp.builder().build();
{{< / highlight >}}

Only code able to hold a reference to `PROP` can retrieve it's value.

#### AndHow Fails Fast... and that is a good 

 > Failing fast is a nonintuitive technique: “failing immediately and visibly” sounds like it would make your software 
 > more fragile, but it actually makes it more robust.  Bugs are easier to find and fix, so fewer go into production. 
 > --[Fail Fast](https://martinfowler.com/ieeeSoftware/failFast.pdf), Jim Shore / Martin Fowler

A key design principal of AndHow is that a non-deterministic initiation or invalid or missing configuration values 
should be detected as early as possible and should be considered fatal.  AndHow implements this in two ways:

 - As discussed above, multiple attempts to initialize AndHow indicate a non-deterministic initiation and will result 
 in a RuntimeException
 - During initiation, all values for all properties are validated and a RuntimeException thrown if any property is 
 invalid  

The alternative to fail fast would be to fail late. If configuration problems are discovered, for example, only when 
they are used, an application might run for a few days, then a feature that hadn't been used up to that point is 
accessed and __~BOOM~__ its invalid configuration is discovered by bringing the application down. It will probably 
be 4AM when you get the call.

##### AndHow Properties are static final constants and behave that way  

Just like Java static finals, this means that:

 - A Property and its value are defined across the entire classloader just like a class definition  
 - Property values are immutable:  Once loaded during initiation, the Property values cannot be changed  
 - AndHow Properties can be used to configure application or class behavior, not a particular class instance  

Think of AndHow Properties as `static final` constants that pull their value from outside the application.  

#### Property Names are CaSe InSeNsItIvE  

For compatibility with Windows environment variable names, property names are case insensitive. Proper case names are 
still used internally for reporting and when creating configuration sample files.  One exception to this is JNDI, 
which is inherently case sensitive. 

#### Whitespace Handling  

Whitespace is generally removed from values and each Property has a `Trimmer` class assigned to it to handle the 
trimming. `StrProp` uses the `QuotedSpacePreservingTrimmer` and most other properties use the `TrimToNullTrimmer`.

The `TrimToNullTrimmer` simply removes all whitespace on either end of a value. If the result is a zero length string, 
it becomes null. The QSPT first trims to null, then, if the remaining text begins and ends with double quotes, those 
quotes are removed and the string inside the quotes, including whitespace, is preserved as the actual value.

Here are a few examples of the QSPT trimming behavior, using dots (●)to represent whitespace:

<table align=left>
<thead><tr><th><i>With whitespace</i></th><th><i>Whitespace trimmed</i></th><th><i>Explanation</i></th></tr></thead>
<tbody>
<tr><td> ● </td> <td> null </td> <td> (An all whitespace raw value is trimmed to null) </td> </tr>  
<tr><td> ●●●abc●●● </td> <td> abc </td> <td> (whitespace on either side of text removed) </td> </tr>
<tr><td> "●abc●" </td> <td> ●abc● </td> <td> (Quotes are removed and all characters inside preserved) </td> </tr>
<tr><td> ●"●abc●"● </td> <td> ●abc● </td> <td> (same result - whitespace outside the quotes is removed) </td> </tr>
<tr><td> ●a "word" here● </td> <td> a "word" here </td> <td> (No special quote handling here - there is text outside the quotes) </td> </tr>
<tr><td> ●"●a "word" here●"● </td> <td> ●a "word" here● </td> <td> (After trimming outer whitespace, start and & dbl quotes were found) </td> </tr>
<tr><td> ●""● </td> <td> Empty String </td> <td> (Using quotes, it is possible to assign an empty string) </td> </tr>
</tbody><tfoot></tfoot></table>

#### Null Handling  

AndHow does not have an explicit null value that is distinct from a property value never being set. This follows from 
the fact that most property sources (property files, cmd line, etc) have no direct way to indicate a null or to 
distinguish it from an empty string. The current handling is this:

 - Attempting to set a property to an empty or all whitespace is ignored. The Trimmer trims the value to null, and null 
 values are considered unset. (Empty strings & whitespace can be preserved for `StrProp`'s - See above)
 - Any property that is null/unset may have a value loaded for it by a subsequent loader.
 - `FlgProp` is a special case: It emulates the behavior of a command line switch so that its presence alone, even 
 without a value, marks its value as true.  

#### Don't worry about Property names  

While many configuration frameworks are based on a user defined String key for each property, you can safely 'forget' 
about naming AndHow properties. The strings used to refer to AndHow properties in configuration files, JNDI and other 
sources are the semantic paths of the Property declarations, e.g. a property named `Boom` in the class `three.two.One` 
has the name `three.two.One.Boom`. That means you don't have to name them and the Java complier ensures there are no 
naming collisions. You can still add aliases and sometimes you need to to 
[support for legacy applications](../legacy-apps).

Still, typing all those names in a configuration file is a drag, so AndHow will create samples files with all of your 
properties.  