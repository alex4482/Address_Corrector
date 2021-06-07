Cerinta proiectului:
- [x] Write an algorithm that corrects the fields country, state, city of a postal address. Example: Country: RO, State: New York, City: Iasi will become
  Country: RO, State: Iasi, City: Iasi
- [x] the algorithm needs to have unit tests and integration tests for performance and precision
- [x] ideally the algorithm will work for all countries in the world and a few languages
- [x] Expose a REST api using spring boot that will receive a postal address and return the corrected result
- [ ] Deploy the application as a docker container in aws/heroku or other using a continuous deployment pipeline"

jdk version - 1.8
maven version - 3.6.3
other versions are specified in the pom.xml file

- create new project,

- pull src folder from git, and replace the one in the new project

- add pom xml dependencies

- pull the romanian dataset file

- go to class DataStoreOperations and DataStore and comment the lines

String dataStorePath = ".\\allCountries\\DataStore.txt";

and 

private final static String dataStoreObjectPath = ".\\data_store_resources\\DataStoreObject";

and uncomment the lines

//private final static String dataStoreObjectPath = ".\\data_store_resources\\RomanianDataStoreObject";

and

//transient private final String dataStorePath = ".\\data_store_resources\\RomaniaDataStore.txt";

- and make sure to have the RomanianDataStore.txt in a folder called data_store_resources

- the database i used is a postgresql database, make sure to change in application properties file if needed

- run the application
wait a little to create/load the datastore
- open localhost:8081/swagger-ui
hf

How it works.
- the datastore object is created then the spring boot application will start
- after a request is made, we check in every field for all strings that could represent a country/state name or postal code.
- normalize every field
- use regular expressions to look in every field to find names that could match any field
- make combinations of those words to consider names like "united states of america"
- find all locations that could be represented by any of those strings
- now we have actual locations and not nodes
- try to match nodes with nodes to get a better score to find the best address.
- 0,1, or more addresses may be returnes
- the street line field will always be returnes empty
- postal code = the first 5 digit number found
- a name for a location = every string containing at least 2 letters

 # Address_Corrector" 
