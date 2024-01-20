# On my mind

* Web server support
* Use JSON schema to define input
    * https://github.com/pwall567/json-kotlin-schema
* Document for first user
* How to do real-world integration tests against Digital.ai Platform
* Automated tests for command line invocations

# Where to take it

* Plaxolotl - cli scripts are just an interface to a portable execution format. This format has all the metadata defined
  explicitly. For example: content type, variable replacement yes/no, etc.
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
* Secrets
* CommandLibrary should store commands in canonical form: all lower case and spaces
* Define 'output' on Script info
* Add condition to input fields on Script info and Prompt
* Naming: Prompt / Prompt one / Prompt all / Prompt multiple
* Remove `Task`

# Blog topics

* How to Design a Language Without Writing a Parser
* How Complexity Creeps in
* Keep it Flat, SIlly (KIFS)
* How (Not) to Create a Lisp
* The Zen of Frictionlessity: Avoid Surprises, Humor and Being Clever
    * "Http serve" (now: "Http endpoint")
    * Cheesy test data
* Liberation of Code: Say What, Not How
    * Sample Server in Instacli vs. Javalin/Kotlin