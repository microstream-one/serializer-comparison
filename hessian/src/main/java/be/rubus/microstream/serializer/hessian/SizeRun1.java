package be.rubus.microstream.serializer.hessian;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SizeRun1
{

    public static void main(final String[] args) throws Exception
    {
        System.out.printf("Hessian test run %n");
        final List<Product> allProducts = GenerateData.products(10_000);


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Hessian2Output mapper = new Hessian2Output(bos);

        ByteArrayInputStream bis = new ByteArrayInputStream(new byte[]{});
        Hessian2Input deserializeMapper = new Hessian2Input(bis);

        // warmup
        byte[] bytes = serialize(allProducts, mapper);

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
            timings.add(deserializeWithTiming(bytes, deserializeMapper));
        }
        System.out.println("timings");
        System.out.println(timings);

    }

    private static long serializeWithTiming(final List<Product> allProducts, final Hessian2Output mapper) throws Exception
    {


        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        mapper.reset();
        serialize(allProducts, mapper);
        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static byte[] serialize(final List<Product> allProducts, final Hessian2Output mapper) throws Exception
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mapper.init(bos);

        mapper.startMessage();
        mapper.writeObject(allProducts);
        mapper.completeMessage();

        mapper.flush();
        mapper.free();

        return bos.toByteArray();
    }

    private static long deserializeWithTiming(final byte[] bytes, final Hessian2Input mapper) throws Exception
    {

        // Timings cannot be trusted since JVM performance optimizations.
        final long start = System.nanoTime();
        deserialize(bytes, mapper);

        final long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    private static List<Product> deserialize(final byte[] bytes, final Hessian2Input mapper)
    {

        final List<Product> products;
        try
        {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            mapper.init(bis);

            mapper.startMessage();
            products = (List<Product>) mapper.readObject(List.class);
            mapper.completeMessage();

            mapper.free();
            bis.close();

        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

        return products;


    }

}
