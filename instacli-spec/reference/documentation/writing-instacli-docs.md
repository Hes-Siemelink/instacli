# Writing documentation for Instacli

Documentation for Instacli is written in Markdown. You can add code examples to the document that the instacli build
will run as unit tests. This way you can make sure that the the code examples are valid.

Note that this document serves as a reference document for testing, so the all examples in _this_ document are also
tested automatically. In order for that to work, you will see examples twice: first how they are written in Markdown,
and then how they will show up in the document. The instacli build will pick up the second one and test it.

## Code examples

You can define Instacli code examples with the **\`\`\`yaml** markdown construct.

    ```yaml cli
    Code example: An Instacli snippet inside Markdown
    
    Print: Hello from Instacli!
    ```

This will show as:

---

```yaml cli
Code example: An Instacli snippet inside Markdown

Print: Hello from Instacli!
```

---

## Run before

Sometimes a code example could become can become cluttered with setup code.

For example, consider an interactive example. In order for the automated tests to run, we need to provide a stock
answer:

```yaml cli
Code example: Example with setup code

Stock answers:
  What is your name?: Alice

Prompt: What is your name?
As: name

Print: Hello, ${name}!
```

When reading the example, a reader may be distracted by the **Stock answers** bit. That is not what this code example is
about. It would be great if could hide it in someway.

You can do so by putting the code that we need but don't want to show in an HTML comment that is marked as **run before
**

    <!-- run before
    Stock answers:
    What is your name?: Alice
    -->

    ```yaml cli
    Code example: Example without setup code

    Prompt: What is your name?
    As: name
    
    Print: Hello, ${name}!
    ```

Now the example looks a lot cleaner:

---

<!-- run before
Stock answers:
    What is your name?: Alice
-->

```yaml cli
Code example: Example without setup code

Prompt: What is your name?
As: name

Print: Hello, ${name}!
```

---

## Files

Sometimes you need to have a helper file in order for the example to work. You can define a helper file with **```yaml
cli file:[filename]**

Here's an example:

    You can define the data in an external file `data.yaml`:

    ```yaml file:data.yaml
    key: value
    ```
    
    And then read it with **Read file**:

    ```yaml cli
    Code example: Read from a file

    Read file:
        relative: data.yaml
    As: data
    
    Print: Value is ${data.key}

This wil show as:

---

You can define the data in an external file `data.yaml`:

```yaml file:data.yaml
key: value
```

And then read it with **Read file**:

```yaml cli
Code example: Read from a file

Read file:
  relative: data.yaml
As: data

Print: Value is ${data.key} 
```

---

## Command line invocations

To show how to invoke the command line `cli` command, use the following syntax:

```commandline cli
cli --help
```

Then followed by a block that contains the output:

```output
Instacli -- Instantly create CLI applications with light scripting!

Usage:
   cli [-q] [--help] file | directory
```