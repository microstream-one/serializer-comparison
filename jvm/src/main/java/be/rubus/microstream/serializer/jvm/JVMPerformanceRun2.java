package be.rubus.microstream.serializer.jvm;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.SomeData;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JVMPerformanceRun2
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(JVMPerformanceRun2.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<SomeData> testData;

    private List<byte[]> serializedContent;

    @Setup
    public void init()
    {
        this.testData = GenerateData.testData(100_000);

        this.prepareDeserializedData();
    }

    private void prepareDeserializedData()
    {

        this.serializedContent = new ArrayList<>();

        for (final SomeData input : this.testData)
        {

            try
            {
                final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                final ObjectOutputStream out = new ObjectOutputStream(bytes);

                out.writeObject(input);
                out.close();

                this.serializedContent.add(bytes.toByteArray());

            } catch (final IOException e)
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

        for (final SomeData input : this.testData)
        {

            try
            {
                final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                final ObjectOutputStream out = new ObjectOutputStream(bytes);

                out.writeObject(input);
                out.close();

                blackhole.consume(bytes.toByteArray());

            } catch (final IOException e)
            {
                throw new RuntimeException(e);
            }
        }

    }


    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserializeWithInitialization(final Blackhole blackhole) throws IOException, ClassNotFoundException
    {

        for (final byte[] input : this.serializedContent)
        {

            final ByteArrayInputStream bytes = new ByteArrayInputStream(input);
            final ObjectInputStream in = new ObjectInputStream(bytes);

            final SomeData someData = (SomeData) in.readObject();
            blackhole.consume(someData);
        }

    }

}
