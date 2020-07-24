This page lists all the existing rule implementations we have to offer.

## How to use this document...

Each rule has two mandatory parameters and can have additional parameters on top of those at the discretion of the
implementation. We will detail only the additional parameters at the specific rules to avoid copy-pasting the basics.

The mandatory configuration parameters are:

| Parameter | Example       | Description                                                                  |
| --------- | ------------- | ---------------------------------------------------------------------------- |
| `name`    | `rule-name`   | The name used when the rule is registered.                                   |
| `path`    | `$.json.path` | The path used to find out which parts of the JSON are in scope for the rule. |

The optional parameters are defined under the `param` object.

Each rule has a **version** when it was added to the app, a **class** implementing them, a **name** they are registered
with, a **parameter** list defining how they can be configured, a **description** stating what they are supposed to do,
and we will show an **example configuration** as well.

## Rule implementations
