package be.rubus.microstream.serializer.microstream;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import be.rubus.microstream.serializer.data.shop.*;
import one.microstream.persistence.binary.util.Serializer;
import one.microstream.persistence.binary.util.SerializerFoundation;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class MicroStreamPerformanceRun3
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder().include(MicroStreamPerformanceRun3.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<Shop> allShops;
    private SerializerFoundation<?> foundation;

    private Serializer<byte[]> reusedSerializer;

    private byte[] serializedContent;

    @Setup
    public void init()
    {
        this.allShops = GenerateData.testShopData();

        this.foundation = SerializerFoundation.New();
        this.foundation.registerEntityTypes(ArrayList.class, Shop.class, Order.class, OrderLine.class, ShopProduct.class, StockItem.class, Warehouse.class);

        this.prepareDeserializedData();
        this.reusedSerializer = Serializer.Bytes(this.foundation);
    }

    private void prepareDeserializedData()
    {
        try (final Serializer<byte[]> serializer = Serializer.Bytes(this.foundation))
        {
            this.serializedContent = serializer.serialize(this.allShops);
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
    public byte[] serializeReuseFoundation()
    {

        final byte[] data;
        try (final Serializer<byte[]> serializer = Serializer.Bytes(this.foundation))
        {
            data = serializer.serialize(this.allShops);
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
        return data;  // To reduce the chance of JVM optimizations and removing the serialisation call :)
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public byte[] serializeWithInitialization()
    {

        final SerializerFoundation<?> foundationLocal = SerializerFoundation.New();
        foundationLocal.registerEntityTypes(ArrayList.class, Shop.class, Order.class, OrderLine.class, ShopProduct.class, StockItem.class, Warehouse.class);

        final byte[] data;
        try (final Serializer<byte[]> serializer = Serializer.Bytes(this.foundation))
        {
            data = serializer.serialize(this.allShops);
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
        return data;  // To reduce the chance of JVM optimizations and removing the serialisation call :)
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public byte[] serializeWithReuse()
    {

        return this.reusedSerializer.serialize(this.allShops);

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<Product> deserializeWithInitialization()
    {

        final SerializerFoundation<?> foundationLocal = SerializerFoundation.New();
        foundationLocal.registerEntityTypes(ArrayList.class, Shop.class, Order.class, OrderLine.class, ShopProduct.class, StockItem.class, Warehouse.class);

        final List<Product> data;
        try (final Serializer<byte[]> serializer = Serializer.Bytes(foundationLocal))
        {
            data = serializer.deserialize(this.serializedContent);
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
        return data;
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<Product> deserializeReuseFoundation()
    {

        final List<Product> data;
        try (final Serializer<byte[]> serializer = Serializer.Bytes(this.foundation))
        {
            data = serializer.deserialize(this.serializedContent);
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
        return data;
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<Product> deserializeWithReuse()
    {

        return this.reusedSerializer.deserialize(this.serializedContent);

    }

}
