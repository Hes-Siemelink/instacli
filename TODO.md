# On my mind

* Document for first user
* Properly parse command line options
* Web server support

# Where to take it

* Plaxolotl - cli scripts are just an interface to a portable execution format. This format has all the metadata defined
  explicitly. For example: content type,
  variable replacement yes/no, etc.
* Auto-generate Instacli based on OpenAPI metadata. Use AI to make sense of stuff?
* Run Release templates

# Command-line support

* Kotlin native? => Use Pure Kotlin libraries
    * JSON nodes with Kotlin Serialization (but still experimental and no Yaml out of the box)
    * Prompt with https://ajalt.github.io/clikt/
* Go?
* Hide internal scripts from interactive cli. Use property 'hidden' or 'private' or something in Script info

# Instacli as glue

* Shell and pipe support
* Database support (SQLite or something)

# Instacli language

* Error handling
* Combine `If` and `When`and introduce `else`
* Raw and live / Apply variables
