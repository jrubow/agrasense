# AsterLink - Smart Farms, Smart Agriculture
### Mesh Sensor Network for Agriculture

Smart, scalable field monitoring using low-power BLE Mesh nodes and ESP32 microcontrollers.

## Overview

This project uses a BLE Mesh Network to collect real-time environmental data (temperature, humidity, soil moisture, and light) across farmland. Sensor nodes relay information wirelessly through a self-healing mesh to a central Wi-Fi-connected provisioner node.

## Problem Statement
### Premise
As sensor technology becomes more advanced, the cost for wide scale sensors has risen exponentially. Farmers, firefighters, environmental scientists and others are faced with an increasing cost to data ratio as wide scale sensing has become more and more expensive. There is an enormous need for these essential workers to have easy and quick access to data that traverses thousands of acres of land. Current sensors do not allow for large scale data analytics and therefore this leaves a large gap in the market that we want to fulfill. 

### Solution
We are creating a low cost sensor alternative to high cost sensors in the agriculture and environmental communities. These sensors will use WIFI-mesh technology to communicate data over large distances without need for connection to internet services. A sentinel node will serve as a liaison between the sensors and the database. It will then transfer the data for data analysis.

## Project Objectives
### Sensor Devices & Mesh Technology
The sensor mesh will serve to communicate information between all sensor devices without need for connection to the internet or use of a SIM card for each device. There will be two types of devices, sentinel devices and relay devices. Sentinel devices are responsible for communicating all data from the devices to the internet either via a SIM card or a WiFi connection. The relay devices will communicate sensor data via other relay devices to the sentinel device. This mesh of devices will be scalable, self healing and decentralized.

## Communication Dashboard
Users will be able to communicate instructions to the devices via an intuitive web dashboard. Also, users will be able to view real time sensor data as well as view and download old data for analysis from the dashboard. Importantly the dashboard will 

## Deployment Aides
Users will have access to multiple deployment aides. When deciding where to deploy devices users will be able to use a machine learning model which will accurately predict the optimal placement of the devices in the field, for the strongest connectivity and sensor readings. Users can choose the radius for each device with the maximum value around 220 ft. This can also be modified to fit each field’s specifications.



## Features

- Mesh for decentralized, reliable communication

- Low-power ESP32 nodes using deep sleep for extended battery life

- Sensor Suite: DHT122, soil moisture, light sensors, pH sensor,

- Provisioner Node connects to Wi-Fi and sends data to the cloud

- Low Cost
