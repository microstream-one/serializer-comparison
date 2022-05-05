package be.rubus.microstream.serializer.jvm;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SizeRun1
{

    public static void main(final String[] args) throws Exception
    {
        System.out.printf("JVM Native test run %n");
        final List<Product> allProducts = GenerateData.products(10_000);


        // warmup
        final byte[] bytes = serialize(allProducts);
        System.out.printf("Serialized byte length %s %n", bytes.length);

        final List<Long> timings = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            timings.add(serializeWithTiming(allProducts));
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

    private static long serializeWithTiming(final List<Product> allProducts) throws Exception
    {
        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        serialize(allProducts);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static byte[] serialize(final List<Product> allProducts) throws Exception
    {


        final byte[] bytes;
        try
        {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            final ObjectOutputStream out = new ObjectOutputStream(stream);

            out.writeObject(allProducts);
            out.close();

            bytes = stream.toByteArray();
        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

        return bytes;
    }

    private static long deserializeWithTiming(final byte[] bytes) throws Exception
    {
        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        deserialize(bytes);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static List<Product> deserialize(final byte[] bytes) throws Exception
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
