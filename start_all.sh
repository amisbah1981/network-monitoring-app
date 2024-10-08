#!/bin/bash

# Define the base directory
BASE_DIR="C:/Users/10059890/Desktop/network-monitoring-app/backend"

# Define the services directories and their respective ports
declare -A SERVICES_PORTS=(
    ["network-monitoring-service"]=8081
    ["data-transformation-service"]=8082
    ["model-training-service"]=8083
    ["model-aggregation-service"]=8084
    ["ids-ips-service"]=8085
)

# Function to build and start a service
build_and_start_service() {
    local service_dir=$1
    local port=$2

    cd "$BASE_DIR/$service_dir"
    ./gradlew clean build
    ./gradlew bootRun &
}

# Stop, build, and start each service
for service in "${!SERVICES_PORTS[@]}"; do
    build_and_start_service "$service" "${SERVICES_PORTS[$service]}"
done

# Wait for all background processes to finish
wait

echo "All services restarted."