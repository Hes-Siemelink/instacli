# On my mind

* Document **Save as**
* Document for first user
    * How-to per topic
* Output in JSON and YAML
* Connect to: be smart about multiple connections and tokens. Currently `connect-to` script in Digital.ai only checks if
  something has been set as Http defaults
* Error handling

# Where to take it

* Auto-generate Instacli based on OpenAPI metadata. Use AI to make sense of stuff?
* Plaxolotl - cli scripts are just an interface to a portable execution format. This format has all the metadata defined
  explicitly. For example: content type, variable replacement yes/no, etc.
* Run Release templates

# Command-line support

* Kotlin native? => Use Pure Kotlin libraries
    * JSON nodes with Kotlin Serialization (but still experimental and no Yaml out of the box)
    * Prompt with https://ajalt.github.io/clikt/
* Go?
* Node / TypeScript?
* Hide internal scripts from interactive cli. Use property 'hidden' or 'private' or something in Script info

# Instacli as glue

* Shell and pipe support
* Database support (SQLite or something)

# Instacli language

* Use JSON schema to define input
    * https://github.com/pwall567/json-kotlin-schema
* Error handling
* Error reporting -> "StackTrace"
* Review Script info on directories
* Secrets
* Raw and live / Apply variables
* CommandLibrary should store commands in canonical form: all lower case and spaces
* Define 'output type' on Script info
* Add condition to input fields on Script info and Prompt
* 'DelayedVariableResolver' should also be 'DelayedEvalResolver' => Eval is done too eagerly within If, for example try
  doing a 'Prompt' in else
* Special variables
    * `input`
    * `output`
    * `request`
    * `session`
    * `settings`
* Properly handle: null, empty, boolean, int
* Note for stdin:

```kotlin
if (System.`in`.available() != 0) {
    val input = Yaml.mapper.readTree(System.`in`)
}
```

## BUGS

```commandline
➜  samples git:(main) ✗ cli digitalai/platform/login/select-account.cli
? Select account to log in with * Create new account

Instacli scripting error

Caused by: java.lang.NullPointerException: textValue(...) must not be null

In select-account.cli:

Set default account:
target: Digital.ai Platform
name: null
```

# Blog topics

* How to Design a Language Without Writing a Parser
* How Complexity Creeps in
* Keep it Flat, SIlly (KIFS)
* How (Not) to Create a Lisp
* The Zen of Frictionlessity: On Avoiding Surprises, Humor and Being Clever
    * "Http server" was "Http endpoint" was: "Http serve"
    * Cheesy test data
* Liberation of Code: Say What, Not How
    * Sample Server in Instacli vs. Javalin/Kotlin
* Coding is the Unhappy Path
    * 80% of code is about exceptions to the rule
    * 80% of coding goes into the happy path
    * Typical scenario
        1. Struggle to get the happy path working
        2. Don't apply learnings to code base
        3. Over 50% (or more) of the code is about the exceptions
        4. Exception handling / alternative paths are implemented on top of a shaky code base as an afterthought
    * Why not try "Exception Driven Programming". But: you can't _start_ with the exceptions.
    * Define the happy path as declarative as possible. Build the exception flow around it?
    * Mold the language to have fewer exceptions: be declarative, idempotent