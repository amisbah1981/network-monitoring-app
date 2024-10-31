from fastapi import FastAPI
import numpy as np
import pandas as pd
import psycopg2
import os
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.metrics import accuracy_score
from sklearn.preprocessing import LabelEncoder
import logging
import matplotlib.pyplot as plt
import uvicorn

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

app = FastAPI()

# Database configuration
DB_NAME = os.getenv("DB_NAME", "ciciomt2024")
DB_USER = os.getenv("DB_USER", "postgres")
DB_PASSWORD = os.getenv("DB_PASSWORD", "admin")
DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = os.getenv("DB_PORT", "5432")

# Connect to PostgreSQL
def get_db_connection():
    return psycopg2.connect(
        dbname=DB_NAME,
        user=DB_USER,
        password=DB_PASSWORD,
        host=DB_HOST,
        port=DB_PORT
    )

# Load data function to take 1000 rows per class
def load_data(table_name: str, class_column: str = 'attack_type', sample_size: int = 100000):
    conn = get_db_connection()
    query = f"SELECT * FROM {table_name};"
    data = pd.read_sql(query, conn)
    conn.close()

    # Sample 1000 rows per class
    sampled_data = data.groupby(class_column).apply(lambda x: x.sample(n=min(sample_size, len(x)))).reset_index(drop=True)
    return sampled_data

# Improved FDDA function
def improved_fdda(X, y, X_test, y_test, n_devices=10, n_rounds=20, n_estimators=100, prune_ratio=0.5):
    device_data = []
    for _ in range(n_devices):
        X_device, _, y_device, _ = train_test_split(X, y, test_size=0.9, stratify=y)
        device_data.append((X_device, y_device))
    
    global_model = RandomForestClassifier(n_estimators=n_estimators, random_state=42)
    global_model.fit(device_data[0][0], device_data[0][1])
    
    for round in range(n_rounds):
        local_models = []
        local_performances = []
        
        for X_device, y_device in device_data:
            local_model = RandomForestClassifier(n_estimators=n_estimators, random_state=42)
            local_model.fit(X_device, y_device)
            
            importances = local_model.feature_importances_
            threshold = np.percentile(importances, prune_ratio * 100)
            pruned_estimators = [tree for tree, imp in zip(local_model.estimators_, importances) if imp > threshold]
            
            local_models.append(pruned_estimators)
            local_performances.append(local_model.score(X_device, y_device))
        
        weights = np.array(local_performances) / np.sum(local_performances)
        
        all_trees = [tree for model in local_models for tree in model]
        soft_predictions = np.mean([tree.predict_proba(X) for tree in all_trees], axis=0)
        temperature = 2.0 / (1 + np.exp(-0.1 * round))
        soft_predictions = np.exp(np.log(soft_predictions) / temperature)
        soft_predictions /= np.sum(soft_predictions, axis=1, keepdims=True)
        
        global_model = DecisionTreeClassifier(max_depth=int(5 + round))
        sample_weights = np.max(soft_predictions, axis=1)
        global_model.fit(X, np.argmax(soft_predictions, axis=1), sample_weight=sample_weights)
        
        accuracy = accuracy_score(y_test, global_model.predict(X_test))
        logger.info(f"Round {round + 1} Improved FDDA global model accuracy: {accuracy:.4f}")
    
    return global_model

# Main route to load data, train models, and display results
@app.get("/train_and_compare")
def train_and_compare_models():
    try:
        # Load sampled train and test data (1000 rows per class)
        train_data = load_data('traffic_data_training')
        test_data = load_data('traffic_data_test')
        
        # Prepare data
        X_train = train_data.drop('attack_type', axis=1)
        y_train = train_data['attack_type']
        X_test = test_data.drop('attack_type', axis=1)
        y_test = test_data['attack_type']

        # Label encoding
        le = LabelEncoder()
        y_train_encoded = le.fit_transform(y_train)
        y_test_encoded = le.transform(y_test)

        # Train using improved FDDA
        fdda_model = improved_fdda(X_train, y_train_encoded, X_test, y_test_encoded)

        # Evaluate the improved FDDA model
        fdda_accuracy = accuracy_score(y_test_encoded, fdda_model.predict(X_test))
        logger.info(f"Final Improved FDDA Accuracy: {fdda_accuracy:.4f}")

        # Train and evaluate a centralized model for comparison
        centralized_model = RandomForestClassifier(n_estimators=100, random_state=42)
        centralized_model.fit(X_train, y_train_encoded)
        centralized_accuracy = centralized_model.score(X_test, y_test_encoded)
        logger.info(f"Centralized Learning Accuracy: {centralized_accuracy:.4f}")

        # Save and display accuracy comparison as a plot
        accuracies = [fdda_accuracy, centralized_accuracy]
        labels = ["Improved FDDA", "Centralized Learning"]
        
        plt.figure(figsize=(10, 6))
        plt.bar(labels, accuracies)
        plt.title("Model Accuracies Comparison")
        plt.ylabel("Accuracy")
        plt.ylim(0, 1)
        
        plot_path = "accuracy_comparison.png"
        plt.savefig(plot_path)
        plt.close()
        logger.info("Plot saved successfully.")

        return {
            "status": "Model training and comparison completed",
            "fdda_accuracy": fdda_accuracy,
            "centralized_accuracy": centralized_accuracy,
            "plot_path": plot_path
        }
    
    except Exception as e:
        logger.error(f"Error in training and comparison: {e}")
        return {"error": str(e)}

# Run the app
if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
