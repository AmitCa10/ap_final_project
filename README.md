# Advanced Programming Final Project - Agent-Topic Graph Manager

## Project Overview

This project implements a Java-based HTTP server that manages an agent-topic graph system with a beautiful web interface. Users can upload configuration files to define agents and their topic subscriptions/publications, visualize the resulting graph, and publish messages to topics to see real-time agent interactions.

## Features

- **🔧 Configuration Management**: Upload configuration files to define agent behaviors
- **🔗 Dynamic Graph Visualization**: Interactive graph showing agent-topic relationships using vis.js
- **📤 Message Publishing**: Send messages to topics and watch agents process them
- **📊 Real-time Monitoring**: View latest messages in all topics through an elegant table
- **🎨 Modern UI**: DopplePress-inspired design with gradients and smooth animations
- **⚡ Concurrent Processing**: Multi-threaded HTTP server with parallel agent execution

## Architecture

### Core Components

- **HTTP Server** (`MyHTTPServer`): Custom HTTP server with servlet support
- **Agent System**: 
  - `Agent` interface for message processing
  - `ParallelAgent` for concurrent execution
  - Various mathematical agents (Plus, Multiply, Increment, Subtract, Divide)
- **Topic Management**: Pub/Sub system with `TopicManagerSingleton`
- **Graph Visualization**: Dynamic graph generation from agent-topic relationships
- **Web Interface**: Multi-frame layout for control panel, graph, and monitoring

### Agent Types Available

1. **PlusAgent**: Adds two input values
2. **MulAgent**: Multiplies two input values  
3. **SubAgent**: Subtracts second input from first
4. **DivAgent**: Divides first input by second
5. **IncAgent**: Increments input by 1

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- A modern web browser
- No external dependencies required (pure Java implementation)

### Installation and Running

1. **Navigate to the project directory**
   ```bash
   cd /home/amitca/Projects/University/AdvancedProg/ap_final_project
   ```

2. **Compile the project**
   ```bash
   mkdir -p build
   javac -cp src -d build src/**/*.java src/*.java
   ```

3. **Run the application**
   ```bash
   java -cp build Main
   ```

4. **Access the web interface**
   - Open your browser and go to: `http://localhost:8080/app/index.html`
   - The interface has three main sections:
     - **Left**: Control panel for uploading configs and sending messages
     - **Center**: Graph visualization 
     - **Right**: Topics and messages table

### Configuration File Format

Configuration files define agents using triplets of lines:
```
[ClassName]
[SubscribedTopics (comma-separated)]
[PublishedTopics (comma-separated)]
```

**Example Configuration (`simple.conf`):**
```
configs.PlusAgent
A,B
C
configs.IncAgent
C
D
```

This creates:
- A PlusAgent that subscribes to topics A,B and publishes to C
- An IncAgent that subscribes to topic C and publishes to D

### Example Usage

1. **Upload Configuration**: 
   - Use the file input in the control panel
   - Select one of the example files in `example_conf_files/`
   - Click "Deploy Configuration"
   - Watch the graph appear in the center panel

2. **Publish Messages**:
   - Enter a topic name (e.g., "A" or "B")
   - Enter a numeric value (e.g., "5.0")
   - Click "Send Message" 
   - See results in the topics table on the right

3. **Example Flow**:
   - Upload `simple.conf`
   - Send message "10" to topic "A"
   - Send message "5" to topic "B" 
   - Watch as PlusAgent calculates 10+5=15 and publishes to topic "C"
   - IncAgent receives 15, calculates 15+1=16, publishes to topic "D"

## Project Structure

```
ap_final_project/
├── src/
│   ├── configs/          # Agent implementations and configuration
│   ├── graph/           # Core agent-topic system
│   ├── server/          # HTTP server implementation
│   ├── servlet/         # Servlet interface
│   ├── servlets/        # Web request handlers
│   ├── views/           # HTML generation utilities
│   └── Main.java        # Application entry point
├── html_files/          # Web interface files
├── example_conf_files/  # Sample configuration files
├── config_files/        # Uploaded configurations storage
└── build/              # Compiled classes
```

## Advanced Features

### Graph Visualization
- **Interactive**: Click and drag nodes, zoom in/out
- **Real-time**: Updates automatically when configuration changes
- **Color-coded**: Agents (red circles) vs Topics (teal rectangles)
- **Cycle Detection**: Shows if graph contains cycles

### Concurrent Processing
- Each agent runs in its own thread via `ParallelAgent`
- Message queues prevent blocking
- Thread-safe topic management

### Web Interface Highlights
- **Responsive Design**: Works on different screen sizes
- **Status Messages**: Clear feedback for all operations
- **File Upload**: Drag-and-drop support for configuration files
- **Error Handling**: Comprehensive error messages and validation

## API Endpoints

- `GET /app/{filename}` - Serve static HTML/CSS/JS files
- `POST /upload` - Upload and deploy configuration files
- `GET /publish?topicName={name}&message={value}` - Publish message to topic

## Technical Implementation Notes

- **Custom HTTP Server**: Built from scratch using Java ServerSocket
- **Pub/Sub Pattern**: Decoupled agent communication via topics
- **Flyweight Pattern**: Efficient topic instance management
- **Observer Pattern**: Agents subscribe to topic changes
- **Thread Pool**: Concurrent request handling
- **Dynamic Class Loading**: Configuration-driven agent instantiation

## Example Configurations

### Simple Math Chain
```
configs.PlusAgent
X,Y
Sum
configs.MulAgent  
Sum,Z
Result
```

### Complex Operations
```
configs.PlusAgent
A,B
Sum1
configs.SubAgent
A,B  
Diff1
configs.DivAgent
Sum1,Diff1
Ratio
```

## Troubleshooting

- **Port 8080 in use**: Change port in Main.java
- **File not found**: Ensure configuration files are properly formatted
- **Agent not found**: Verify class name in configuration matches actual class
- **Browser issues**: Try clearing cache or use incognito mode

## Future Enhancements

- REST API for programmatic access
- Configuration validation UI
- Agent performance metrics
- Custom agent development interface
- Graph export functionality
- Message history and replay

---

**Developed for Advanced Programming Course**  
*University Project - 2025*
