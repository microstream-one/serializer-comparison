package be.rubus.microstream.serializer.yaml;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class SizeRun1
{

    public static void main(final String[] args) throws Exception
    {
        System.out.printf("YAML test run %n");
        final List<Product> allProducts = GenerateData.products(10_000);

        final Yaml mapper = new Yaml();

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

    private static long serializeWithTiming(final List<Product> allProducts, final Yaml yaml) throws Exception
    {

        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        serialize(allProducts, yaml);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static byte[] serialize(final List<Product> allProducts, final Yaml yaml) throws Exception
    {

        return yaml.dumpAs(allProducts, Tag.BINARY, null).getBytes(Charset.defaultCharset());
    }

    private static long deserializeWithTiming(final byte[] bytes, final Yaml yaml) throws Exception
    {

        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        deserialize(bytes, yaml);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static List<Product> deserialize(final byte[] bytes, final Yaml yaml)
    {

        final List<Product> products;
        try
        {
            ByteArrayInputStream data = new ByteArrayInputStream(bytes);
            products = yaml.loadAs(data, List.class);
            data.close();
        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

        return products;


    }

}
