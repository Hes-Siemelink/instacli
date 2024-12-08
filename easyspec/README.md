# Easyspec

Easily write specifications using Markdown and Yaml!

Easyspec has two main ideas:

1. Write down what you want in Markdown or Yaml
2. Keep it simple.

This is the simplest Easyspec file, [hello.spec.yaml](samples/hello.spec.yaml):

```yaml spec file:hello.spec.yaml
Print: Hello from Easyspec!
```

Invoke it with

```commandline spec
spec run hello.spec.yaml
```

And it will print the expected message:

```spec output
Hello from Easyspec!
```

# Documentation

All of Easyspec is defined in the **[spec](spec)** directory.

# Build & Run

The Easyspec implementation is in Kotlin.

## Build it

* Install a current JDK

```commandline
./gradlew build
alias cli="java -jar `pwd`/build/libs/easyspec-*.jar"
```

## Run it

Run the "Hello world" example:

```commandline
spec run samples/hello.spec.yaml
```
