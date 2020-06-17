## Introduction
This application operates as a Java web server using Spring framework, and its main purpose is to view and analyze past
and current Twitter trends (provided by this [API](https://developer.twitter.com/en/docs/trends/locations-with-trending-topics/api-reference/get-trends-available)).  
The user can interrogate the server by using a simple frontend or by calling the rest APIs directly.  
Once the server is started, it will periodically save the daily trends in a json file, thus providing an archive of past 
daily trends to the user.

### Routes
Name | Description | Type
------------ | -------------  | ------------- 
/twittertrends/home | Shows the home webpage, with the current day trends in a table | HTML
/twittertrends/home?date=\[dateString]&filter=\[filterExpression] | Shows the home webpage, with the filtered trends by date and expression in a table | HTML
/twittertrends/api/trends | Returns all the trends of the current day | GET
/twittertrends/api/trends?date=\[dateString]&filter=\[filterExpression] | returns the trends filtered by date and expression | GET
/twittertrends/api/metadata | returns the metadata of the trends model | GET
##### Notes:
- The parameter \[dateString] has to be in format 'yyyy-MM-dd'
- If in the query string the date is not present, the returned trends will be of the current day.
- If in the query string the filter expression is not present, the returned trends will be unfiltered.

### Filters
Operator | Description |  Usage| Type
------------ | -------------  | ------------- | -------------  
$and | Is true only if all argument expressions are true | `$and: [<expression_1>, ..., <expression_n>]` | Logical 
$or | Is true if any argument expression is true | `$or: [<expression_1>, ..., <expression_n>]` | Logical 
$not | Negates an expression | `$not: [ <expression>]` | Logical 
$lt | <  | `<fieldName> : $lt: <value>` | Comparison
$lte | <= | `<fieldName> : $lte: <value>` | Comparison
= or : | = | `<fieldName> = <value>` or `<fieldName> : <value>` | Comparison
$gte | \>= | `<fieldName> : $gte: <value>` | Comparison
$gt | \> | `<fieldName> : $gt: <value>` | Comparison
$lt | < | `<fieldName> : $lt: <value>` | Comparison
$bt | Is true if the field value is between the arguments | `<fieldName> : $bt: [<lowerVal>, <upperVal>]` | Set
$in | Is true if the field is any of the arguments | `<fieldName> : $in: [<value_1>, ..., <value_n>]` | Set
$nin | Is true if the field is not any of the arguments | `<fieldName> : $nin: [<value_1>, ..., <value_n>]` | Set
. | Access a sub field in a field | `<fieldName>.<subFieldName>`|Pseudo-operator 
#### Notes:
- The filter expressions are case sensitive.
- The filter expressions can be separated by any amount of spaces.
- If a value contains spaces, it has to be delimited by quotation marks.
- The dot operator can access any depth of nested fields.
- The filter expression can contain curly brackets (like in a JSON format), but it's not necessary.
- The comparison operators work on strings too. If the comparator is a $gte or a $lte, it functions like an equal; if its a $lt or a $gt it functions like a not equal.
#### Filter expression examples 
1. `country = Italy` or `country : Italy`  
Filter trend by the location's country
2. `$and: [countryCode: $in: [IT, EN, US], placeType.name = Town]`  
Get trends of locations that are towns and have a country code IT or EN or US
3. `{"$or": {["country": {"$in": {["Italy", "France", "United Kingdom"]}, "placeType.code" = 12]}`  
Get trends of locations that are either in Italy, France or United Kingdom, or that have a place type code equal to 12 (which corresponds to the type 'Country').
Note that the query is in JSON format

### Frontend
The frontend design is very simple and barebone, without compromising on functionalities and ease of use.  
It's built using HTML and CSS as the view part, Thymeleaf as a very simple controller and the Spring server as the backend.  
The main functionalities are:
- Buttons for accessing the REST APIs (metadata, data)
- Table for displaying the filtered (or unfiltered) trends
- Text input field for inserting the filter expression
- A multiple selection field for choosing the desired date between the available ones
- In case of query error the relative error message will be shown
#### Screenshot
![Home screenshot](/readme_assets/frontend.png)

### Backend
Once the web server starts, a service for managing the data is started. 
Its features are:
- Once a day saves the current trends available on the Twitter's API in a folder
, thus providing an history of past daily trends.
- The trends are provided to the user via a cache (saved in memory) instead of being always retrieved on a file.  
This improves the response time at the cost of some memory (around 150-200 KB per daily trends collection).  
Normally implementing a safe and robust cache is difficult, but since in this project the user can only view data without adding
or modifying it, a simple caching method was implemented without hassles.

# Design and structure
### Use case diagram
![Use case diagram](/readme_assets/Use_case.png)

## UML Class diagram
#### Main package
![src package uml](/readme_assets/UML_Packages/src.png)
#### Models package
![models package uml](/readme_assets/UML_Packages/Models.png)
#### Filter package
![filter package uml](/readme_assets/UML_Packages/Filter.png)
#### All
![all classes uml](/readme_assets/UML_Packages/GLOBAL.png)

## Call sequence diagrams
#### Home page (HTML) `/home`
![homePage sequence](/readme_assets/UML_Sequences/homePage.png)
#### Home page filtered `/home?date=[dateString]&filter=[filterExpression]` (HTML)
![homePageFilter sequence](/readme_assets/UML_Sequences/homePageFilter.png)
#### Get Metadata `/api/metadata` (GET)
![metadata sequence](/readme_assets/UML_Sequences/getMetadata.png)
#### Get Trends `/api/trends` and Get Filtered Trends `/api/trends?date=[dateString]&filter=[filterExpression]` (GET)
The sequences for these REST calls are the same as the html correspectives, with the main difference that the data is 
returned in the HTTP response body as JSON instead of being formatted in the html page.  
Also in these calls, the returned object does not include the metadata and in case of an exception
the returned object is the exception message.

## Filter design

#### Operators structure
The filter operators can be divided in three separate categories: Comparison, Logical and Set operators.  
These categories differ both by type of operation and by number and position of arguments, but each one returns either
true or false:

Operator type | Left argument | Operations |Right argument 
------------ | -------------  | ------------- | ------------- 
Comparison | \<fieldName> | \<, \<=, =, >=, > | \<singleValue>
Set | \<fieldName> | IN, NIN, BT | [<value_1>, ..., <value_n>]
Logical | |AND, OR, NOT | [<operator_1>, ..., <operator_n>] 

Note: to avoid unnecessary complexity, in the operators NOT and BT the only checked arguments are, respectively, the first one
and the first two.
![OP Structure](/readme_assets/Operators.png)

#### Parsing and filter application
After the user inputs a filter expression (via html form or get url), the string is first
checked for errors and then cleaned. Then the parser builds a tree of Operators from the expression and finally the built
tree is applied iteratively to the list of object 
For example `{"$or": {["country": {"$in": {["Italy", "France", "United Kingdom"]}, "placeType.code" = 12]}` 
after cleaning becomes `$or:[country:$in:[Italy,France,United Kingdom],placeType.code=12]`.  
Here you can see that the curly brackets are not necessary but supportet, the formula can contain an arbitrary amout of 
spaces and that values that contain spaces must be delimited by double quotation marks.  
![Filter example](/readme_assets/Filter_example.png)  

After the operators' tree is built, it's applied to each item of a collection (in this case a list of trends) returning
true if the entry matches the condition or false in the other case; thus the filtered collection is made only by the
entries that match the expression.  
The main advantage of this approach is that the filter expression string needs only to be parsed once, and once the 
expression tree is built it only needs to be applied to an object to check if it matches the expression.
   


## Project management
A very important but underestimated part of the project was the planning of the sequence of implementations.
I included this section in the readme because i think that having a good plan and idea of what to do before the actual
 implementation is a fundamental step in every project.
- [x] Git repository initialization
- [x] Spring boot initialization
    - [x] Hello World on browser
    - [x] Hello World on routes
- [x] Twitter APIs initialization
    - [x] GET tests on route (just JSON)
- [x] Model definition
- [x] Implement data requests (GET)
    - [x] REST call (`/api/trends`)
    - [x] Webpage table (`/home`)
- [x] Implement metadata requests (GET)
- [x] Website polishing

After the first revision, some requirements were added (filters and date selection), so therefore the project plan was 
updated with the new requirements
- [x] Multiple date handling
    - [x] Implement periodical saving in server
    - [x] Handle requests for specific dates
- [x] Filters implementation
    - [x] Design and problem definition (see below)
    - [x] Operator implementation
    - [x] Parser implementation
    - [x] Filter interfacing with user
- [x] Final touches and debugging
