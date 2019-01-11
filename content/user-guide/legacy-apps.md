---
title: Working with Legacy Apps
---  

<strong><center> strong.valid.simple Application Configuration </center></strong>

Strong typing, validation at startup, and semantic property names are nice features for new applications, but what 
about existing applications that are relying on finding specifically named properties?

AndHow uses _alias_ and _exports_ to bridge the gap between legacy 'name dependent' applications and its semantic 
naming approach. Below is an example that might be used for a legacy application expecting to find configuration in 
System.Properties with a specific set of names. Even those the legacy app doesn't used AndHow, we've created a set of 
AndHow properties to shadow the legacy app's configuration:

{{< highlight java "linenos=inline" >}}
@GroupExport(
  exporter=SysPropExporter.class,
  exportByCanonicalName=Exporter.EXPORT_CANONICAL_NAME.NEVER,
  exportByOutAliases=Exporter.EXPORT_OUT_ALIASES.ALWAYS
)
public interface ShoppingCartSvsConfig {
  StrProp SERVICE_URL = StrProp.builder().mustEndWith("/").aliasInAndOut("cart.svs").build();
  IntProp TIMEOUT = IntProp.builder().aliasInAndOut("cart.to").aliasOut("timeout").build();
  StrProp QUERY_ENDPOINT = StrProp.builder().aliasInAndOut("cart.query").build();
  StrProp ITEM_ENDPOINT = StrProp.builder().required().aliasInAndOut("cart.item").build();
}
{{< / highlight >}}

The AndHow Properties are declared in an interface: They don't have to be, but it is a handy shortcut because member 
variables declared in an interface are always `static final` without having to include those modifiers.

Anyway.

Each property is given an `InAndOut` alias. An _in_ alias is just an added name that will be recognized when reading 
property values from some configuration source, like JNDI or System.Properties. An _out_ alias is available for use 
when the property names and values are exported to some configuration destination, like JNDI or System.Properties.

Exports are configured by adding a `GroupExport` annotation to the parent (class or interface) and happen as soon as 
all property loaders have completed. In this example, the annotation specifies the `SysPropExporter`, which exports 
properties to System.Properties. It also specifies that the canonical names of the properties not be used (the legacy 
code wouldn't recognize those names) and that all properties with out aliases should be included in the export.

By using `GroupExport` and `aliasInAndOut`, a legacy application can  be virtually unchanged and still benefit from 
strong typing, validation checks, multi-source loading and other features of AndHow.

Further, multiple `aliasInAndOut`, `aliasIn` and `aliasOut`'s can be added to a single property. Say you discover that 
your legacy code has been using two separately named configuration properties for TIMEOUT. In the example above, that 
property was given an extra out alias so that any legacy code reading `cart.to` or `timeout` from System.Properties 
will find the same value, populated from a single configuration point.