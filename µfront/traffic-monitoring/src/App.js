import React, { useState } from 'react';

function App() {
  // State for traffic generator
  const [endpoint, setEndpoint] = useState('');
  const [trafficType, setTrafficType] = useState('');

  // State for malicious traffic generator
  const [maliciousEndpoint, setMaliciousEndpoint] = useState('');
  const [attackType, setAttackType] = useState('');

  // Function to generate traffic
  const generateTraffic = async (e) => {
    e.preventDefault();
    const url = `${endpoint}?trafficType=${trafficType}`;
    try {
      const response = await fetch(url);
      const result = await response.json();
      console.log('Traffic generated:', result);
    } catch (error) {
      console.error('Error generating traffic:', error);
    }
  };

  // Function to generate malicious traffic
  const generateMaliciousTraffic = async (e) => {
    e.preventDefault();
    const url = `${maliciousEndpoint}?attackType=${attackType}`;
    try {
      const response = await fetch(url);
      const result = await response.json();
      console.log('Malicious traffic generated:', result);
    } catch (error) {
      console.error('Error generating malicious traffic:', error);
    }
  };

  return (
    <div>
      <h1>Traffic Monitoring System</h1>
      
      {/* Traffic Generator Form */}
      <div>
        <h2>Traffic Generator</h2>
        <form onSubmit={generateTraffic}>
          <label>
            Endpoint URL:
            <input
              type="text"
              value={endpoint}
              onChange={(e) => setEndpoint(e.target.value)}
              required
            />
          </label>
          <br />
          <label>
            Traffic Type:
            <input
              type="text"
              value={trafficType}
              onChange={(e) => setTrafficType(e.target.value)}
              required
            />
          </label>
          <br />
          <button type="submit">Generate Traffic</button>
        </form>
      </div>

      {/* Malicious Traffic Generator Form */}
      <div style={{ marginTop: '40px' }}>
        <h2>Malicious Traffic Generator</h2>
        <form onSubmit={generateMaliciousTraffic}>
          <label>
            Endpoint URL:
            <input
              type="text"
              value={maliciousEndpoint}
              onChange={(e) => setMaliciousEndpoint(e.target.value)}
              required
            />
          </label>
          <br />
          <label>
            Attack Type:
            <input
              type="text"
              value={attackType}
              onChange={(e) => setAttackType(e.target.value)}
              required
            />
          </label>
          <br />
          <button type="submit">Generate Malicious Traffic</button>
        </form>
      </div>
    </div>
  );
}

export default App;
