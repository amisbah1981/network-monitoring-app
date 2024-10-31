const WebSocket = require('ws');
const kafka = require('kafka-node');

const Consumer = kafka.Consumer;
const client = new kafka.KafkaClient({ kafkaHost: 'localhost:9092' });
const consumer = new Consumer(
  client,
  [{ topic: 'edge-traffic', partition: 0 }, { topic: 'malicious-traffic', partition: 0 }],
  { autoCommit: true, fromOffset: false }
);

const wss = new WebSocket.Server({ port: 8080 });

// Cache to store the latest 10 messages for each topic
const messageCache = {
  'edge-traffic': [],
  'malicious-traffic': []
};

// Limit the cache to the last 10 messages
const CACHE_LIMIT = 10;

// Helper function to add a message to the cache
function addToCache(topic, message) {
  if (messageCache[topic].length >= CACHE_LIMIT) {
    messageCache[topic].shift(); // Remove the oldest message
  }
  messageCache[topic].push(message);
}

wss.on('connection', (ws) => {
  console.log('WebSocket connection established');

  // Send cached messages for each topic to the newly connected client
  Object.keys(messageCache).forEach(topic => {
    messageCache[topic].forEach(message => {
      ws.send(JSON.stringify(message));
    });
  });

  ws.on('close', () => {
    console.log('WebSocket connection closed');
  });
});

// Listen for messages from Kafka and broadcast to all connected WebSocket clients
consumer.on('message', (message) => {
  console.log('Kafka message received:', message);

  // Cache the message
  addToCache(message.topic, message);

  // Broadcast message to all connected clients
  wss.clients.forEach((client) => {
    if (client.readyState === WebSocket.OPEN) {
      client.send(JSON.stringify(message));
    }
  });
});

console.log('WebSocket server is running on ws://localhost:8080');
