# Serializer Comparison

Compare the usage, performance and data sizes of several Java Serialization frameworks.

**Binary**

- JVM native solution
- MicroStream Serializer
- Kryo
- CBOR (implemented by Jackson CBOR dataformat)
- Hessian

**JSON**

- Jackson
- GSON

**XML**

- JAXB

**YAML**

- snakeYAML

## Test method

The performance is measured through the _Java Microbenchmark Harness_ JMH library.

In each module, you can find a class containing _PerformanceRun_ in it that executes the performance test for a certain
scenario.

## Scenarios

The following scenarios are tested

- Scenario1: 1 single List with a large number of objects.
- Scenario2: Serialize and deserialize many small objects
- Scenario3: Object graph with Circular dependency **Not implemented yet** (Will adapt the Bookstore demo model for this purpose)

## Results

See confluence page 
https://microstream.atlassian.net/wiki/spaces/MSG/pages/1958674451/Serializer

## TODO

Compare with specialized serializers like

- Avro
- Protobuf

Add the scenario with the circular reference.