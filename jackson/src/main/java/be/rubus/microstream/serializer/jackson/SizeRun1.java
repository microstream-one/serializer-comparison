package be.rubus.microstream.serializer.jackson;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SizeRun1
{

    public static void main(final String[] args) throws Exception
    {
        System.out.printf("Jackson test run %n");
        final List<Product> allProducts = GenerateData.products(10_000);


        final ObjectMapper mapper = new ObjectMapper();

        // warmup
        final byte[] bytes = serialize(allProducts, mapper);
        System.out.printf("Serialized byte length %s %n", bytes.length);

        final List<Long> timings = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            timings.add(serializeWithTiming(allProducts, mapper));
        }
        System.out.println("timings");
        System.out.println(timings);

        System.out.printf("Deserialize %n");
        timings.clear();
        for (int i = 0; i < 10; i++)
        {
            timings.add(deserializeWithTiming(bytes, mapper));
        }
        System.out.println("timings");
        System.out.println(timings);

    }

    private static long serializeWithTiming(final List<Product> allProducts, final ObjectMapper mapper) throws Exception
    {

        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        serialize(allProducts, mapper);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static byte[] serialize(final List<Product> allProducts, final ObjectMapper mapper) throws Exception
    {

        return mapper.writeValueAsBytes(allProducts);
    }

    private static long deserializeWithTiming(final byte[] bytes, final ObjectMapper mapper) throws Exception
    {

        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        deserialize(bytes, mapper);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static List<Product> deserialize(final byte[] bytes, final ObjectMapper mapper)
    {

        final List<Product> products;
        try
        {
            products = mapper.readValue(bytes, new TypeReference<List<Product>>()
            {
            });

        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

        return products;


    }

}
