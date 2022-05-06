package be.rubus.microstream.serializer.jackson;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import be.rubus.microstream.serializer.data.shop.Shop;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This will not success due to the circular reference (unless adding specific writers)
 */
public class SizeRun3
{

    public static void main(final String[] args) throws Exception
    {
        System.out.printf("Jackson test run Scenario 3 %n");
        final List<Shop> allShops = GenerateData.testShopData();


        final ObjectMapper mapper = new ObjectMapper();

        // warmup
        final byte[] bytes = serialize(allShops, mapper);
        System.out.printf("Serialized byte length %s %n", bytes.length);

        final List<Long> timings = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            timings.add(serializeWithTiming(allShops, mapper));
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

    private static long serializeWithTiming(final List<Shop> allShops, final ObjectMapper mapper) throws Exception
    {

        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        serialize(allShops, mapper);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static byte[] serialize(final List<Shop> allShops, final ObjectMapper mapper) throws Exception
    {

        return mapper.writeValueAsBytes(allShops);
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
