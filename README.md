###Rewards Application

A Spring Boot application for managing and calculating customer reward points based on purchase history. Supports RESTful APIs, async processing, and secure endpoints.

####Features  

Calculate reward points for customers over a configurable period.  
Retrieve purchase details asynchronously.  
RESTful API endpoints with validation.  
Secure endpoints (Spring Security).  
Data persistence with JPA (H2).  
Unit tests with JUnit and Mockito.  
Async processing with @EnableAsync.  


####Tech Stack

Java 21  
Spring Boot 4.x  
Spring Data JPA  
Spring Web MVC   
ModelMapper  
H2 In memory database  
JUnit 5, Mockito    
Lombok  

####API Endpoints

GET /rewards/points  
Query reward points for a customer.  
Parameters: emailId, noOfMonths, fromDate, toDate  

GET /rewards/purchase-details  
Retrieve purchase details asynchronously.  
Parameter: emailId  

####Testing

Developed Testing using Junit and Mockito  
