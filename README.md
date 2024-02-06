# Instacli

Instantly create CLI applications with light scripting in Yaml!

## Quick Example

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
  url: http://localhost:25125/greeting
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
? Select a language English
Hi Hes!
```

You can specify the parameters as arguments. First let's query for them with the `--help` option:

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
2. Keep it simple: Express your intent. without ceremony or boilerplating

Use Instacli to cook up a CLI in minutes using just Yaml files. Code should be easy to read: Instacli encourages a
declare style: start telling 'what' you want not 'how' to do it. There is a thin line between 'what' and 'how'. The
purpose of Instacli is that you can get as far as possible with straightforward scripts that are readable to everybody.
When programming, complexity creeps in and your need idea "turns into code"". Instacli is designed to avoid that point
as long as possible, so you can quickly whip up a functional CLI. You can use Instacli to quickly automate or prototype
light tasks like testing out APIs; with some user interaction and do that in a repeatable way, as-code so to speak, but
without the complexity.

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

## User interaction

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

<!-- FIXME
  Use the `-q` option to avoid user prompts:
  
  ```commandline cli
  cli -q input-options.cli
  ```
  
  ```output
  ? Username Hes
  ? Password ********
  ```
-->

<!--
## Subcommand support

## Http requests as code

## User interaction

## Variables

## Read a file and save

## Call a shell script

## Call another cli script

## Define output

## Programming logic: If

## Data manipulation: For each

## Data manipulation: Add and Sort

## Manage your Http connection info

## Run an HTTP server

## Testing in Instacli

## Documenting Instacli

-->
