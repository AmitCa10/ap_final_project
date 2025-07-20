#!/bin/bash

echo "=== Checking ALL functions for Doxygen documentation ==="
echo

# Function to check if a method has documentation above it
check_method_docs() {
    local file="$1"
    local line_num="$2"
    
    # Check 10 lines before the method for /** or @brief
    local start=$((line_num - 10))
    if [ $start -lt 1 ]; then start=1; fi
    
    # Extract lines before the method
    sed -n "${start},$((line_num-1))p" "$file" | grep -E '(/\*\*|@brief)' > /dev/null
    return $?
}

# Find all Java files and check methods
undocumented_methods=()

find src -name "*.java" -type f | while read file; do
    echo "Checking $file..."
    
    # Find all method/function declarations (public, private, protected, package-private)
    # This regex looks for method signatures
    grep -n -E '^\s*(public|private|protected|\s+)\s+.*\s+\w+\s*\([^)]*\)\s*(throws\s+\w+)?\s*\{?$' "$file" | while IFS=: read line_num method_sig; do
        # Skip constructors and simple getters/setters without complex logic
        if echo "$method_sig" | grep -qE '(class\s+|interface\s+|enum\s+)'; then
            continue
        fi
        
        # Check if this method has documentation
        if ! check_method_docs "$file" "$line_num"; then
            echo "  ❌ Line $line_num: $method_sig"
        fi
    done
done
