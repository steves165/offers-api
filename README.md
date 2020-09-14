# OffersApi

How to start the OffersApi application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/OffersApi-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:3000`

Assumptions
---

2. Only the RESTful web service was required and that a front end web page was not required as it was not defined on the spec.
2. Dropwizard biolerplate application code didn't need testing for this demo (Though normally I would as it would have a lot more custom code than this did in it)
2. That some example code would be needed so 4 example offers are added to system on startup.
2. An offer contains a description, active status, price, currency and expiry date only.

Usage guide
---

I have included a postman workspace in the folder `postman` in the root directory which contains an example of every call and response type I could think of for convience.

Below are sample calls in case you dont have postman:

These headers should be included for all requests `Accept: application/json, Content-Type: application/json`

#### Get all offers

`GET` - `http:"//localhost:3000/offers-api/offers`

#### Get an offer by ID

`GET` - `http:"//localhost:3000/offers-api/offers/{id}`

#### Create an offer

`POST` - `http:"//localhost:3000/offers-api/offers/create`

```
{
    "description": "Example",
    "active": true,
    "currency": "GBP",
    "price": 12,
    "expiry": "24/10/2020"
}
```

#### Cancel an offer by ID

`DELETE` - `http:"//localhost:3000/offers-api/offers/{id}`