package be.rubus.microstream.serializer.protobuf;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import one.microstream.compare.serializer.proto.model.ProductOuterClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SizeRun1
{

    public static void main(final String[] args) throws Exception
    {
        System.out.printf("Protobuf test run %n");
        final List<Product> allProducts = GenerateData.products(10_000);
        final ProductOuterClass.ProductList.Builder productListBuilder = ProductOuterClass.ProductList.newBuilder();
        allProducts.stream()
                .map(Helper::createProtoVariant)
                .forEach(productListBuilder::addProductEntity);

        final ProductOuterClass.ProductList productList = productListBuilder.build();


        // warmup
        final byte[] bytes = serialize(productList);
        System.out.printf("Serialized byte length %s %n", bytes.length);

        final List<Long> timings = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            timings.add(serializeWithTiming(productList));
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

    private static long serializeWithTiming(final ProductOuterClass.ProductList productList)
    {
        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        serialize(productList);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static byte[] serialize(final ProductOuterClass.ProductList productList)
    {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try
        {
            productList.writeTo(bytes);
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

    private static ProductOuterClass.ProductList deserialize(final byte[] serializedContent) throws Exception
    {
        final ByteArrayInputStream in = new ByteArrayInputStream(serializedContent);

        final ProductOuterClass.ProductList result;
        try
        {
            result = ProductOuterClass.ProductList.newBuilder()
                    .mergeFrom(in)
                    .build();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return result;
    }

}
