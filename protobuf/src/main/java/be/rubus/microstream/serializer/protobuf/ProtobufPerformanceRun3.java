package be.rubus.microstream.serializer.protobuf;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.shop.Shop;
import one.microstream.compare.serializer.proto.model.ShopOuterClass;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
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
public class ProtobufPerformanceRun3
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(ProtobufPerformanceRun3.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<Shop> originalTestData;

    private ShopOuterClass.Shops data;

    private byte[] serializedContent;

    @Setup
    public void init() throws IOException
    {
        this.originalTestData = GenerateData.testShopData(true);
        this.data = Helper.createProtoVariant(this.originalTestData);

        this.prepareDeserializedData();
    }

    private void prepareDeserializedData() throws IOException
    {

        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try
        {
            this.data.writeTo(bytes);
            bytes.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        serializedContent = bytes.toByteArray();

    }

    @TearDown
    public void shutdown() throws Exception
    {
        // So that we are consistent with all other tests
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public byte[] serialize()
    {

        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try
        {
            this.data.writeTo(bytes);
            bytes.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return bytes.toByteArray();

    }


    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserialize(final Blackhole blackhole)
    {
        final ByteArrayInputStream in = new ByteArrayInputStream(serializedContent);

        final ShopOuterClass.Shops result;
        try
        {
            result = ShopOuterClass.Shops.newBuilder()
                    .mergeFrom(in)
                    .build();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        blackhole.consume(result);

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public byte[] serializeWithMapping()
    {

        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try
        {
            Helper.createProtoVariant(this.originalTestData)
                    .writeTo(bytes);
            bytes.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return bytes.toByteArray();

    }


    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserializeWithMapping(final Blackhole blackhole)
    {
        final ByteArrayInputStream in = new ByteArrayInputStream(serializedContent);

        final ShopOuterClass.Shops result;
        List<Shop> shops;
        try
        {
            result = ShopOuterClass.Shops.newBuilder()
                    .mergeFrom(in)
                    .build();
            shops = Helper.createPojoVariant(result);
            Helper.useShopReference(shops);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        blackhole.consume(shops);

    }
}
