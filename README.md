# Instacli

Instantly create CLI applications with light scripting in Yaml!

## Quick Example

Get a flavor of instacli with this example file `greeting.cli`:

<!-- run before example
${input}:
    name: Hes
    language: English
-->

```yaml
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

When running it, we get prompted for input before a POST request is made to the server. The greeting that we get back is
printed.

```commandline
$ cli greeting.cli 
? Enter your name Hes
? Select a language English
Hi Hes!
```

You can specify the parameters as arguments. First let's query for them with the `--help` option:

```commandline
 $ cli --help greeting.cli 
Multi-language greeting

Input parameters:
  --name       Enter your name
  --language   Select a language
```

Now we can call the example again with the parameters filled in:

```commandline
$ cli greeting.cli --name Hes --language Spanish
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

There are several examples in the [samples](samples) directory - check it out!

Explore them all with the following command:

```commandline
cli samples
```

The following example will provide an interactive experience and connect to the Spotify API:

```commandline
cli samples/spotify
```

When connecting to Spotify for the first time, the script will ask you for your login credentials (App Client ID and
Client secret -- you should already have
those). These will be stored
in `~/.instacli/connections.yaml` and will be used for subsequent invocations.

## Documentation

* See [Basic concepts](instacli-spec/basic-concepts) for an overview of the Instacli scripting language
* The [Command reference](instacli-spec/command-reference/README.md) has a list of all the available commands with
  explanation and code examples.

<!--
# Highlight Reel

Main ideas:
* Everything is Yaml
* Code should be easy to read

## Http requests as code

## Invoke as a cli

## Define input 

## Define output

## User interaction

## Variables

## Data manipulation: For each

## Data manipulation: Add and Sort

## Programming logic: If

## Call another cli script

## Call a shell script

## Manage your Http connection info

## Read a file

## Testing in Instacli

## Documenting Instacli
-->

