package be.rubus.microstream.serializer.protobuf;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import one.microstream.compare.serializer.proto.model.ProductOuterClass;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class ProtobufPerformanceRun1
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(ProtobufPerformanceRun1.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private ProductOuterClass.ProductList allProducts;

    private byte[] serializedContent;

    @Setup
    public void init()
    {
        final List<Product> products = GenerateData.products(10_000);
        final ProductOuterClass.ProductList.Builder productListBuilder = ProductOuterClass.ProductList.newBuilder();
        products.stream()
                .map(Helper::createProtoVariant)
                .forEach(productListBuilder::addProductEntity);

        this.allProducts = productListBuilder.build();

        this.prepareDeserializedData();
    }

    private void prepareDeserializedData()
    {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try
        {
            this.allProducts.writeTo(bytes);
            bytes.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        this.serializedContent = bytes.toByteArray();

    }

    @TearDown
    public void shutdown() throws Exception
    {
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public byte[] serialize()
    {

        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try
        {
            this.allProducts.writeTo(bytes);
            bytes.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return bytes.toByteArray();
        // To reduce the chance of JVM optimizations and removing the serialisation call :)
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public ProductOuterClass.ProductList deserialize()
    {

        final ByteArrayInputStream in = new ByteArrayInputStream(this.serializedContent);

        final ProductOuterClass.ProductList result;
        try
        {
            result = ProductOuterClass.ProductList.newBuilder()
                    .mergeFrom(in)
                    .build();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return result;
    }
}
