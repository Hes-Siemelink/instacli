# Digital.ai Platform Login

This is the script to connect to Digital.ai Platform.

```yaml instacli
Script info: Logs in to Digital.ai Platform
```

First, we check if we are already logged in. If we are, we exit the script.

```yaml instacli
If:
  not:
    empty:
      :Http request defaults: { }
  then:
    Exit: Already logged in
```

Then we get the credentials for the Digital.ai Platform that were stored previously.

```yaml instacli
Get credentials: Digital.ai Platform
As: ${endpoint}
```

If no connection credentials are set, we create a new account. This action will ask the user for credentials.

```yaml instacli
If:
  empty: ${endpoint}
  then:
    Create new account: { }
    As: ${endpoint}

Print: Connecting as ${endpoint.username} to ${endpoint.id} on ${endpoint.url}
```

Finally, we need to set the token for the connection. If the token is already set, we store it in the Http defaults for
this session.

```yaml instacli
If:
  not:
    empty: ${endpoint.token}
  then:
    Http request defaults:
      url: ${endpoint.url}
      headers:
        Authorization: Token ${endpoint.token}
    Exit: Using token authentication
```

If we don't have a token, we need to get a Bearer token. We do this by sending a POST request to the Digital.ai Platform
with the credentials we have. This is done in the Get token script that we call from here. Once we have the token, we
can store it in the request defaults for this session.

```yaml instacli
Get token: ${endpoint}

Http request defaults:
  url: ${endpoint.url}
  headers:
    Authorization: Bearer ${output.access_token}
```