# Two Microservices with Feign, Docker and PostgreSQL

| Service | Port | Role |
|---|---|---|
| client-service | 8081 | Public API, calls storage-service via OpenFeign |
| storage-service | 8082 | Internal service, stores data in PostgreSQL |

## Run
```
docker compose up --build
```

## Test
```
curl http://localhost:8081/api/client/hello
curl -X POST http://localhost:8081/api/client/items -H "Content-Type: application/json" -d '{"name":"  laptop ","description":"Gaming laptop"}'
curl http://localhost:8081/api/client/items
```
