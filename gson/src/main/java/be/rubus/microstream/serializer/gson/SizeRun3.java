package be.rubus.microstream.serializer.gson;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import be.rubus.microstream.serializer.data.shop.Shop;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * This will not success due to the circular reference (unless adding specific writers)
 */
public class SizeRun3
{

    public static void main(final String[] args) throws Exception
    {
        System.out.printf("GSON test run Scenario 3 %n");
        final List<Shop> allShops = GenerateData.testShopData();


        final Gson gson = new Gson();

        // warmup
        final byte[] bytes = serialize(allShops, gson);
        System.out.printf("Serialized byte length %s %n", bytes.length);

        final List<Long> timings = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            timings.add(serializeWithTiming(allShops, gson));
        }
        System.out.println("timings");
        System.out.println(timings);

        System.out.printf("Deserialize %n");
        timings.clear();
        for (int i = 0; i < 10; i++)
        {
            timings.add(deserializeWithTiming(bytes, gson));
        }
        System.out.println("timings");
        System.out.println(timings);

    }

    private static long serializeWithTiming(final List<Shop> allShops, final Gson gson)
    {

        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        serialize(allShops, gson);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static byte[] serialize(final List<Shop> allShops, final Gson gson)
    {

        return gson.toJson(allShops)
                .getBytes(StandardCharsets.UTF_8);
    }

    private static long deserializeWithTiming(final byte[] bytes, final Gson gson)
    {

        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        deserialize(bytes, gson);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static List<Product> deserialize(final byte[] bytes, final Gson gson)
    {

        final List<Product> products = gson.fromJson(new String(bytes), new TypeToken<ArrayList<Product>>()
        {
        }.getType());

        return products;


    }

}
