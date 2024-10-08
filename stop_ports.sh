#!/bin/bash

# Define the ports to stop
PORTS=(8081 8082 8083 8084 8085)

# Function to check if a port is in use
is_port_in_use() {
    local port=$1
    if netstat -ano | findstr ":$port" | findstr "LISTEN"; then
        return 0
    else
        return 1
    fi
}

# Function to kill the process running on a specific port
kill_process_on_port() {
    local port=$1
    local pid=$(netstat -ano | findstr ":$port" | awk '{print $5}')
    if [ -n "$pid" ]; then
        echo "Killing process $pid on port $port"
        taskkill /PID $pid /F
    fi
}

# Stop processes on specified ports
for port in "${PORTS[@]}"; do
    if is_port_in_use $port; then
        kill_process_on_port $port
    fi
done

echo "Processes on specified ports have been stopped."