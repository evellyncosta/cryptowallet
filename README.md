# Cryptowallet
This project calculates the performance of a cryptocurrency wallet. It was built using Spring Boot and Java 17.

# Running the project
To run the project, follow these steps:

## Step 1
Copy the file to the src/main/resources folder. The application expects a file named file.csv

## Step 2
Run the command
```
docker build --tag=cryptowallet:latest .
```

## Step 3
Run the command
```
docker run -it -p 8080:8080 cryptowallet
```

## Step 4
Monitor the logs to observe the behavior of the application
