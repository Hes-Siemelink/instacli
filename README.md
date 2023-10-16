# Instacli

Instantly create CLI applications with light scripting in Yaml!

## Example script

Get a flavor of instacli with this example that does a small interaction between user and server.

```yaml
Http GET: /flavor/options
As: flavors

Ask user:
  message: Select your favorite flavor
  choices: ${flavors}
  type: select one
As: favorite

Http POST:
  path: /flavor/favorit
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

Interactive example:

```commandline
cd instacli-spec/samples
cli -i digitalai
```
