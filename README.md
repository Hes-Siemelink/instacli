# Instacli

Instantly create CLI applications with light scripting in Yaml!

Use Instacli to quickly automate or prototype light tasks like testing out APIs. Sprinkle in some user interaction.

As-code, but without the complexity of actual code.

## Full Example

Get a flavor of Instacli with this example file `greetings.cli`:

```yaml file=greetings.cli
Script info:
  description: Multi-language greeting
  input:
    name: Your name
    language:
      description: Select a language
      enum:
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

<!-- answers
Your name: Hes
Select a language: English
-->

```shell cli
cli greetings.cli
```

When running it, we get prompted for input before a POST request is made to the server. The greeting that we get back is
printed.

<!-- answers
Your name: Hes
Select a language: English
-->

```output
? Your name Hes
? Select a language 
 ❯ ◉ English
   ◯ Spanish
   ◯ Dutch

Hi Hes!
```

You can specify the parameters as arguments. Find out what to pass with the `--help` option:

```shell cli
cli --help greetings.cli
```

Will print:

```output
Multi-language greeting

Options:
  --name       Your name
  --language   Select a language
```

We can call the example again with the parameters filled in:

```shell cli
cli greetings.cli --name Hes --language Spanish
```

And we get the result in Spanish:

```output
¡Hola Hes!
```

# Documentation

All of Instacli is defined in the **[instacli-spec](instacli-spec)**.

* [CLI](instacli-spec/cli/README.md) defines the `cli` shell command
* [Language](instacli-spec/language/README.md) defines the structure of the Instacli scripting language
* [Command reference](instacli-spec/commands/README.md) defines all commands with descriptions, code examples and tests.

# Build & Run

The Instacli implementation is in Kotlin.

## Build it

* Install a current JDK

```shell ignore
./gradlew build
alias cli="java -jar `pwd`/build/libs/instacli-*.jar"
```

## Run it

Run the "Hello world" example:

```shell ignore
cli samples/hello.cli
```

See [Running instacli files](instacli-spec/cli/Running%20Instacli%20files.spec.md) for more information on the `cli`
command.

## Explore

There are more examples in the **[samples](samples)** directory - check them out!

Explore them all with the command:

```shell ignore
cli samples
```

The following example will provide an interactive experience and connect to the Spotify API:

```shell ignore
cli samples/spotify
```

When connecting to Spotify for the first time, the script will ask you for your login credentials (App Client ID and
Client secret -- you should already have those). These will be stored in `~/.instacli/credentials.yaml` and will be used
for subsequent invocations.

# Highlight Reel

Instacli has two main ideas:

1. Everything is Yaml.
2. Keep it simple.

## Hello world

This is the simplest Instacli progam, **[hello.cli](samples/hello.cli)**:

```yaml file=hello.cli
Print: Hello from Instacli!
```

Invoke it with

```shell cli
cli hello.cli
```

And it will print the expected message:

```output
Hello from Instacli!
```

## HTTP requests as code

Tired of remembering the exact curl syntax or forgetting which tab had that request that worked in Postman?

Simply write your **[GET](instacli-spec/commands/instacli/http/GET.spec.md)** request as-code with Instacli:

```yaml instacli
GET: http://localhost:2525/greetings
```

Here's a **[POST](instacli-spec/commands/instacli/http/POST.spec.md)**:

```yaml instacli
POST:
  url: http://localhost:2525
  path: /greeting
  body:
    name: Hes
    language: Dutch
```

## Define input

Define all command-line options in Yaml. Take this file `simple-options.cli`

```yaml file=simple-options.cli
Script info:
  description: Call Acme
  input:
    user: Username
    language: Preferred language
```

This will automatically generate a command description and command line options:

```shell cli
cli --help simple-options.cli
```

```output
Call Acme

Options:
  --user       Username
  --language   Preferred language
```

## Input options

Instacli allows you to specify the type and format
of [input properties](instacli-spec/commands/instacli/user-interaction/Prompt.spec.md#prompt-properties). Here's an
example file `input-options.cli`

```yaml file=input-options.cli
Script info:
  description: Different input options
  input:
    user:
      description: Username
      short option: u
    password:
      description: Password
      secret: true
      short option: p
```

```shell cli
cli --help input-options.cli
```

```output
Different input options

Options:
  --user, -u   Username
  --password, -p   Password
```

## Interactive mode

By default, Instacli runs in interactive mode. If there are unknown commandline options, the user is prompted to give
input.

<!-- answers
Username: Hes
Password: Secret
-->

```shell cli
cli input-options.cli
```

```output
? Username Hes
? Password ********
```

## Subcommand support

Easily provide subcommand support by organizing your cli files in directories.

For example, to run the greeting example from the **[samples](samples)** directory, you can write

```shell cli cd=.
cli samples basic greet
```

with output:

```
Hello, World!
```

You can interactively select which command to run.

```shell ignore
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

Use the `-q` option for non-interacivte mode

```shell cli cd=.
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

Easily construct [user prompts](instacli-spec/commands/instacli/user-interaction/Prompt.spec.md) with Instacli.

Here's an example of how to ask the user to pick something from a list, in a file called `prompt.cli`:

```yaml file=prompt.cli 
Prompt:
  description: Select a language
  enum:
    - English
    - Spanish
    - Dutch

Print:
  You selected: ${output}
```

You will be presented with an interactive selector when running it:

```shell cli
cli prompt.cli
```

<!-- answers
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

Define [variables](instacli-spec/language/Variables.spec.md) in `${...}` syntax and pick and choose content using the
path notation.

```yaml instacli
Code example: Define a variable

${var}:
  name: my variable
  content:
    a: one
    b: two

Print: ${var.content}
```

will print

```output
a: one
b: two
```

## The output variable

The result of a command is always stored in the variable `${output}`.

This makes it easy to pick up in a subsequent command

For example

```yaml instacli
Code example: Using the output variable

GET: http://localhost:2525/hello

Print: ${output}
```

Will print the output of the GET request:

```output
Hello from Instacli!
```

Some commands work directly with the output variable. This helps in having a more declarative and readable script. For
example, you don;t need to pass the `${output}` variable to the **[Expected output
](instacli-spec/commands/instacli/testing/Expected%20output.spec.md)** command.

```yaml instacli
Code example: Implicit output variable

GET: http://localhost:2525/hello

Expected output: Hello from Instacli!
```

If you are going to use the output variable later on, best practice is to assign it to a named variable using **[As
](instacli-spec/commands/instacli/variables/As.spec.md)**.

```yaml instacli
Code example: Assign output to a named variable

GET: http://localhost:2525/hello
As: ${result}

Print:
  The result of GET /hello was: ${result}
```

## Http Server

For quick API prototyping, Instacli will run
an [HTTP server](instacli-spec/commands/instacli/http/Http%20server.spec.md) for you. Define some endpoints and back
them by Instacli scripts:

```yaml instacli
Code example: Running an HTTP server

Http server:
  port: 2525
  endpoints:
    /hello-example:
      get:
        script:
          Output: Hello from Instacli!
```

Take a look at the [sample server](samples/http-server/sample-server/sample-server.cli) that serves all requests from
the Instacli documentation and test suite.

## If statement

Instacli supports various programming logic constructs, like 'if', 'repeat', 'for each'

This is what an **[If](instacli-spec/commands/instacli/control-flow/If.spec.md)** statement looks like:

```yaml instacli
Code example: If statement

If:
  item: this
  equals: that
  then:
    Print: I'm confused!
```

## For each

With **[For each](instacli-spec/commands/instacli/control-flow/For%20each.spec.md)** you can loop over collections and
do stuff.

```yaml instacli
Code example: For each statement

For each:
  ${name} in:
    - Alice
    - Bob
    - Carol
  Print: Hello ${name}!
```

Output:

```output
Hello Alice!
Hello Bob!
Hello Carol!
```

You can use **For each**
to [transform a list](instacli-spec/commands/instacli/control-flow/For%20each.spec.md#transform-a-list) into something
else, like the `map()` function in some programming languages.

```yaml instacli
Code example: For each to transform a list

For each:
  ${name} in:
    - Alice
    - Bob
    - Carol
  Output: Hello ${name}!

Expected output:
  - Hello Alice!
  - Hello Bob!
  - Hello Carol!
```

## Testing in Instacli

Writing tests in Instacli is straightforward:

```yaml instacli
Test case: A simple test case

Assert that:
  item: one
  in: [ one, two, three ]
```

In fact, all tests for the Instacli language and commands are written in Instacli itself and can be found in the
**[instacli-spec](instacli-spec)** directory, in the `tests` subfolders. For example, take a look at
the [tests for assertions](instacli-spec/commands/instacli/testing/tests/Assert%20tests.cli)

## Documenting Instacli

All documentation can be found in the **[instacli-spec](instacli-spec)** directory.

Instacli documentation is in Markdown and contains runnable code that is run as part of the test suite.

Here's an example of Instacli documentation:

    ## Code examples
    
    The following code prints a message:
    
    ```yaml instacli
    Print: Hello from Instacli!
    ```

You can do 'Spec-driven development' with Instacli. For new features, write the documentation first, then run it. Since
you haven't implemented anything yet, the test suite will fail. Then write the implementation, and once the tests are
green, you're done!

