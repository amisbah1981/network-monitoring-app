import os
import requests

# Bypass proxy for localhost
os.environ['NO_PROXY'] = 'localhost'

# Define the services and their respective ports
services_ports = {
    'network-monitoring-service': 8081,
    'data-transformation-service': 8082,
    'model-training-service': 8083,
    'model-aggregation-service': 8084,
    'ids-ips-service': 8085
}

# Function to check the health of a service
def check_health(service, port):
    url = f"http://localhost:{port}/actuator/health"
    try:
        print(f"Checking health for {service} on port {port}...")
        response = requests.get(url)
        print(f"Response status code: {response.status_code}")
        response.raise_for_status()
        health_status = response.json()
        print(f"Response JSON: {health_status}")
        if health_status['status'] == 'UP':
            print(f"Service '{service}' on port {port} is UP")
        else:
            print(f"Service '{service}' on port {port} is DOWN with status: {health_status['status']}")
    except requests.exceptions.HTTPError as http_err:
        print(f"Service '{service}' on port {port} is DOWN with HTTP error: {http_err}")
    except requests.exceptions.ConnectionError as conn_err:
        print(f"Service '{service}' on port {port} is DOWN with connection error: {conn_err}")
    except requests.exceptions.Timeout as timeout_err:
        print(f"Service '{service}' on port {port} is DOWN with timeout error: {timeout_err}")
    except requests.exceptions.RequestException as req_err:
        print(f"Service '{service}' on port {port} is DOWN with error: {req_err}")

# Check the health of each service
for service, port in services_ports.items():
    check_health(service, port)