# Markdown directives tests

Defining the behavior of the little helpers that make it easier to write and execute Instacli script in Markdown.

For example, providing input and checking output of scripts.

## Using `input` for Yaml snippets

## Testing the ouput of Instacli script snippets

You can test the output of an Instacli script by using the `Expected console output` directive.

```yaml instacli
Code example: Expected console output

Print: Hello, World!

Expected console output: Hello, World!
```

Sometimes it is nicer to separate script and expected output so you can write something like

```yaml instacli
Code example: Printing something, checking later

Print: More interesting output
```

This will print

```output
More interesting output
```

In markdown, you tell Instacli to check the output of the last command by using the ` ```output` directive.

    ```output
    More interesting output
    ```

```yaml instacli