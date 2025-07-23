# Poc-Poleepo

# Build

> **Prerequisites**:
>
> - install **jdk21** on your pc and correctly set JAVA_HOME env var to the root java folder

Then you can build using the integrated maven wrapper, execute from root code folder:

`mvn clean install -Pdev` (for local developments)

## Start

### Runtime Spring Profile

Project is configured used the following default profiles

- local: starts with embedded DB (H2) on docker compose, you need to first start the include docker-compose file (see next section, "How to start docker")
- dev: connects to H2 db (for local developments)
- staging: connects to Oracle DB and Kafka Server of the environment Staging
- main: connects to H2 DB of the environment Pro


### How to start docker

You can start via Docker all the external components required by microservice simply

`docker-compose -f docker/poleepo/compose.yml up -d`

### How to connect to db h2

To connect to db h2 there are the following methods:

- Go to http://localhost:8080/h2-console and login

## Swaager information

The Swagger documentation is available at the following folder : ./swagger
