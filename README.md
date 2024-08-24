# Greenfield

## Introduction

The Greenfield project simulates a smart city named Greenfield where a fleet of cleaning robots move around the city districts to clean the streets and measure air pollution. The project involves creating several components, including an Administrator Server, an Administrator Client, and a network of robots that coordinate through gRPC and send pollution measurements via MQTT.

This project was developed as part of the Distributed and Pervasive Systems Lab Course at the university.

## Components

### MQTT Broker
The MQTT Broker is used by the cleaning robots to publish air pollution measurements to the Administrator Server. The broker operates at `tcp://localhost:1883`.

### Cleaning Robots
The cleaning robots are the core of the project. Each robot:
- Periodically sends air pollution measurements to the Administrator Server via MQTT.
- Coordinates with other robots using gRPC to decide which robot can go for maintenance at a time.
- Simulates a sensor to detect air pollution levels, which are processed using a sliding window technique.

### Administrator Server
The Administrator Server is a REST server that manages the network of cleaning robots and collects pollution data sent via MQTT. It provides APIs to:
- Register and remove cleaning robots.
- Collect and analyze pollution measurements.
- Provide statistics to the Administrator Client.

### Administrator Client
The Administrator Client is a command-line interface that allows querying the Administrator Server for information about the robots and pollution statistics.

## Functionalities

### Robot Registration and Coordination
- **Registration**: Robots register with the Administrator Server, which assigns them a position in Greenfield and provides information about other robots in the system.
- **Coordination**: Robots use gRPC to communicate and decide which one should go for maintenance using a mutual exclusion algorithm.

### Pollution Measurement and Reporting
- **Measurement**: Each robot measures air pollution (PM10 levels) and sends the average measurements to the Administrator Server every 15 seconds.
- **Reporting**: The Administrator Server collects pollution data from all districts and makes it available to the Administrator Client for analysis.

### Mutual Exclusion for Maintenance
- **Ricart and Agrawala Algorithm**: The mutual exclusion for robot maintenance is implemented using the Ricart and Agrawala algorithm. This distributed algorithm ensures that only one robot at a time can access the mechanic for maintenance, avoiding conflicts. The robots coordinate through gRPC to request and grant permission to enter the critical section (maintenance).

## Starting the Project

To start the Greenfield project, follow these steps:

1. **Clean the Project:**
   Run the Gradle `clean` task to ensure that the project is built from a clean state.
   ```bash
   ./gradlew clean

2. **Build the Project**
   Run the Gradle build task to compile the project and generate the necessary protocol buffer files. 
   ```bash
   ./gradlew build
   
3. **Run the Administrator Server**
   Start the Administrator Server to manage the robots and handle pollution data.

4. **Allow Multiple Instances of `RobotStart`**:
   Run multiple instances of RobotStart to simulate multiple robots in Greenfield.

5. **Run RobotStart Instances**: 
   Launch several robots by running multiple instances of RobotStart. Each robot will register with the Administrator Server and start its operations.
6. **Run the Administrator Client**: 
   Use the Administrator Client to interact with the Administrator Server and obtain statistics or manage the robot fleet.

