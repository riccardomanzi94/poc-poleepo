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

- dev: connects to MySql db (for local developments)
- staging: connects to MySql db
- main: connects to MySql db of the environment Pro


### How to start docker

You can start via Docker all the external components required by microservice simply

`docker-compose -f docker/poleepo/compose.yml up -d`

## Swagger information

The Swagger documentation is available at the following folder : ./swagger
