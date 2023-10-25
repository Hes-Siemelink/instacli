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

This example will connect to the Digital.ai Platform API and provide an interactive menu.

In order to be able to connect, put the following in `~/instacli/default-variables.yaml`:

```yaml
digitalaiStaging:
  $type: digitalai.platform.Endpoint
  url: https://api.staging.digitalai.cloud
  username: <USERNAME>
  password: <PASSWORD>
  id: acme  
```

Then start the cli with

```commandline
cd instacli-spec/samples
cli -i digitalai
```

