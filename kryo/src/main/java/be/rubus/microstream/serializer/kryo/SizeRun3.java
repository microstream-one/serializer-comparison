package be.rubus.microstream.serializer.kryo;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import be.rubus.microstream.serializer.data.shop.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SizeRun3
{

    public static void main(final String[] args)
    {
        System.out.printf("Kryo test run Scenario 3 %n");
        final List<Shop> allShops = GenerateData.testShopData();


        final Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.register(ArrayList.class);
        kryo.register(Shop.class);
        kryo.register(Order.class);
        kryo.register(OrderLine.class);
        kryo.register(ShopProduct.class);
        kryo.register(StockItem.class);
        kryo.register(Warehouse.class);
        kryo.register(LocalDate.class);

        // warmup
        final byte[] bytes = serialize(allShops, kryo);
        System.out.printf("Serialized byte length %s %n", bytes.length);

        final List<Long> timings = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            timings.add(serializeWithTiming(allShops, kryo));
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

    private static long serializeWithTiming(final List<Shop> allShops, final Kryo kryo)
    {

        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        serialize(allShops, kryo);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static byte[] serialize(final List<Shop> allShops, final Kryo kryo)
    {


        final ByteArrayOutputStream data = new ByteArrayOutputStream();
        final Output output = new Output(data);

        kryo.writeClassAndObject(output, allShops);
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
