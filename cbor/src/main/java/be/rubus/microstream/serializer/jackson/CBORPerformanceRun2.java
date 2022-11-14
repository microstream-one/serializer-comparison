package be.rubus.microstream.serializer.jackson;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.SomeData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class CBORPerformanceRun2
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(CBORPerformanceRun2.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<SomeData> testData;

    private CBORMapper reusedObjectMapper;

    private List<byte[]> serializedContent;

    @Setup
    public void init()
    {
        this.testData = GenerateData.testData(100_000);

        this.reusedObjectMapper = new CBORMapper();
        this.reusedObjectMapper.registerModule(new JavaTimeModule());
        this.reusedObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        this.prepareDeserializedData();
    }

    private void prepareDeserializedData()
    {
        final CBORMapper objectMapper = new CBORMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        this.serializedContent = new ArrayList<>();

        for (final SomeData input : this.testData)
        {

            try
            {
                this.serializedContent.add(objectMapper.writeValueAsBytes(input));

            } catch (final JsonProcessingException e)
            {
                throw new RuntimeException(e);
            }
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
    public void serializeWithInitialization(final Blackhole blackhole)
    {

        final CBORMapper objectMapper = new CBORMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try
        {
            for (final SomeData input : this.testData)
            {
                blackhole.consume(objectMapper.writeValueAsBytes(input));
            }
        } catch (final JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }


    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void serializeReuseMapper(final Blackhole blackhole)
    {

        try
        {
            for (final SomeData input : this.testData)
            {
                blackhole.consume(this.reusedObjectMapper.writeValueAsBytes(input));
            }
        } catch (final JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserializeWithInitialization(final Blackhole blackhole)
    {

        final CBORMapper objectMapper = new CBORMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try
        {
            for (final byte[] input : this.serializedContent)
            {
                blackhole.consume(objectMapper.readValue(input, SomeData.class));
            }

        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }


    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserializeReuseMapper(final Blackhole blackhole)
    {

        try
        {
            for (final byte[] input : this.serializedContent)
            {
                blackhole.consume(this.reusedObjectMapper.readValue(input, SomeData.class));
            }

        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

    }
}
