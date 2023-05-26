package be.rubus.microstream.serializer.protobuf;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.shop.Shop;
import one.microstream.compare.serializer.proto.model.ShopOuterClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SizeRun3
{

    public static void main(final String[] args) throws Exception
    {

        System.out.printf("ProtoBuf test run Scenario 3 %n");
        final List<Shop> allShops = GenerateData.testShopData(true);

        // warmup
        ShopOuterClass.Shops protoVariant = Helper.createProtoVariant(allShops);
        final byte[] bytes = serialize(protoVariant);
        System.out.printf("Serialized byte length %s %n", bytes.length);

        final List<Long> timings = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            timings.add(serializeWithTiming(protoVariant));
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

    private static long serializeWithTiming(final ShopOuterClass.Shops shops)
    {
        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        serialize(shops);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static byte[] serialize(final ShopOuterClass.Shops shops)
    {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try
        {
            shops.writeTo(bytes);
            bytes.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return bytes.toByteArray();
    }

    private static long deserializeWithTiming(final byte[] bytes) throws Exception
    {
        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        deserialize(bytes);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static ShopOuterClass.Shops deserialize(final byte[] serializedContent) throws Exception
    {
        final ByteArrayInputStream in = new ByteArrayInputStream(serializedContent);

        final ShopOuterClass.Shops result;
        try
        {
            result = ShopOuterClass.Shops.newBuilder()
                    .mergeFrom(in)
                    .build();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return result;
    }

}
