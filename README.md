## API Print Service

A simple API to save API exchange and render as HTML.

### API Description

It consists of two endpoints.

#### Save API Print
Saves the `base64` encoded document and returns the location to retrieve the HTML.

##### Request
```
POST /prints
```
*Headers*
```
Content-Type: application/json
```
*Body*
```
{
    "document": "IyMjIEhlYWRpbmc="
}

```    

##### Response
```
HTTP 201
```

*Headers*
```
location: http://localhost:8080/prints/3f7d252e-5e4f-4232-ac48-e67b08ffffc0
```

*Sample Call*
```
curl -X 'POST' -H 'Content-Type: application/json' -d '{"document": "IyMjIEhlYWRpbmc="}' 'http://127.0.0.1:8080/prints'
``` 

#### Get API Print

Renders the print as HTML.

##### Request
```
GET /prints/3f7d252e-5e4f-4232-ac48-e67b08ffffc0
```
*Headers*
```
Accept: */*
```

##### Response
```
HTTP 200
```

*Headers*
```
content-type: text/html
```

*Body*
```
<!DOCTYPE html>
<html>
<head>
    <title>Deskriders :: API Print</title>
</head>
<body>
<div><h3>Heading</h3>
</div>
<footer>
    Layout footer
</footer>
</body>
</html>

```

*Sample Call*
```
curl -X 'GET' -H 'Accept: */*' 'http://localhost:8080/prints/3f7d252e-5e4f-4232-ac48-e67b08ffffc0'
``` 

### Development

```
git clone ...
```

To run application

```
./gradlew run
```

To run tests

```
./gradlew test
```
