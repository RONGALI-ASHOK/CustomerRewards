-> Rewards Application

A Spring Boot application for managing and calculating customer reward points based on purchase history. Supports RESTful APIs, async processing, and secure endpoints.

-> Features

Calculate reward points for customers over a configurable period.

RESTful API endpoints with validation.

Secure endpoints (Spring Security).

Data persistence with JPA (H2).

Unit tests with JUnit and Mockito.

-> Tech Stack

Java 21

Spring Boot 4.x

Spring Data JPA

Spring Web MVC

ModelMapper

H2 In memory database

JUnit 5, Mockito, WebMVC

Lombok

-> API Endpoints

Base URLs

http://localhost:8080/rewards

This application has 1 endpoint.

1\. GET http://localhost:8080/rewards/points

&nbsp; Required Parameters - customerId

&nbsp; Optional Parameters - noOfMonths, fromDate, toDate

&nbsp; Different Scenarios

&nbsp;

&nbsp; Scenario - 1

&nbsp; When only customerId is given.

&nbsp; Request :

&nbsp; GET http://localhost:8080/rewards/points?customerId=1

&nbsp; Response :

{

"customerDetails": {

"customerId": 1,

        "name": "Ashok Rongali",

        "emailId": "ashok.rongali@gmail.com"

    },

    "monthlyRewardPoints": [

        {

            "year": 2026,

            "month": "January",

            "points": 276

        },

        {

            "year": 2026,

            "month": "February",

            "points": 16

        },

        {

            "year": 2026,

            "month": "March",

            "points": 242

        }

    ],

    "totalRewardPoints": 534,

    "customerPurchaseDetails": [

        {

            "dateOfPurchase": "2026-01-05",

            "amount": 145.00

        },

       {

           "dateOfPurchase": "2026-01-15",

            "amount": 92.25

        },

        {

            "dateOfPurchase": "2026-01-25",

            "amount": 121.50

        },

        {

            "dateOfPurchase": "2026-02-04",

            "amount": 66.45

        },

        {

            "dateOfPurchase": "2026-03-14",

            "amount": 125.40

        },

        {

            "dateOfPurchase": "2026-03-05",

            "amount": 145.75

        }

    ]

}

&nbsp; Scenario - 2

&nbsp; when customerId and number of months given. It will calculate todays date and calculate the points before the noOfMonths.

&nbsp; Request :

&nbsp; GET http://localhost:8080/rewards/points?customerId=1&noOfMonths=1

&nbsp; Response :

{

    "customerDetails": {

        "customerId": 1,

        "name": "Ashok Rongali",

        "emailId": "ashok.rongali@gmail.com"

    },

    "monthlyRewardPoints": [

        {

            "year": 2026,

            "month": "March",

            "points": 242

        }

    ],

    "totalRewardPoints": 242,

    "customerPurchaseDetails": [

        {

            "dateOfPurchase": "2026-03-14",

            "amount": 125.40

        },

        {

            "dateOfPurchase": "2026-03-05",

            "amount": 145.75

        }

    ]

}

&nbsp;

&nbsp; Scenario - 3

&nbsp; When fromDate and toDate is given.

&nbsp; Request :

&nbsp; GET http://localhost:8080/rewards/points?customerId=1\&fromDate=2026-01-19\&toDate=2026-03-19

&nbsp; Response :

{

    "customerDetails": {

        "customerId": 1,

        "name": "Ashok Rongali",

        "emailId": "ashok.rongali@gmail.com"

    },

    "monthlyRewardPoints": [

        {

            "year": 2026,

            "month": "January",

            "points": 94

        },

        {

            "year": 2026,

            "month": "February",

            "points": 16

        },

        {

            "year": 2026,

            "month": "March",

            "points": 242
        }

    ],

    "totalRewardPoints": 352,

    "customerPurchaseDetails": [

        {

            "dateOfPurchase": "2026-01-25",

            "amount": 121.50

        },

        {

            "dateOfPurchase": "2026-02-04",

            "amount": 66.45

        },

        {

            "dateOfPurchase": "2026-03-14",

            "amount": 125.40

        },

        {

            "dateOfPurchase": "2026-03-05",

            "amount": 145.75

        }

    ]

}

&nbsp; Testing

Developed Testing using Junit, Mockito and WebMvc
