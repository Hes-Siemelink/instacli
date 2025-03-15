# Command: Code example

`Code example` marks the start of example code in a Markdown document.

| Content type | Supported |
|--------------|-----------|
| Value        | yes       |
| List         | no        |
| Object       | no        |

[Code example.schema.yaml](schema/Code%20example.schema.yaml)

## Basic usage

With **Code example** you give a name to snippet of code inside a Markdown document. This can be picked up by the test
runner to validate the code that is being described.

This is what it looks like when writing Markdown:

> This is a Markdown document describing how to run a simple Instacli command
>
> \```yaml instacli
> Code example: Hello world
>
> Print: Hello world!  
> \```

And it will render like this in the documentation:

```yaml instacli
Code example: Hello world

Print: Hello world!
```