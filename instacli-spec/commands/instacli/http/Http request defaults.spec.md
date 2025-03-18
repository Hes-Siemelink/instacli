# Command: Http request defaults

`Http request defaults` sets the default parameters for all subsequent HTTP commands
like [GET](GET.spec.md), [POST](POST.spec.md), etc.

| Content type | Supported |
|--------------|-----------|
| Value        | no        |
| List         | implicit  |
| Object       | yes       |

[Http request defaults.schema.yaml](schema/Http%20request%20defaults.schema.yaml)

## Basic usage

Set the HTTP server and credentials, then issue a normal **GET**

```yaml instacli
Code example: Http request defaults usage

Http request defaults:
  url: http://localhost:2525
  username: admin
  password: admin

GET: /items

Expected output:
  - 1
  - 2
  - 3
```

## Options

All parameters for **Http request defaults** are available for all other Http commands (if applicable)

### Url

The target host server.

```yaml instacli
Code example: Default url

Http request defaults:
  url: http://localhost:2525

GET: /items
```

### Path

The endpoint path.

```yaml instacli
Code example: Default url and path

Http request defaults:
  url: http://localhost:2525
  path: /items

POST:
  body:
    1: One
    2: Two
    3: Three
```

### Body

The (JSON) body

```yaml instacli
Code example: Default body

Http request defaults:
  url: http://localhost:2525
  body:
    1: One
    2: Two
    3: Three

POST:
  path: /items
```

## Username and password

When using the **username** and **password** properties, Basic Authentication will be used to authenticate against the
endpoint

```yaml instacli
Code example: Basic authentication

Http request defaults:
  url: http://localhost:2525
  username: admin
  password: admin

GET: /items
```

## Custom headers

Set headers with the **headers** property

```yaml instacli
Code example: Custom headers

Http request defaults:
  url: http://localhost:2525
  headers:
    Authentication: Bearer XYZ
    X-Custom-Header: XYZ

GET: /items
```

## Save the result to a file

You can save the result to a file with **save as**.

```yaml instacli
Code example: Save file

Http request defaults:
  url: http://localhost:2525
  save as: out/items.json

GET: /items
```
