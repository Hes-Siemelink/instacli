# Instacli

Instantly create CLI applications with light scripting in Yaml!

## Example script

Get a flavor of instacli with this example that does a small interaction between user and server.

```yaml
GET: /flavor/options
As: flavors

Ask user:
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
cd instacli-spec/samples
cli hello
```

## Interactive example

This example will connect to the Digital.ai Platform API and provide an interactive experience.

Start the cli with

```commandline
cd instacli-spec/samples
cli digitalai
```

When connecting to Digital.ai Platform for the first time, the script will ask you for your login credentials. These will be stored
in `~/.instacli/connections.yaml` and will be used for subsequent invocations.
