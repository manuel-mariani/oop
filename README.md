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
/twittertrends/api/data | Returns all the trends of the current day | GET
/twittertrends/api/data?date=\[dateString]&filter=\[filterExpression] | returns the trends filtered by date and expression | GET
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

#####Notes:
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

## Frontend
The frontend design is very simple and barebone, without compromising on functionalities and ease of use.

It's built using HTML and CSS as the view part, Thymeleaf as a very simple controller and the Spring server as the backend.
###Screenshots
## Project management

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

## Calls structure
## Filter design 
