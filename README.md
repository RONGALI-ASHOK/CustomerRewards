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
JUnit 5, Mockito    
Lombok  

#### API Endpoints  
#### Base URLs
http://localhost:8080/rewards  

This application has 2 endpoints which will perform two different tasks  
1. GET http://localhost:8080/rewards/points  
   Required Parameters  -  emailId  
   Optional Parameters  -  noOfMonths, fromDate, toDate  
   Different Scenarios
   
   Scenario - 1
   When only email is given.    
   Request :  
    GET http://localhost:8080/rewards/points?emailId=ashok.rongali@gmail.com  
   Response :  
     ashok.rongali@gmail.com  
     January: 274 points  
     February: 16 points  
     March: 140 points  
     Total: 430 points
    
   Scenario - 2   
   when email and number of months given. It will calculate todays date and calculate the points before the noOfMonths.  
   Request :  
     GET http://localhost:8080/rewards/points?emailId=ashok.rongali@gmail.com&noOfMonths=2  
   Response :  
     ashok.rongali@gmail.com    
     January: 134 points  
     February: 16 points  
     March: 140 points  
     Total: 290 points
   
   Scenario - 3  
      When fromDate and toDate is given.  
   Request :  
     GET http://localhost:8080/rewards/points?emailId=ashok.rongali@gmail.com&fromDate=2026-01-28&toDate=2026-03-05
   Response :  
     ashok.rongali@gmail.com  
     February: 16 points  
     March: 140 points  
     Total: 156 points  
  
2. GET http://localhost:8080/rewards/purchase-details  
   Retrieve purchase details asynchronously.  
   Parameter: emailId  
   Request :  
     GET http://localhost:8080/rewards/purchase-details?emailId=ashok.rongali@gmail.com  
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

Developed Testing using Junit and Mockito  
