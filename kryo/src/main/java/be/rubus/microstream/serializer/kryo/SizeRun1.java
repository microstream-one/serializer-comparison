package be.rubus.microstream.serializer.kryo;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SizeRun1
{

    public static void main(final String[] args)
    {
        System.out.printf("Kryo test run %n");
        final List<Product> allProducts = GenerateData.products(10_000);


        final Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.register(ArrayList.class);
        kryo.register(Product.class);

        // warmup
        final byte[] bytes = serialize(allProducts, kryo);
        System.out.printf("Serialized byte length %s %n", bytes.length);

        final List<Long> timings = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            timings.add(serializeWithTiming(allProducts, kryo));
        }
        System.out.println("timings");
        System.out.println(timings);

        System.out.printf("Deserialize %n");
        timings.clear();
        for (int i = 0; i < 10; i++)
        {
            timings.add(deserializeWithTiming(bytes, kryo));
        }
        System.out.println("timings");
        System.out.println(timings);

    }

    private static long serializeWithTiming(final List<Product> allProducts, final Kryo kryo)
    {

        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        serialize(allProducts, kryo);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static byte[] serialize(final List<Product> allProducts, final Kryo kryo)
    {


        final ByteArrayOutputStream data = new ByteArrayOutputStream();
        final Output output = new Output(data);

        kryo.writeClassAndObject(output, allProducts);
        output.close();
        return data.toByteArray();
    }

    private static long deserializeWithTiming(final byte[] bytes, final Kryo kryo)
    {

        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        deserialize(bytes, kryo);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static List<Product> deserialize(final byte[] bytes, final Kryo kryo)
    {


        try (final Input input = new Input(bytes))
        {
            return (List<Product>) kryo.readClassAndObject(input);
        }

    }

}
