# On my mind

* Performance
    * Typescript / Node
* Configuring connections pout of the box with packaged instacli scripts
* Run Markdown from cli
* Tiered tests
    * Level 0: Bootstrap
    * Level 1: Core functionality
    * Level 2: Nice to haves
    * Level 3: Edge cases

# Where to take it

* Plaxolotl - cli scripts are just an interface to a portable execution format. This format has all the metadata defined
  explicitly. For example: content type, variable replacement yes/no, etc.
* Spec.it - Extract into a separate module:
    * Markdown doc
    * Cli tests
    * Schema stuff
    * Http server
    * ...what's left not to extract?
* HouseApp
* Run Release templates
* Instacli as glue
    * Shell and pipe support
    * Database support (SQLite or something)

# Installable artifact

* Produce a clean, lightweight, native library
* Kotlin native? => Use Pure Kotlin libraries
* Go?
* Node / TypeScript?
* Compile to WASM

# Instacli language

* Error reporting
    * Proper "StackTrace"
* Types
    * Document types
    * Define array and string types in type system
    * Define 'output type' on Script info
    * Turn types into JSON schema
    * Refactor types and directory info
* Core
    * Define commands in Instacli
    * Secrets
    * Raw and live / Apply variables
    * Properly handle: null, empty, boolean, int
* Docs
    * Run CLI command as 'semi-interactive': print mock output and exit.
    * Check with Running instacli and command line examples.
* Http
    * Clean up Connect to: be smart about multiple connections and tokens. Currently `connect-to` script in Digital.ai
      only checks if something has been set as Http defaults
    * Built-in OAuth. It's kinda cool that you can do it in Instacli but not that you should... Makes the script 'turn
      into code'.
* Code organization
    * Versioning and auto-upgrading of commands
    * Review Script info on directories
    * Support modules
* Support stdin:
  if (System.`in`.available() != 0) { val input = Yaml.mapper.readTree(System.`in`)}

# Bugs

* Imported commands show up in directory help
* CommandLibrary should store commands in canonical form: all lower case and spaces

# Implementation improvements

* Slow startup
* Serialize more Kotlin like

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