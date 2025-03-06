# HolidayFetcherAPI
A RESTful API for retrieving and managing public holiday data for various countries by invoking the Nager.Date API (https://date.nager.at/).

## Prerequisites and tools needed

- Java 17 or higher
- Maven 3.8 or higher
- Spring Boot
- A REST client like Postman (optional, for testing APIs)

## How to install and start the application with IntelliJ

**Note**: Ensure that you're using Git Bash as your terminal on Windows for the following steps.

1. Open Git Bash and clone the repository:
   git clone https://github.com/Becca0409/HolidayFetcherAPI.git

2. Open the Project
   - Launch your IDE.
   - Select "Open" or "Import Project" from the welcome screen or the File menu.
   - Navigate to your project folder (the folder you cloned from GitHub) and select it.
   - If prompted, select "Import as Maven Project" to ensure the dependencies are properly configured.

3. Verify the Dependencies
The IDE should automatically detect the pom.xml file and download the required dependencies. If not, go to the Maven tool window on the right, then click the Reload All Maven Projects icon and check that there are no red errors in your project related to dependencies.

4. Run the Application
Locate the main class in your project. For Spring Boot, this is typically the class annotated with @SpringBootApplication. In this case, the class is called HolidayFetcherApiApplication.
Right-click on it and select "Run 'HolidayFetcherApiApplication.main()'". 

The application will start, and you’ll see output in the IDE’s console indicating that the Spring Boot application has started!
It will run on http://localhost:8080.

5. Test the Endpoints
Use a REST client (e.g., Postman) or your browser to test the API endpoints. Here are the endpoints for the APIs in the controller:

To get the last three holidays celebrated given a country code and a year:
GET /holidays/last?country=NL&year=2024

To get the holidays celebrated on non-weekend days given a year and one or multiple country codes:
GET /holidays/non-weekends?year=2024&countries=IT,NL

To get the holidays two countries have in common given a year and two country codes:
GET /holidays/common?year=2024&country1=IT&country2=NL
