package be.rubus.microstream.serializer.yaml;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.SomeData;
import be.rubus.microstream.serializer.yaml.custom.MyCustomConstructor;
import be.rubus.microstream.serializer.yaml.custom.MyCustomRepresenter;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class YAMLPerformanceRun2
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(YAMLPerformanceRun2.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<SomeData> testData;

    private Yaml reusedYamlOut;
    private Yaml reusedYamlIn;

    private List<byte[]> serializedContent;

    @Setup
    public void init()
    {
        this.testData = GenerateData.testData(100_000);

        this.reusedYamlOut = new Yaml(new MyCustomRepresenter());
        this.reusedYamlIn = new Yaml(new MyCustomConstructor());
        this.prepareDeserializedData();
    }

    private void prepareDeserializedData()
    {
        final Yaml yaml = new Yaml(new MyCustomRepresenter());

        this.serializedContent = new ArrayList<>();
        for (final SomeData input : this.testData)
        {
            this.serializedContent.add(yaml.dumpAs(input, Tag.BINARY, null)
                                               .getBytes(Charset.defaultCharset()));
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

        final Yaml yaml = new Yaml(new MyCustomRepresenter());

        for (final SomeData input : this.testData)
        {
            blackhole.consume(yaml.dumpAs(input, Tag.BINARY, null)
                                      .getBytes(Charset.defaultCharset()));
        }

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void serializeReuseMapper(final Blackhole blackhole)
    {
        for (final SomeData input : this.testData)
        {
            blackhole.consume(this.reusedYamlOut.dumpAs(input, Tag.BINARY, null)
                                      .getBytes(Charset.defaultCharset()));
        }

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserializeWithInitialization(Blackhole blackhole)
    {

        final Yaml yaml = new Yaml(new MyCustomConstructor());

        try
        {
            for (final byte[] input : this.serializedContent)
            {
                ByteArrayInputStream data = new ByteArrayInputStream(input);

                blackhole.consume(yaml.loadAs(data, List.class));
                data.close();

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
                ByteArrayInputStream data = new ByteArrayInputStream(input);

                blackhole.consume(this.reusedYamlIn.loadAs(data, List.class));
                data.close();

            }
        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

    }
}
