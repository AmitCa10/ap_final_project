# Views Package Documentation

## Overview
The `views` package implements the View layer of the MVC (Model-View-Controller) architecture for the Agent Graph Manager project. It separates presentation logic from business logic and provides a clean interface for rendering different types of responses.

## Architecture

### Core Components

1. **View Interface** (`View.java`)
   - Base interface for all view implementations
   - Defines `render(Object data, OutputStream output)` method
   - Defines `getContentType()` method for proper HTTP headers

2. **HtmlView** (`HtmlView.java`)
   - Renders HTML content using templates
   - Supports simple template substitution with `{{content}}` placeholders
   - Automatically handles HTTP headers

3. **TopicView** (`TopicView.java`)
   - Specialized view for rendering topic data as HTML table rows
   - Handles Topic and Message objects from the graph package
   - Provides HTML escaping for security
   - Supports both success and error states

4. **JsonView** (`JsonView.java`)
   - Renders data as JSON responses
   - Handles various data types (String, Number, List, Objects)
   - Includes proper JSON escaping
   - Sets appropriate CORS headers

5. **ViewFactory** (`ViewFactory.java`)
   - Factory pattern implementation for creating views
   - Supports different view types through enum
   - Provides default templates and customization options

## Usage Examples

### Basic HTML View
```java
View htmlView = ViewFactory.createView(ViewFactory.ViewType.HTML);
htmlView.render("Hello World", outputStream);
```

### Topic Table View
```java
View topicView = ViewFactory.createView(ViewFactory.ViewType.TOPIC_TABLE);
List<Topic> topics = topicManager.getTopics();
topicView.render(topics, outputStream);
```

### JSON API Response
```java
View jsonView = ViewFactory.createView(ViewFactory.ViewType.JSON);
jsonView.render(dataObject, outputStream);
```

### Custom HTML Template
```java
String customTemplate = "<!DOCTYPE html><html><body>{{content}}</body></html>";
View customView = ViewFactory.createHtmlView(customTemplate);
customView.render("Custom content", outputStream);
```

## Integration with Servlets

The views package is integrated with the servlet layer to provide clean separation of concerns:

```java
// In TopicDisplayer servlet
View topicView = ViewFactory.createView(ViewFactory.ViewType.TOPIC_TABLE);

if (topics.isEmpty()) {
    topicView.render("No configuration loaded", toClient);
} else {
    List<Topic> topicList = new ArrayList<>(topics);
    topicView.render(topicList, toClient);
}
```

## Benefits

1. **Separation of Concerns**: Business logic is separated from presentation logic
2. **Reusability**: Views can be reused across different servlets
3. **Maintainability**: Template changes don't require code changes
4. **Type Safety**: Proper handling of different data types
5. **Security**: Built-in HTML and JSON escaping
6. **Extensibility**: Easy to add new view types

## Future Enhancements

- **Template Engine Integration**: Support for advanced templating (Mustache, Thymeleaf)
- **Caching**: Template and rendered content caching
- **Internationalization**: Multi-language support
- **View Composition**: Combining multiple views
- **Error Views**: Specialized error page rendering

## Error Handling

Views handle errors gracefully:
- Invalid data types are handled with fallback responses
- IO exceptions are propagated to calling servlets
- Security escaping prevents XSS attacks
- Proper HTTP status codes are set

## Thread Safety

All view implementations are stateless and thread-safe, making them suitable for use in a multi-threaded servlet environment.
