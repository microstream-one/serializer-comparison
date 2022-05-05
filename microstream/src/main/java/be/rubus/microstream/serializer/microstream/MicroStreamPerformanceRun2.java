package be.rubus.microstream.serializer.microstream;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.SomeData;
import one.microstream.persistence.binary.util.Serializer;
import one.microstream.persistence.binary.util.SerializerFoundation;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class MicroStreamPerformanceRun2
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(MicroStreamPerformanceRun2.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<SomeData> testData;

    private SerializerFoundation<?> foundation;

    private Serializer<byte[]> reusedSerializer;

    private List<byte[]> serializedContent;

    @Setup
    public void init()
    {
        this.testData = GenerateData.testData(100_000);

        this.foundation = SerializerFoundation.New();
        this.foundation.registerEntityTypes(
                ArrayList.class,
                SomeData.class,
                LocalDateTime.class
        );
        this.reusedSerializer = Serializer.Bytes(this.foundation);

        this.prepareDeserializedData();
    }

    private void prepareDeserializedData()
    {
        this.serializedContent = new ArrayList<>();
        try (final Serializer<byte[]> serializer = Serializer.Bytes(this.foundation))
        {
            for (final SomeData input : this.testData)
            {
                this.serializedContent.add(serializer.serialize(input));
            }

        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

    }

    @TearDown
    public void shutdown() throws Exception
    {
        this.reusedSerializer.close();
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void serializeWithInitialization(final Blackhole blackhole)
    {

        final SerializerFoundation<?> localFoundation = SerializerFoundation.New();
        localFoundation.registerEntityTypes(
                ArrayList.class,
                SomeData.class,
                LocalDateTime.class
        );


        try (final Serializer<byte[]> serializer = Serializer.Bytes(localFoundation))
        {

            for (final SomeData input : this.testData)
            {
                blackhole.consume(serializer.serialize(input));
            }
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void serializeReuseFoundation(final Blackhole blackhole)
    {

        try (final Serializer<byte[]> serializer = Serializer.Bytes(this.foundation))
        {

            for (final SomeData input : this.testData)
            {
                blackhole.consume(serializer.serialize(input));
            }
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void serializeWithReuse(final Blackhole blackhole)
    {

        for (final SomeData input : this.testData)
        {
            blackhole.consume(this.reusedSerializer.serialize(input));
        }
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserializeWithInitialization(final Blackhole blackhole)
    {

        final SerializerFoundation<?> localFoundation = SerializerFoundation.New();
        localFoundation.registerEntityTypes(
                ArrayList.class,
                SomeData.class,
                LocalDateTime.class
        );

        try (final Serializer<byte[]> serializer = Serializer.Bytes(localFoundation))
        {

            for (final byte[] input : this.serializedContent)
            {
                blackhole.consume(serializer.deserialize(input));
            }
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserializeReuseFoundation(final Blackhole blackhole)
    {

        try (final Serializer<byte[]> serializer = Serializer.Bytes(this.foundation))
        {

            for (final byte[] input : this.serializedContent)
            {
                blackhole.consume(serializer.deserialize(input));
            }
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserializeWithReuse(final Blackhole blackhole)
    {

        for (final byte[] input : this.serializedContent)
        {
            blackhole.consume(this.reusedSerializer.deserialize(input));
        }
    }

}
