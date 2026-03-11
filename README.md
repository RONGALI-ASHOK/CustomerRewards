### Rewards Application

A Spring Boot application for managing and calculating customer reward points based on purchase history. Supports RESTful APIs, async processing, and secure endpoints.

#### Features  

Calculate reward points for customers over a configurable period.  
Retrieve purchase details asynchronously.  
RESTful API endpoints with validation.  
Secure endpoints (Spring Security).  
Data persistence with JPA (H2).  
Unit tests with JUnit and Mockito.  
Async processing with @EnableAsync.  


#### Tech Stack

Java 21  
Spring Boot 4.x  
Spring Data JPA  
Spring Web MVC   
ModelMapper  
H2 In memory database  
JUnit 5, Mockito, WebMVC  
Lombok  

#### API Endpoints  
#### Base URLs
http://localhost:8080/rewards  

This application has 2 endpoints which will perform two different tasks  
1. GET http://localhost:8080/rewards/points  
   Required Parameters  -  customerId  
   Optional Parameters  -  noOfMonths, fromDate, toDate  
   Different Scenarios
   
   Scenario - 1
   When only customerId  is given.    
   Request :  
    GET http://localhost:8080/rewards/points?customerId=1  
   Response :  
     {
    "rewardPointsDTO": {  
        "customerId": 1,    
        "name": "Ashok Rongali",  
        "emailId": "ashok.rongali@gmail.com",  
        "totalPoints": "Total Reward Points : 430",  
        "monthlyPoints": {  
            "January": 274,  
            "February": 16,  
            "March": 140  
        }  
    },  
    "customerDetailsDTO": [  
        {  
            "serialNumber": "2",  
            "customerId": 1,  
            "name": "Ashok Rongali",  
            "emailId": "ashok.rongali@gmail.com",  
            "dateOfPurchase": "2026-01-05",  
            "amount": 145  
        },  
        {  
            "serialNumber": "12",  
            "customerId": 1,  
            "name": "Ashok Rongali",  
            "emailId": "ashok.rongali@gmail.com",  
            "dateOfPurchase": "2026-01-15",  
            "amount": 92  
        },  
        {  
            "serialNumber": "22",  
            "customerId": 1,   
            "name": "Ashok Rongali",  
            "emailId": "ashok.rongali@gmail.com",  
            "dateOfPurchase": "2026-01-25",  
            "amount": 121  
        },
        {
            "serialNumber": "32",  
            "customerId": 1,  
            "name": "Ashok Rongali",  
            "emailId": "ashok.rongali@gmail.com",  
            "dateOfPurchase": "2026-02-04",  
            "amount": 66   
        },  
        {    
            "serialNumber": "52",  
            "customerId": 1,  
            "name": "Ashok Rongali",  
            "emailId": "ashok.rongali@gmail.com",   
            "dateOfPurchase": "2026-03-05",  
            "amount": 145  
        }  
    ]  
}  
    
   Scenario - 2    
   when customerId and number of months given. It will calculate todays date and calculate the points before the noOfMonths.  
   Request :  
     GET http://localhost:8080/rewards/points?customerId=1&noOfMonths=1    
   Response :  
     {  
    "rewardPointsDTO": {  
        "customerId": 1,  
        "name": "Ashok Rongali",  
        "emailId": "ashok.rongali@gmail.com",  
        "totalPoints": "Total Reward Points : 140",  
        "monthlyPoints": {  
            "March": 140  
        }  
    },  
    "customerDetailsDTO": [  
        {  
            "serialNumber": "52",  
            "customerId": 1,  
            "name": "Ashok Rongali",  
            "emailId": "ashok.rongali@gmail.com",  
            "dateOfPurchase": "2026-03-05",  
            "amount": 145  
        }  
    ]    
}  
   
   Scenario - 3  
      When fromDate and toDate is given.  
   Request :  
     GET http://localhost:8080/rewards/points?customerId=1&fromDate=2026-01-11&toDate=2026-02-11  
   Response :  
     {   
    "rewardPointsDTO": {   
        "customerId": 1,  
        "name": "Ashok Rongali",  
        "emailId": "ashok.rongali@gmail.com",  
        "totalPoints": "Total Reward Points : 150",  
        "monthlyPoints": {  
            "January": 134,  
            "February": 16  
        }  
    },  
    "customerDetailsDTO": [  
        {  
            "serialNumber": "12",  
            "customerId": 1,  
            "name": "Ashok Rongali",  
            "emailId": "ashok.rongali@gmail.com",  
            "dateOfPurchase": "2026-01-15",  
            "amount": 92  
        },  
        {  
            "serialNumber": "22",  
            "customerId": 1,  
            "name": "Ashok Rongali",  
            "emailId": "ashok.rongali@gmail.com",  
            "dateOfPurchase": "2026-01-25",  
            "amount": 121  
        },  
        {  
            "serialNumber": "32",  
            "customerId": 1,   
            "name": "Ashok Rongali",  
            "emailId": "ashok.rongali@gmail.com",  
            "dateOfPurchase": "2026-02-04",  
            "amount": 66  
        }  
    ]  
}   
  
2. GET http://localhost:8080/rewards/purchase-details  
   Retrieve purchase details asynchronously.  
   Parameter: customerId  
   Request :  
     GET http://localhost:8080/rewards/purchase-details?customerId    
   Response :  
     [  
    {  
        "amount": 145  
        "dateOfPurchase": "2025-12-05",  
        "emailId": "ashok.rongali@gmail.com",  
        "name": "Ashok Rongali",  
        "serialNumber": "1"  
    },  
    {  
        "amount": 145,  
        "dateOfPurchase": "2026-01-05",  
        "emailId": "ashok.rongali@gmail.com",  
        "name": "Ashok Rongali",  
        "serialNumber": "2"  
    },  
    {  
        "amount": 92,  
        "dateOfPurchase": "2026-01-15",  
        "emailId": "ashok.rongali@gmail.com",  
        "name": "Ashok Rongali",  
        "serialNumber": "12"  
    },  
    {  
        "amount": 121,  
        "dateOfPurchase": "2026-01-25",  
        "emailId": "ashok.rongali@gmail.com",  
        "name": "Ashok Rongali",  
        "serialNumber": "22"  
    },  
    {  
        "amount": 66,  
        "dateOfPurchase": "2026-02-04",  
        "emailId": "ashok.rongali@gmail.com",  
        "name": "Ashok Rongali",  
        "serialNumber": "32"  
    },  
    {  
        "amount": 125,  
        "dateOfPurchase": "2026-03-14",  
        "emailId": "ashok.rongali@gmail.com",  
        "name": "Ashok Rongali",  
        "serialNumber": "42"  
    },  
    {  
        "amount": 145,  
        "dateOfPurchase": "2026-03-05",  
        "emailId": "ashok.rongali@gmail.com",  
        "name": "Ashok Rongali",  
        "serialNumber": "52"  
    }  
]  
     

#### Testing

Developed Testing using Junit, Mockito and WebMvc    

