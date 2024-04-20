# On my mind

* Types
* Deifne commands
* Define on different scopes: script, dir, global
* Serialize more Kotlin like
* Script info with types
* Turn types into JSON schema
* Implement JSON Patch

# Where to take it

* Plaxolotl - cli scripts are just an interface to a portable execution format. This format has all the metadata defined
  explicitly. For example: content type, variable replacement yes/no, etc.
* Spec.it - Extract into a separate module:
    * Markdown doc
    * Cli tests
    * Schema stuff
    * Http server
    * ...what's left not to extract?
* Run Release templates

# Command-line support

* Kotlin native? => Use Pure Kotlin libraries
* Go?
* Node / TypeScript?

# Instacli as glue

* Shell and pipe support
* Database support (SQLite or something)

# Instacli language

* Don't use JSON schemas to define input
* Error handling
* Error reporting -> "StackTrace"
* Friendly messages when schema doesn't validate
* Check output from Yaml scripts in tests
* Run CLI command as 'semi-interactive': print mock output and exit.
    * Check with Running instacli and command line examples.
* Clean up Connect to: be smart about multiple connections and tokens. Currently `connect-to` script in Digital.ai only
  checks if something has been set as Http defaults
* Built-in OAuth. It's kinda cool that you can do it in Instacli but not that you should... Makes the script 'turn into
  code'.
* Review Script info on directories
* Secrets
* Raw and live / Apply variables
* CommandLibrary should store commands in canonical form: all lower case and spaces
* Define 'output type' on Script info
* Add condition to input fields on Script info and Prompt
* Special variables
    * `input`
    * `output`
    * `request`
    * `session`
    * `settings`
* Properly handle: null, empty, boolean, int
* Note for stdin:
  if (System.`in`.available() != 0) { val input = Yaml.mapper.readTree(System.`in`)}

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