package be.rubus.microstream.serializer.microstream;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import be.rubus.microstream.serializer.data.shop.Shop;
import one.microstream.persistence.binary.util.Serializer;
import one.microstream.persistence.binary.util.SerializerFoundation;

import java.util.ArrayList;
import java.util.List;

public class SizeRun3
{

    public static void main(final String[] args) throws Exception
    {
        System.out.printf("MicroStream test run Scenario 3 %n");
        final List<Shop> allShops = GenerateData.testShopData();

        final SerializerFoundation<?> foundation = SerializerFoundation.New();

        // warmup
        final byte[] bytes = serialize(allShops, foundation);
        System.out.printf("Serialized byte length %s %n", bytes.length);

        final List<Long> timings = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            timings.add(serializeWithTiming(allShops, foundation));
        }
        System.out.println("timings");
        System.out.println(timings);

        System.out.printf("Deserialize %n");
        timings.clear();
        for (int i = 0; i < 10; i++)
        {
            timings.add(deserializeWithTiming(bytes, foundation));
        }
        System.out.println("timings");
        System.out.println(timings);

    }

    private static long serializeWithTiming(final List<Shop> allShops, final SerializerFoundation<?> foundation) throws Exception
    {
        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        serialize(allShops, foundation);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static byte[] serialize(final List<Shop> allShops, final SerializerFoundation<?> foundation) throws Exception
    {
        final byte[] result;
        try (final Serializer<byte[]> serializer = Serializer.Bytes(foundation))
        {

            result = serializer.serialize(allShops);
        }
        return result;
    }

    private static long deserializeWithTiming(final byte[] bytes, final SerializerFoundation<?> foundation) throws Exception
    {
        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        deserialize(bytes, foundation);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static List<Product> deserialize(final byte[] bytes, final SerializerFoundation<?> foundation) throws Exception
    {
        final List<Product> result;
        try (final Serializer<byte[]> serializer = Serializer.Bytes(foundation))
        {
            result = serializer.deserialize(bytes);
        }
        return result;
    }

}
