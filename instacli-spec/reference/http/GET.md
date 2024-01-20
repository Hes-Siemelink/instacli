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

```yaml
Code example: Simple GET

GET: http://localhost:25125/items

Expected output: [ '1', '2', '3' ]
```

## Split host and path

You can split the target host and path by using the `url` and `path` properties.

```yaml
Code example: Split url and path

GET:
  url: http://localhost:25125
  path: /items

Expected output: [ '1', '2', '3' ]
```

## Using Http request defaults

By using [Http request defaults](Http%20request%20defaults.md), you can set the properties that are common to all
requests in advance

```yaml
Code example: Use Http request defaults

Http request defaults:
  url: http://localhost:25125

GET:
  path: /items
```

This way you can simply write the `path` parameter on **GET**:

```yaml
Code example: Use Http Endpoint with simple GET

Http request defaults:
  url: http://localhost:25125

GET: /items
```

## Basic authentication

When using the **username** and **password** properties, Basic Authentication will be used to authenticate against the
endpoint

```yaml
Code example: Basic authentication

GET:
  url: http://localhost:25125/items
  username: admin
  password: admin
```

## Custom headers

Set headers with the **headers** property

```yaml
Code example: Custom headers

GET:
  url: http://localhost:25125/items
  headers:
    Authentication: Bearer XYZ
    X-Custom-Header: XYZ
```

## Save the result to a file

You can save the result to a file with **save as**

```yaml
Code example: Save file

GET:
  url: http://localhost:25125/items
  save as: out/items.json
```
