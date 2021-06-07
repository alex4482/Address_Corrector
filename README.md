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

 # Address_Corrector" 
