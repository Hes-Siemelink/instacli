# Instacli

Instantly create CLI applications with light scripting!

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
