
# Url Shortening Rest API

HTTP-based RESTful API for managing Short URLs and redirecting clients similar to bit.ly or goo.gl. 

A Short Url: 
1. Has one long url 
2. Permanent; Once created 
3. Is Unique; If a long url is added twice it should result in two different short urls. 
4. Not easily discoverable; incrementing an already existing short url should have a low probability of finding a working short url. 

Solution supports the following operations: 
1. Generating a short url from a long url 
2. Redirecting a short url to a long url within 10 ms. 
3. Listing the number of times a short url has been accessed in the last 24 hours, past week and all time. 
4. Persistence (data must survive computer restarts) 

## Setup
1. Make sure you have Docker installed.
2. Clone this repository:
   git clone https://github.com/priyapatelr23/UrlShorteningService.git
3. Cd to the project directory
4. Run Docker compose to setup and run the application.
   > docker-compose up -d
5. The application will be available on localhost:8080

## Endpoints
- **POST /url**
  - **Description:** Generatates a short url
  - **Request Body:** Json payload containing the long url
  - **Response Body:** Short url in JSON format
  - **Curl Request:** curl --location 'http://localhost:8080/generate' \
--header 'Content-Type: application/json' \
--data '{
    "longUrl": "LONG_URL_PLACEHODLER"
}'
- **GET /{shortUrl}**
  - **Description:** Redirects to the original long url
  - **Curl Request:** curl --location 'http://localhost:8080/{SHORT_URL_PLACEHOLDER}'
- **GET /url/{shortUrl}**
  - **Description:** Listing the number of times a short url has been accessed in the last 24 hours
  - **Response Body:** Integer count of number of times a short url has been accessed in the last 24 hours.
  - **Curl Request:** curl --location 'http://localhost:8080/url/{SHORT_URL_PLACEHOLDER}'

## Postman Collection

A Postman collection containing sample requests for the endpoints mentioned above is provided [here](https://drive.google.com/file/d/1ecZNyGesfmoGuawd3-VhzjAuw8gO69Ru/view?usp=sharing)

## Design Details
 - **Database**
      -NoSql Db (MongoDb)
      -MongoDb supports sharding
      -Non-relational data
      -Flexible schema
      -Easy to scale horizontolly
      -There is no need for complex joins
- **Cache**
     -Currently using spring in memory cache for simplicity but ideally would use redis cache as redis supports clustering and replication, which will allow to scale redis deployment horizontally to handle increased load and data volume
     -Store frequently accessed shortUrl in redis cache along with its longUrl mapping, for fast redirect to avoid making a call to the db to retrive long url.    
     This will reduce the latency.
     -Caching also help reduce the load on db server during hight trafic or heavy load as we will not be repeatedly querying the db. This prevents overlaoding the backend    
     infrastructre.
     -Assumption: Current implementation have kept redis default configurations. In real usecase we can provide specific configurations such as setting the eviction policy 
     to least recently used.
- **Generating Unique Url each time**
     -Generating unique url using Murmur3 or SHA-256 algorithm. I have decided to use Murmur3
     -Appending current time to long Url to ensure each time a unique short url is generated even for duplicate long urls
- **Scope for Improvement**
     -Logging, analytics and monitoring
     -Scalling:  run multiple instances of application, sharding db, replica sets to be fault taulerant, redis clustering and replication, adding loadbalancers between 1. client and application servers, 2. Application servers and db servers and 3. Between application servers and cache servers
     -mongoDB's aggregation framework to perform analytics such as getting the number of times short url has been accessed. 
