# money-transfer-sandbox

test task project

## Used technologies

- `scala`
- `sbt`
- [Play framework](https://www.playframework.com/)
- [Play-Slick](https://github.com/playframework/play-slick) + inmemory `H2` database
- [macwire](https://github.com/adamw/macwire) for dependency injection
- [Play scala test](http://www.scalatest.org/plus/play)
- [sbt-native-packager](https://www.scala-sbt.org/sbt-native-packager/)

## Useful sbt commands

- `sbt run` starts project in development mode 
- `sbt stage` stages app into `/target/universal/stage/` folder
- `sbt test` runs test

## Project structure

```
app
  controllers      # containg viewmodel + presentational logics
  model
    dao            # contains database access logic
    entity         # domain model value-classes
    service        # contains business logic
  module           # dependency injection
  util
  Loader.scala     # app entry point
conf
  application.conf # configuration
  logback.xml      # logger configuration
  routes           # routes mapping
test
  controllers
    RestSpec.scala # rest tests               
```

## Rest calls

### Get by id

`GET /api/account/:id`

Responses with 200 if found
```
{
    "data":
        {
            "id": "1",
            "balance": 0,
            "createdAt": 1538999758241,
            "updatedAt": 1538999773671
        }
}
```

otherwise responces with 404

### Get all

`GET http://localhost:9000/api/account?limit&offset`

Example response:

```
{
    "data": [
        {
            "id": "1",
            "balance": 0,
            "createdAt": 1538999758241,
            "updatedAt": 1538999773671
        },
        {
            "id": "2",
            "balance": 10,
            "createdAt": 1538999755462,
            "updatedAt": 1538999773671
        }
    ]
}
```

### Create account

`PUT http://localhost:9000/api/account/:id`

Responses with 201 or with 200 if already created

### Add money

```
POST http://localhost:9000/api/account/balance/add
{
    "id": "1",
    "amount": 10
}
```

Responses with 200 or with 404 if id not found

### Money Transfer

```
POST http://localhost:9000/api/moneytransfer
{
    "idFrom": "1",
    "idTo": "2",
    "amount": 10
}
```

Responses 
- 200 on success
- 404 if any of account is not found
- 400 if `From` account has insufficient money

#### Internal requests

`POST http://localhost:9000/maintenance/tables` 

Creates tables

`DELETE http://localhost:9000/maintenance/tables` 

Drops tables
