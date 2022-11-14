package be.rubus.microstream.serializer.yaml;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class YAMLPerformanceRun1
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(YAMLPerformanceRun1.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<Product> allProducts;

    private Yaml reusedYaml;

    private byte[] serializedContent;

    @Setup
    public void init()
    {
        this.allProducts = GenerateData.products(10_000);

        this.reusedYaml = new Yaml();
        this.prepareDeserializedData();
    }

    private void prepareDeserializedData()
    {
        final Yaml yaml = new Yaml();

        this.serializedContent = yaml.dumpAs(this.allProducts, Tag.BINARY, null)
                .getBytes(Charset.defaultCharset());

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

        final Yaml yaml = new Yaml();

        final byte[] data;

        data = yaml.dumpAs(this.allProducts, Tag.BINARY, null)
                .getBytes(Charset.defaultCharset());

        return data;

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public byte[] serializeReuseMapper()
    {
        final byte[] data;

        data = this.reusedYaml.dumpAs(this.allProducts, Tag.BINARY, null)
                .getBytes(Charset.defaultCharset());

        return data;

        //return data;
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<Product> deserializeWithInitialization()
    {

        final Yaml yaml = new Yaml();

        final List<Product> products;
        try
        {
            ByteArrayInputStream data = new ByteArrayInputStream(this.serializedContent);

            products = yaml.loadAs(data, List.class);
            data.close();
        } catch (IOException e)
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
            ByteArrayInputStream data = new ByteArrayInputStream(this.serializedContent);

            products = this.reusedYaml.loadAs(data, List.class);
            data.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return products;
    }
}
