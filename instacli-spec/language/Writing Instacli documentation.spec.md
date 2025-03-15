# Writing documentation for Instacli

Documentation for Instacli is written in Markdown. You can add code examples to the document that the instacli build
will run as unit tests. This way you can make sure that the code examples are valid.

Note that this document serves as a reference document for testing, so the all examples in _this_ document are also
tested automatically. In order for that to work, you will see examples twice: first how they are written in Markdown,
and then how they will show up in the document. The instacli build will pick up the second one and test it.

## Code examples

You can define Instacli code examples with the **\`\`\`yaml instacli** markdown construct.

    ```yaml instacli
    Code example: An Instacli snippet inside Markdown
    
    Print: Hello from Instacli!
    ```

This will show as:

---

```yaml instacli
Code example: An Instacli snippet inside Markdown

Print: Hello from Instacli!
```

---

## Hidden setup code

Sometimes a code example could become can become cluttered with setup code.

For example, consider an interactive example. In order for the automated tests to run, we need to provide a stock
answer:

```yaml instacli
Code example: Example with setup code

Stock answers:
  What is your name?: Alice

Prompt: What is your name?
As: ${name}

Print: Hello, ${name}!
```

When reading the example, a reader may be distracted by the **Stock answers** bit. That is not what this code example is
about. It would be great if could hide it in someway.

You can do so by putting the code that we need but don't want to show in an HTML comment that is marked as **yaml
instacli before**

    <!-- yaml instacli before
    Stock answers:
    What is your name?: Alice
    -->

    ```yaml instacli
    Code example: Example without setup code

    Prompt: What is your name?
    As: ${name}
    
    Print: Hello, ${name}!
    ```

Now the example looks a lot cleaner:

---

<!-- yaml instacli before
Stock answers:
    What is your name?: Alice
-->

```yaml instacli
Code example: Example without setup code

Prompt: What is your name?
As: ${name}

Print: Hello, ${name}!
```

---

You can also provide hidden cleanup code with `<!-- yaml instacli after`. The yaml code will be appended to the last
code example defined by `yaml instacli`

## Files

Sometimes you need to have a helper file in order for the example to work. You can define a helper file with **```yaml
file:[filename]**

Here's an example:

    You can define the data in an external file `data.yaml`:

    ```yaml file:data.yaml
    key: value
    ```
    
    And then read it with **Read file**:

    ```yaml instacli
    Code example: Read from a file

    Read file:
        resource: data.yaml
    As: ${data}
    
    Print: Value is ${data.key}

This wil show as:

---

You can define the data in an external file `data.yaml`:

```yaml file:data.yaml
key: value
```

And then read it with **Read file**:

```yaml instacli
Code example: Read from a file

Read file:
  resource: data.yaml
As: ${data}

Print: Value is ${data.key} 
```

---

## Command line invocations

To show how to invoke the command line `cli` command, use the following syntax:

    ```commandline cli
    cli --help
    ```

Then followed by a block that contains the output:

    ```cli output
    Instacli -- Instantly create CLI applications with light scripting!
    
    Usage:
       cli [options] file | directory
    
    Options:
    ```

Provide input using an HTML comment marked as `cli input`. Inside the comment, define the answers to questions
like  [Stock answers](../commands/instacli/testing/Stock%20answers.md)

    <!-- cli input
    Enter your name: Hes
    Select a language: English
    -->

This will show as:

---

```commandline cli
cli --help
```

---

Followed by:

---

```cli output
Instacli -- Instantly create CLI applications with light scripting!

Usage:
   cli [global options] file | directory [command options]

Global options:
  --help, -h          Print help on a script or directory and does not run anything
  --output, -o        Print the output at the end of the script in Yaml format
  --output-json, -j   Print the output at the end of the script in Json format
  --non-interactive, -q   Indicate that Instacli should not prompt for user input
  --debug, -d         Run in debug mode. Prints stacktraces when an error occurs.
```

---