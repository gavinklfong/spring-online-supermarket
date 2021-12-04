# SpringBoot API for Online Supermarket

## Introduction
This repository contains the source code of Webflux SpringBoot application for the demonstration of integration with Elasticsearch.

## Use Cases
There are 3 API endpoints for product queries:
* **List products by category** - `GET /products/by-category?category=<category>`
* **Search products by keyword** - `GET /products/by-keyword?keyword=<keyword>`
* **Search relevant products** - `GET /products/{productId}/relevant-products`

![Use Case Diagram](https://github.com/gavinklfong/spring-online-supermarket/blob/master/blob/Use_Case.png?raw=true)

## System Overview

The system structure is based on the typical `Controller-Service-Repository` design pattern.

Repository makes use of `ReactiveElasticsearchTemplate` for the access to Elasticsearch

![Application Component Diagram](https://github.com/gavinklfong/spring-online-supermarket/blob/master/blob/System_Overview.png?raw=true)

## Dependency

Docker environment is required as disposable Elasticsearch docker container is created during the automated test execution.

## Elasticsearch Docker Container

This application needs to connect to Elasticsearch. Run this command to start up Elasticsearch in docker container:

```
cd docker-compose
docker compose up
```

Then, import sample product data into Elasticsearch

```
run-logstash.sh
```

## Build & Run

Use maven command to build the source and run automated tests

```
mvn clean test
```

Finally, run this command to start the SpringBoot application:

```
mvn spring-boot:run
```
