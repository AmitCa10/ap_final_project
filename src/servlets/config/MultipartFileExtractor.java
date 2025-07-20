package servlets.config;

/**
 * @file MultipartFileExtractor.java
 * @brief Utility for extracting file content from HTTP multipart form data
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This utility class handles the parsing of HTTP multipart form data to extract
 * uploaded configuration files. Follows the Single Responsibility Principle
 * by focusing solely on multipart parsing.
 */
public class MultipartFileExtractor {
    
    /**
     * @brief Extracts file content from multipart form data
     * @param content The raw multipart content as byte array
     * @return The extracted file content as string
     * @throws ConfigurationException if the multipart data cannot be parsed
     */
    public String extractFileContent(byte[] content) throws ConfigurationException {
        if (content == null || content.length == 0) {
            throw new ConfigurationException("No multipart content provided");
        }
        
        try {
            String contentStr = new String(content, "UTF-8");
            return parseMultipartContent(contentStr);
        } catch (Exception e) {
            throw new ConfigurationException("Failed to parse multipart content: " + e.getMessage(), e);
        }
    }
    
    /**
     * @brief Parses the multipart content string to extract file data
     * @param contentStr The multipart content as string
     * @return The extracted file content
     * @throws ConfigurationException if parsing fails
     */
    private String parseMultipartContent(String contentStr) throws ConfigurationException {
        String[] lines = contentStr.split("\\r\\n");
        boolean inFileSection = false;
        boolean foundContentStart = false;
        StringBuilder fileContent = new StringBuilder();
        String fileExtension = null;
        
        for (String line : lines) {
            if (line.contains("filename=")) {
                fileExtension = extractFileExtension(line);
                inFileSection = true;
                System.out.println("MultipartFileExtractor: Found " + fileExtension + " file section");
                continue;
            }
            
            if (inFileSection && line.trim().isEmpty() && !foundContentStart) {
                foundContentStart = true;
                continue;
            }
            
            if (inFileSection && foundContentStart) {
                if (line.startsWith("--")) {
                    // End of file content
                    break;
                }
                
                // Preserve line breaks for CONF format, compact for JSON
                if ("conf".equalsIgnoreCase(fileExtension)) {
                    fileContent.append(line).append("\n");
                } else {
                    fileContent.append(line);
                }
            }
        }
        
        String result = fileContent.toString().trim();
        if (result.isEmpty()) {
            throw new ConfigurationException("No file content found in multipart data");
        }
        
        System.out.println("MultipartFileExtractor: Extracted " + fileExtension + " content (" + 
                         result.length() + " characters)");
        return result;
    }
    
    /**
     * @brief Extracts file extension from Content-Disposition header
     * @param dispositionLine The Content-Disposition header line
     * @return The file extension (e.g., "json", "conf")
     */
    private String extractFileExtension(String dispositionLine) {
        // Look for filename="something.ext"
        int filenameStart = dispositionLine.indexOf("filename=");
        if (filenameStart == -1) {
            return "unknown";
        }
        
        int quoteStart = dispositionLine.indexOf('"', filenameStart);
        if (quoteStart == -1) {
            return "unknown";
        }
        
        int quoteEnd = dispositionLine.indexOf('"', quoteStart + 1);
        if (quoteEnd == -1) {
            return "unknown";
        }
        
        String filename = dispositionLine.substring(quoteStart + 1, quoteEnd);
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1).toLowerCase();
        }
        
        return "unknown";
    }
}
