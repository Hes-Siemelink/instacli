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

```yaml
Code example: Http request defaults usage

Http request defaults:
  url: http://localhost:25125
  username: admin
  password: admin

GET: /items

Expected output: [ '1', '2', '3' ]
```

## Options

All parameters for **Http request defaults** are available for all other Http commands (if applicable)

### Url

The target host server.

```yaml
Code example: Default url

Http request defaults:
  url: http://localhost:25125

GET: /items
```

### Path

The endpoint path.

```yaml
Code example: Default url and path

Http request defaults:
  url: http://localhost:25125
  path: /items

POST:
  body: [ 1, 2, 3 ]
```

### Body

The (JSON) body

```yaml
Code example: Default body

Http request defaults:
  url: http://localhost:25125
  body: [ 1, 2, 3 ]

POST:
  path: /items
```

## Username and password

When using the **username** and **password** properties, Basic Authentication will be used to authenticate against the
endpoint

```yaml
Code example: Basic authentication

Http request defaults:
  url: http://localhost:25125
  username: admin
  password: admin

GET: /items
```

## Custom headers

Set headers with the **headers** property

```yaml
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

```yaml
Code example: Save file

Http request defaults:
  url: http://localhost:25125
  save as: out/items.json

GET: /items
```
