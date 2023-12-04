# Command: Http endpoint

`Http endpoint` sets the default parameters for all subsequent HTTP commands like [GET](GET.md), [POST](POST.md), etc.

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
Code example: Http endpoint usage

Http endpoint:
  url: http://localhost:25125
  username: admin
  password:
    admin:

GET: /items

Expected output: [ '1', '2', '3' ]
```

## Http endpoint options

All parameters for **Http endpoints** are available for all other Http commands (if applicable)

### Url

The target host server.

```yaml
Code example: Http endpoint with url

Http endpoint:
  url: http://localhost:25125

GET: /items
```

### Path

The endpoint path.

```yaml
Code example: Http endpoint with url and path

Http endpoint:
  url: http://localhost:25125
  path: /items

POST:
  body: [ 1, 2, 3 ]
```

### Body

The (JSON) body

```yaml
Code example: Http endpoint with body

Http endpoint:
  url: http://localhost:25125
  body: [ 1, 2, 3 ]

POST:
  path: /items
```

## Username and password

When using the **username** and **password** properties, Basic Authentication will be used to authenticate against the endpoint

```yaml
Code example: Basic authentication

Http endpoint:
  url: http://localhost:25125
  username: admin
  password: admin

GET: /items
```

## Custom headers

Set headers with the **headers** property

```yaml
Code example: Custom headers

Http endpoint:
  url: http://localhost:25125
  headers:
    Authentication: Bearer XYZ
    X-Custom-Header: XYZ

GET: /items
```

## Save the result to a file

You can save the result to a file with **save as**

```yaml
Code example: Save file

Http endpoint:
  url: http://localhost:25125
  save as: out/items.json

GET: /items
```
