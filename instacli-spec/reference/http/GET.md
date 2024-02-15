# Command: GET

`GET` sends a GET request to an HTTP endpoint.

| Content type | Supported                         |
|--------------|-----------------------------------|
| Value        | yes                               |
| List         | implicit                          |
| Object       | yes                               |
| url          | base URL of the request           |
| path         | path of the request               |
| username     | username for Basic authentication |
| password     | password for Basic authentication |
| headers      | request headers                   |
| saveAs       | save the result to a file         |

## Basic usage

Use **GET** on a URL to get the results as JSON/YAML

```yaml script
Code example: Simple GET

GET: http://localhost:2525/items

Expected output:
  - 1
  - 2
  - 3
```

## Split host and path

You can split the target host and path by using the `url` and `path` properties.

```yaml script
Code example: Split url and path

GET:
  url: http://localhost:2525
  path: /items

Expected output:
  - 1
  - 2
  - 3
```

## Using Http request defaults

By using [Http request defaults](Http%20request%20defaults.md), you can set the properties that are common to all
requests in advance

```yaml script
Code example: Use Http request defaults

Http request defaults:
  url: http://localhost:2525

GET:
  path: /items
```

This way you can simply write the `path` parameter on **GET**:

```yaml script
Code example: Use Http Endpoint with simple GET

Http request defaults:
  url: http://localhost:2525

GET: /items
```

## Basic authentication

When using the **username** and **password** properties, Basic Authentication will be used to authenticate against the
endpoint

```yaml script
Code example: Basic authentication

GET:
  url: http://localhost:2525/items
  username: admin
  password: admin
```

## Custom headers

Set headers with the **headers** property

```yaml script
Code example: Custom headers

GET:
  url: http://localhost:2525/items
  headers:
    Authentication: Bearer XYZ
    X-Custom-Header: XYZ
```

## Save the result to a file

You can save the result to a file with **save as**

```yaml script
Code example: Save file

GET:
  url: http://localhost:2525/items
  save as: out/items.json
```
