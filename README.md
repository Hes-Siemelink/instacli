# Instacli

Instantly create CLI applications with light scripting in Yaml!

Use Instacli to quickly automate or prototype light tasks like testing out APIs. Sprinkle in some user interaction.
As-code, but without the complexity of actual code.

## Full Example

Get a flavor of instacli with this example file `greeting.cli`:

<!-- run before
${input}:
    name: Hes
    language: English
-->

```yaml file:greeting.cli
Script info:
  description: Multi-language greeting
  input:
    name: Enter your name
    language:
      description: Select a language
      type: select one
      choices:
        - English
        - Spanish
        - Dutch

POST:
  url: http://localhost:2525/greeting
  body:
    name: ${input.name}
    language: ${input.language}

Print: ${output}
```

Run the script with this command:

```commandline cli
cli greeting.cli
```

When running it, we get prompted for input before a POST request is made to the server. The greeting that we get back is
printed.

<!-- input
Enter your name: Hes
Select a language: English
-->

```output
? Enter your name Hes
? Select a language 
 ❯ ◉ English
   ◯ Spanish
   ◯ Dutch

Hi Hes!
```

You can specify the parameters as arguments. First let's find out what to pass with the `--help` option:

```commandline cli
cli --help greeting.cli
```

Wil print:

```output
Multi-language greeting

Options:
  --name       Enter your name
  --language   Select a language
```

We can call the example again with the parameters filled in:

```commandline cli
cli greeting.cli --name Hes --language Spanish
```

And we get the result in Spanish:

```output
¡Hola Hes!
```

## Build it

* Install a current JDK

```commandline
./gradlew build
alias cli="java -jar `pwd`/build/libs/instacli-*.jar"
```

## Run it

Run the "Hello world" example:

```commandline
cli samples/hello.cli
```

There are more examples in the [samples](samples) directory - check them out!

Explore them all with the command:

```commandline
cli samples
```

The following example will provide an interactive experience and connect to the Spotify API:

```commandline
cli samples/spotify
```

When connecting to Spotify for the first time, the script will ask you for your login credentials (App Client ID and
Client secret -- you should already have those). These will be stored in `~/.instacli/connections.yaml` and will be used
for subsequent invocations.

## Documentation

* See [Basic concepts](instacli-spec/basic-concepts) for an overview of the Instacli scripting language
* The [Command reference](instacli-spec/reference/README.md) has a list of all the available commands with explanations
  and code examples.

# Highlight Reel

Instacli has two main ideas:

1. Everything is Yaml.
2. Keep it simple.

## Hello world

This is the simplest Instacli progam, [hello.cli](samples/hello.cli):

```yaml file:hello.cli
Print: Hello from Instacli!
```

Invoke it with

```commandline cli
cli hello.cli
```

And it will print the expected message:

```output
Hello from Instacli!
```

## Http requests as code

Tired of remembering the exact curl syntax or forgetting which tab had that request that worked in Postman?

Simply write your request as-code with Instacli:

```yaml cli
Code example: A simple GET

GET: http://localhost:2525/greetings
```

Here's a POST:

```yaml cli
Code example: A simple POST

POST:
  url: http://localhost:2525
  path: /greeting
  body:
    name: Hes
    language: Dutch
```

## Define input

Define all command-line options in Yaml. Take this file `simple-options.cli`

```yaml file:simple-options.cli
Script info:
  description: Call Acme
  input:
    user: Username
    language: Preferred language
```

This will automatically generate a command description and command line options:

```commandline cli
cli --help simple-options.cli
```

```output
Call Acme

Options:
  --user       Username
  --language   Preferred language
```

## Input options

Instacli allows you to specify the type and format of input properties. Here's an example file `input-options.cli`

```yaml file:input-options.cli
Script info:
  description: Different input options
  input:
    user:
      description: Username
      short option: u
    password:
      description: Password
      type: password
      short option: p
```

```commandline cli
cli --help input-options.cli
```

```output
Different input options

Options:
  --user, -u   Username
  --password, -p   Password
```

By default, Instacli runs in interactive mode. If there are unknown commandline options, the user is prompted to give
input.

```commandline cli
cli input-options.cli
```

<!-- input
Username: Hes
Password: Secret
-->

```output
? Username Hes
? Password ********
```

## Subcommand support

Easily povide subcommand support by organizing your cli files in directories.

For example, to run the greeting example from the [samples](samples) directory, you can write

```commandline
cli samples basic greet
```

with output:

```
Hello, World!
```

You can interactively select which command to run.

```commandline
cli samples
```

Use the cursor to select a command

```
samples has several subcommands.

* Available commands: 
 > basic         Simple Instacli example scripts
   digitalai     Interact with Digital.ai products and services.
   hello         Hello
   http-server   Use Instacli to run web services
   programming   Programming examples in Instacli
   spotify       Spotify API examples
```

Use the `-q` option for non-interacive mode

```commandline
cli -q samples
```

will just print the info message:

```
samples has several subcommands.

Available commands:
  basic         Simple Instacli example scripts
  digitalai     Interact with Digital.ai products and services.
  hello         Hello
  http-server   Use Instacli to run web services
  programming   Programming examples in Instacli
  spotify       Spotify API examples

```

## User interaction

Easily construct user prompts with Instacli.

Here's an example of how to ask the user to pick something from a list, in a file called `prompt.cli`:

```yaml file:prompt.cli 
Prompt:
  description: Select a language
  type: select one
  choices:
    - English
    - Spanish
    - Dutch

Print:
  You selected: ${output}
```

Run it and you will be presented with an interactive selector:

```commandline cli
cli prompt.cli
```

<!-- input
Select a language: English
-->

```output
? Select a language 
 ❯ ◉ English
   ◯ Spanish
   ◯ Dutch

You selected: English
```

## Variables

Define variables in `${...}` syntax and pick and choose content using the path notation.

```yaml cli
${var}:
  name: my variable
  content:
    1: one
    2: two

Print: ${var.content}
```

will print

```
1: one
2: two
```

## Http Server

For quick API prototyping, Instacli will run an Http server for you. Define some endpoints and back them by Instacli
scripts!

```yaml cli
Http endpoints:

  /example:
    get:
      script:
        Output: Hello from Instacli!
```

<!--

## Programming logic: If
## Data manipulation: For each
## Testing in Instacli
## Documenting Instacli

## Call a shell script
## Call another cli script
## Define output
## Data manipulation: Add and Sort
## Manage your Http connection info
## Read a file and save

-->

<!--
Use Instacli to cook up a CLI in minutes using just Yaml files.

Express your intent. without ceremony or boilerplating

Code should be easy to read: Instacli encourages a declarative style: start telling 'what' you want not 'how' to do it.
There is a thin line between 'what' and 'how'.

The purpose of Instacli is that you can get as far as possible with straightforward scripts that are readable to
everybody.

When programming, complexity creeps in and your neat idea "turns into code" quite quickly. Instacli is designed to avoid
that point as long as possible, so you can quickly whip up a functional CLI.

-->
