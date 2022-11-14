package be.rubus.microstream.serializer.jvm;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class JVMPerformanceRun1
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(JVMPerformanceRun1.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<Product> data;

    private byte[] serializedContent;

    @Setup
    public void init() throws IOException
    {
        this.data = GenerateData.products(10_000);

        this.prepareDeserializedData();
    }

    private void prepareDeserializedData() throws IOException
    {

        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(bytes);

        out.writeObject(this.data);
        out.close();

        this.serializedContent = bytes.toByteArray();

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

        final byte[] bytes;
        try
        {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            final ObjectOutputStream out = new ObjectOutputStream(stream);

            out.writeObject(this.data);
            out.close();

            bytes = stream.toByteArray();
        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

        return bytes;

    }


    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<Product> deserializeWithInitialization()
    {
        final List<Product> data;
        try
        {
            final ByteArrayInputStream bytes = new ByteArrayInputStream(this.serializedContent);
            final ObjectInputStream in = new ObjectInputStream(bytes);

            data = (List<Product>) in.readObject();

            in.close();
        } catch (final IOException | ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        return data;

    }
}
