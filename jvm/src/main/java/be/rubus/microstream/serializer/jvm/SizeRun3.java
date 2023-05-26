package be.rubus.microstream.serializer.jvm;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import be.rubus.microstream.serializer.data.shop.Shop;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SizeRun3
{

    public static void main(final String[] args) throws Exception
    {
        System.out.printf("JVM Native test run Scenario 3 %n");
        final List<Shop> allShops = GenerateData.testShopData(true);

        // warmup
        final byte[] bytes = serialize(allShops);
        System.out.printf("Serialized byte length %s %n", bytes.length);

        final List<Long> timings = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            timings.add(serializeWithTiming(allShops));
        }
        System.out.println("timings");
        System.out.println(timings);

        System.out.printf("Deserialize %n");
        timings.clear();
        for (int i = 0; i < 10; i++)
        {
            timings.add(deserializeWithTiming(bytes));
        }
        System.out.println("timings");
        System.out.println(timings);

    }

    private static long serializeWithTiming(final List<Shop> allShops)
    {
        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        serialize(allShops);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static byte[] serialize(final List<Shop> allShops)
    {

        final byte[] bytes;
        try
        {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            final ObjectOutputStream out = new ObjectOutputStream(stream);

            out.writeObject(allShops);
            out.close();

            bytes = stream.toByteArray();
        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

        return bytes;
    }

    private static long deserializeWithTiming(final byte[] bytes)
    {
        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        deserialize(bytes);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static List<Product> deserialize(final byte[] bytes)
    {
        final List<Product> data;
        try
        {
            final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            final ObjectInputStream in = new ObjectInputStream(stream);

            data = (List<Product>) in.readObject();

        } catch (final IOException | ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        return data;

    }

}
