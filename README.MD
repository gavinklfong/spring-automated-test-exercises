# Exercises of Automated Test Implementation

## Overview
This repository provides a sample Spring Boot Application for practicing the implementation of unit tests.

It is a insurance product quotation API, it exposes an endpoint:
- ``POST /quotations``: Generate new quotation 
- ``GET /quotations/{quotationCode}`` : Retrieve quotation by code 

![Application Component Diagram](https://github.com/gavinklfong/spring-automated-test-exercises/blob/main/blob/Product_Quotation_System.png?raw=true)

## Exercises
If you are about to start the exercise, then checkout **exercise** branch.

- **exercise** branch - contain skeleton of test codes for exercises
- **main** branch - contain completed set of unit test codes

## Build

Run this command to build and run all automated tests

`mvn clean install`

## Run

### External APIs
This system depends on external APIs - Customer API and Product API. To run this system on local machine for testing, you need to start up json-server which simulate the response of Customer API and Product API.

Run this command to start up the json server:

````
json-server --watch ./json-server/data.json
````
If your local machine does not have json-server, then run this command for installation:
```
npm --install -g json-server
```

### Database
By default, it connects to H2 in-memory database. 

### SpringBoot Application
Finally, run this command to launch the SpringBoot application:
```
mvn spring-boot:run
```

## Test It Manually

Once you have the system up and running, run curl command to verify its healthiness 

### Get quotations
Sample quotation records are inserted into database by default during the startup.
```
curl --location --request \
GET 'http://localhost:8102/quotations/e6766e0c-df53-4ea8-bede-dbe3b4006798'
```

### Generate a new quotation
Run this curl command to generate a new quotation record:
```
curl --location --request POST 'http://localhost:8102/quotations' \
--header 'Content-Type: application/json' \
--data-raw '{
    "productCode": "HOME-001",
    "customerId": 1,
    "postCode": "ABC"
}'
```