# Transactions

## Requirements

- Java 8
- Maven

## Running

```
mvn spring-boot:run
```

## API

### Create transaction:

**Endpoint:** `POST /transactions`

**Parameters:**

| Parameter   | Description                                                |
|-------------|------------------------------------------------------------|
| `amount`    | Transaction amount                                         |
| `timestamp` | Transaction time in epoch in milliseconds in UTC time zone |

**Response:** empty

| Status | Description                              |
|--------|------------------------------------------|
| 200    | Transaction successfully created         |
| 204    | Transaction is older than metrics window |

**Example:**

```
$ curl -v -X POST -H 'Content-type: application/json' http://localhost:8080/transactions -d '{"amount":'$(($RANDOM % 100))'.0,"timestamp":'$(date +"%s000")'}'
*   Trying ::1...
* Connected to localhost (::1) port 8080 (#0)
> POST /transactions HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.43.0
> Accept: */*
> Content-type: application/json
> Content-Length: 41
>
* upload completely sent off: 41 out of 41 bytes
< HTTP/1.1 200
< Content-Length: 0
< Date: Sun, 13 Aug 2017 14:10:05 GMT
<
* Connection #0 to host localhost left intact
```

### Get statistics

**Endpoint:** `GET /statistics`

**Response:** metrics JSON

| Status | Description                                |
|--------|--------------------------------------------|
| 200    | Transaction metrics for the metrics window |

**Example:**

```
$ curl -v http://localhost:8080/statistics
*   Trying ::1...
* Connected to localhost (::1) port 8080 (#0)
> GET /statistics HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.43.0
> Accept: */*
>
< HTTP/1.1 200
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
< Date: Sun, 13 Aug 2017 14:12:24 GMT
<
* Connection #0 to host localhost left intact
{"sum":66367.0,"avg":49.306835066864785,"max":96.0,"min":18.0,"count":1346}
```

## Tests

You can run tests by executing:

```
mvn test
```

You can also run the integration tests by executing:

```
mvn test -Dspring.profiles.active=it
```

## Benchmark

There's a little script to generate an urls.txt file to run a benchmark against the `/transactions` endpoint using [siege](https://www.joedog.org/siege-home/):

```
./tools/gen_urls.sh > /tmp/urls.txt && siege -f /tmp/urls.txt --content-type 'application/json' -t30S -c200
```

On a different terminal you can at the same time also run siege against the `/statistics` endpoint:

```
siege -t30S -c200 http://localhost:8080/statistics
```
