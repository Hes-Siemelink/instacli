# Command: Http request defaults

`Http request defaults` sets the default parameters for all subsequent HTTP commands
like [GET](GET.md), [POST](POST.md), etc.

| Content type | Supported                         |
|--------------|-----------------------------------|
| Value        | no                                |
| List         | implicit                          |
| Object       | yes                               |
| url          | base URL of the request           |
| path         | path of the request               |
| body         | request body                      |
| username     | username for Basic authentication |
| password     | password for Basic authentication |
| headers      | request headers                   |
| saveAs       | save the result to a file         |

## Basic usage

Set the HTTP server and credentials, then issue a normal **GET**

```yaml cli
Code example: Http request defaults usage

Http request defaults:
  url: http://localhost:25125
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

```yaml cli
Code example: Default url

Http request defaults:
  url: http://localhost:25125

GET: /items
```

### Path

The endpoint path.

```yaml cli
Code example: Default url and path

Http request defaults:
  url: http://localhost:25125
  path: /items

POST:
  body:
    1: One
    2: Two
    3: Three
```

### Body

The (JSON) body

```yaml cli
Code example: Default body

Http request defaults:
  url: http://localhost:25125
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

```yaml cli
Code example: Basic authentication

Http request defaults:
  url: http://localhost:25125
  username: admin
  password: admin

GET: /items
```

## Custom headers

Set headers with the **headers** property

```yaml cli
Code example: Custom headers

Http request defaults:
  url: http://localhost:25125
  headers:
    Authentication: Bearer XYZ
    X-Custom-Header: XYZ

GET: /items
```

## Save the result to a file

You can save the result to a file with **save as**.

```yaml cli
Code example: Save file

Http request defaults:
  url: http://localhost:25125
  save as: out/items.json

GET: /items
```
