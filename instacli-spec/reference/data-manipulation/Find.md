# Command: Find

`Find` retrieves a snippet of JSON from a larger object

| Content type | Supported               |
|--------------|-------------------------|
| Value        | no                      |
| List         | implicit                |
| Object       | yes                     |
| `path`       | the path to retrieve    |
| `in`         | the object to search in |

## Basic usage

Add some numbers

```yaml script
Code example: Find a snippet based on a variable path

${language}: English

Find:
  path: ${language}
  in:
    English: Hello
    Spanish: Hola
    Dutch: Hallo

Expected output: Hello
```

