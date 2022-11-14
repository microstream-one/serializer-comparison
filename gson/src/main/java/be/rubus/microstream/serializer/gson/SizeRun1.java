package be.rubus.microstream.serializer.gson;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SizeRun1
{

    public static void main(final String[] args) throws Exception
    {
        System.out.printf("GSON test run %n");
        final List<Product> allProducts = GenerateData.products(10_000);


        final Gson gson = new Gson();

        // warmup
        final byte[] bytes = serialize(allProducts, gson);
        System.out.printf("Serialized byte length %s %n", bytes.length);

        final List<Long> timings = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            timings.add(serializeWithTiming(allProducts, gson));
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

    private static long serializeWithTiming(final List<Product> allProducts, final Gson gson) throws Exception
    {

        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        serialize(allProducts, gson);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static byte[] serialize(final List<Product> allProducts, final Gson gson) throws Exception
    {

        return gson.toJson(allProducts)
                .getBytes(StandardCharsets.UTF_8);
    }

    private static long deserializeWithTiming(final byte[] bytes, final Gson gson) throws Exception
    {

        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        deserialize(bytes, gson);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static List<Product> deserialize(final byte[] bytes, final Gson gson)
    {

        return gson.fromJson(new String(bytes), new TypeToken<ArrayList<Product>>()
        {
        }.getType());

    }

}
