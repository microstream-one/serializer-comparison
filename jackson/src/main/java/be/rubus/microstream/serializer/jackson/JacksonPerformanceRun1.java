package be.rubus.microstream.serializer.jackson;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JacksonPerformanceRun1
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(JacksonPerformanceRun1.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<Product> allProducts;
    private ObjectMapper reusedObjectMapper;

    private byte[] serializedContent;

    @Setup
    public void init()
    {
        this.allProducts = GenerateData.products(10_000);

        this.reusedObjectMapper = new ObjectMapper();
        this.prepareDeserializedData();
    }

    private void prepareDeserializedData()
    {
        final ObjectMapper objectMapper = new ObjectMapper();

        try
        {
            this.serializedContent = objectMapper.writeValueAsBytes(this.allProducts);
        } catch (final JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }

    }

    @TearDown
    public void shutdown() throws Exception
    {
        // So that we are consistent with all other tests
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public byte[] serializeWithInitialization()
    {

        final ObjectMapper objectMapper = new ObjectMapper();

        final byte[] data;
        try
        {
            data = objectMapper.writeValueAsBytes(this.allProducts);

        } catch (final JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }

        return data;

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public byte[] serializeReuseMapper()
    {
        final byte[] data;

        try
        {
            data = this.reusedObjectMapper.writeValueAsBytes(this.allProducts);

        } catch (final JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
        return data;
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<Product> deserializeWithInitialization()
    {

        final ObjectMapper objectMapper = new ObjectMapper();

        final List<Product> products;
        try
        {
            products = objectMapper.readValue(this.serializedContent, new TypeReference<List<Product>>()
            {
            });
        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

        return products;

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<Product> deserializeReuseMapper()
    {

        final List<Product> products;
        try
        {
            products = this.reusedObjectMapper.readValue(this.serializedContent, new TypeReference<List<Product>>()
            {
            });

        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

        return products;
    }
}
