# cbpOrganizer
Tool to organize cBioPortal studies for publishing to cBioPortal. This will help cBioPortal study owners prepare and validate their studies. This tool helps to publish cBioPortal studies efficiently.

### Stack
- React
- Spring Boot
- Python
- Keycloak

## Prerequisites

- Node.js
- Java 8 or higher

## Project Structure

## Running the Application

1. Clone the repository
2. Navigate to the project directory and run `npm install` to install dependencies for the frontend
3. Run `mvn clean package` to build the backend Spring Boot application
4. Start the backend by running `java -jar target/cbporganizer-0.0.1-SNAPSHOT.jar`
5. Start the frontend by running `npm start` from the project directory
6. Open `http://localhost:3030` in your browser to access the application

## Docker
1. Clone the repository
2. Create a `.env` file in the project directory and add the following environment variables:
```BACKEND_URL=http://localhost:8080``` See  .env.example for more details
3. Create a `.env` file in the 'frontend' directory and add the following environment variables:
```REACT_APP_API_URL = 'http://localhost:8080/file/'``` See  .env.example for more details
4. In project root folder, run in terminal `docker-compose up -d`

## Features

- Upload cBioPortal study .tar.gz files to the server
- Validate uploaded files
- View validation results

## Uploading Studies

To upload a study, click the "Upload File" button and select the .tar.gz you want to upload.

## Validating Studies

To validate a study, select the validate tab, select the study you want to validate, and click the "Validate" button.

## Viewing Validation Results
In Result tab, you can view the validation results for the selected study.

## Contributing

If you would like to contribute to this project, please create a pull request.

## License
This project is licensed under the MIT License. See the `LICENSE` file for details.

