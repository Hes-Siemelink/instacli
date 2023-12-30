# Instacli

Instantly create CLI applications with light scripting in Yaml!

## Example script

Get a flavor of instacli with this example that does a small interaction between user and server.

```yaml
GET: /flavor/options
As: flavors

Prompt:
  description: Select your favorite flavor
  choices: ${flavors}
  type: select one
As: favorite

POST:
  path: /flavor/favorite
  body:
    flavor: ${favorite}
```

## Build it

* Install a current JDK

```commandline
./gradlew build
alias cli="java -jar `pwd`/build/libs/instacli-*.jar"
```

## Run it

Hello world example:

```commandline
cd samples
cli hello
```

## Examples

There are several examples in the [samples](samples) directory - check it out!

The following example will provide an interactive experience and connect to the Spotify API:

```commandline
cd samples
cli spotify
```

When connecting to Spotify for the first time, the script will ask you for your login credentials (App Client ID and Client secret -- you should already have
those). These will be stored
in `~/.instacli/connections.yaml` and will be used for subsequent invocations.

## Documentation

* See [Basic concepts](instacli-spec/basic-concepts) for an overview of the Instacli scripting language
* The [Command reference](instacli-spec/command-reference/README.md) has a list of all the available commands with explanation and code examples.

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