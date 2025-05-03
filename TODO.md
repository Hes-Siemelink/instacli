# On my mind

## In general

* Learn how to write Go
* Learn how to use AI
* AI to generate scripts: notebook type interaction to write scripts
* Document Json Patch (uses RFC 6902)

## For live deployments demo:

* Temp file
    * Option to set temp dir variable on shell directive
* Document: Shell 'show command' and 'show output' options in directive
* Find a way to confirm default input parameters vs. just taking them for granted. Interactive mode would trigger
  confirmation?
* Shell: option to capture output as yaml.
* Clean up Exceptions and error handling
* BUG: $SCRIPT_DIR doesn't work
* BUG: shell error is not caught by On error. (When installing k3d runner without k3d)
* BUG: First line of output is not captured if shell script asks for user input => command appears to hang

# Actually build something

* Home Recipes
    * Store stuff on file system with a construct like Connect to
    * Store stuff on sqlite using same construct
    * Edit lists

# Installable artifact

* Produce a clean, lightweight, native library
* Node / TypeScript?
* Kotlin native? => Use Pure Kotlin libraries
* Go?
* Compile to WASM

# Easyspec

* Extract into a separate module:
    * Markdown doc
    * Cli tests
    * Schema stuff
    * Http server
    * ...what's left not to extract?
* Naming
    * Easyspec
    * Easyspec.ai
    * Quickspec
    * Spec.it
* Add tests for samples
    * Test script for a directory, using multiple commands

# Instacli language

## Core

* Proper "StackTrace"
* Define commands in Instacli
* Secrets
* Properly handle: null, empty, boolean, int
* Also use ` ```output` directive in markdown execution to check expected output

## Types

* Document types
* Define array and string types in type system
* Define 'output type' on Script info
* Turn types into JSON schema
* Refactor types and directory info

## Docs

* Document literate programming style with cli scripts in markdown
* Run CLI command as 'semi-interactive': print mock output and exit.

## Http

* Configuring connections out of the box with packaged instacli scripts
* Clean up Connect to: be smart about multiple connections and tokens. Currently `connect-to` script in Digital.ai only
  checks if something has been set as Http defaults
* Built-in OAuth. It's kinda cool that you can do it in Instacli but not that you should... Makes the script 'turn into
  code'.

## Shell & Files

* Support stdin:
  if (System.`in`.available() != 0) { val input = Yaml.mapper.readTree(System.`in`)}

* Pass environment variables to shell command
    * SCRIPT_ROOT_DIR

* Code organization
    * Versioning and auto-upgrading of commands
    * Review Script info on directories
    * Support modules

## Improve slow startup

* Jackson is slow on Kotlin: https://github.com/FasterXML/jackson-module-kotlin/issues/69
* Alternative Yaml lib: https://github.com/pwall567/kjson-yaml
* Or rewrite as Go or TypeScript

# Bugs

* BUG: Unknown variable error when using a variable in a script that is not defined in the Script Info -- see goodbye
  example in TempFile spec.

# Where to take it

* Plaxolotl - cli scripts are just an interface to a portable execution format. This format has all the metadata defined
  explicitly. For example: content type, variable replacement yes/no, etc.
* HouseApp
* Run Release templates
* Instacli as glue
    * Shell and pipe support
    * https://www.gnu.org/software/bash/manual/bash.html
    * Database support (SQLite or something)

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

# Nerd badges 🦡

* Code as-code
* Towers of Hanoi
* Made it a Lisp by adding 30 lines of code.
* Define Instacli in Instacli itself