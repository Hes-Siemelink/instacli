package main

import (
    "fmt"
    "io/ioutil"
    "log"

    "gopkg.in/yaml.v2"
)

// Define a type alias for []interface{}
type ArrayNode []interface{}
type ObjectNode map[interface{}]interface{}

func main() {
    // Read the YAML file
    data, err := ioutil.ReadFile("./specification/Hello.spec.yaml")
    if err != nil {
        log.Fatalf("error: %v", err)
    }

    // Parse the YAML file into a generic map
    var result map[interface{}]interface{}
    err = yaml.Unmarshal(data, &result)
    if err != nil {
        log.Fatalf("error: %v", err)
    }

    // Check the type of the "Print" field
    if value, ok := result["Print"]; ok {
        switch v := value.(type) {
        case string:
            fmt.Println("object is a string:", v)
        case int:
            fmt.Println("object is an int:", v)
        case bool:
            fmt.Println("object is a bool:", v)
        case ArrayNode:
            fmt.Println("object is an array:", v)
        case ObjectNode:
            fmt.Println("object is a map:", v)
        default:
            fmt.Println("object is of an unknown type")
        }
    } else {
        fmt.Println("object field not found")
    }
}