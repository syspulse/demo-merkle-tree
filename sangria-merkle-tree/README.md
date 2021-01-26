## Simple MerkleTree GraphQL 

Based on sangria-akka-http: [GraphQL](https://graphql.org) server written with [akka-http](https://github.com/akka/akka-http), [circe](https://github.com/circe/circe) and [sangria](https://github.com/sangria-graphql/sangria).


### Build

```bash
sbt run

# or, if you want to watch the source code changes
 
sbt ~reStart
``` 

### Query

[graphql-playground](https://github.com/prisma/graphql-playground): [http://localhost:8080/playground](http://localhost:8080/playground) 

The HTTP endpoint follows [GraphQL best practices for handling the HTTP requests](http://graphql.org/learn/serving-over-http/#http-methods-headers-and-body).


### Curl Examples


```bash
$ curl -X POST localhost:8080/graphql \
  -H "Content-Type:application/json" \
  -d '{"query": "{nodes {data,name,type}}"}'
```


```bash
$ curl -X POST localhost:8080/graphql \
  -H "Content-Type:application/json" \
  -d '{"query": "{nodes(limit: 1,data: \"random\") {data}}"}'
```
