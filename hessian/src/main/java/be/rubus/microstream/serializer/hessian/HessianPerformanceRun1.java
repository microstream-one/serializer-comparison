package be.rubus.microstream.serializer.hessian;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class HessianPerformanceRun1
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(HessianPerformanceRun1.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<Product> allProducts;

    private Hessian2Output reusedSerializer;
    private Hessian2Input reusedDeserializer;

    private byte[] serializedContent;

    @Setup
    public void init()
    {
        this.allProducts = GenerateData.products(10_000);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.reusedSerializer = new Hessian2Output(bos);

        ByteArrayInputStream bis = new ByteArrayInputStream(new byte[]{});
        this.reusedDeserializer = new Hessian2Input(bis);

        this.prepareDeserializedData();
    }

    private void prepareDeserializedData()
    {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final Hessian2Output mapper = new Hessian2Output(bos);

        try
        {
            mapper.startMessage();
            mapper.writeObject(this.allProducts);

            mapper.completeMessage();
            mapper.flush();

            bos.close();
            this.serializedContent = bos.toByteArray();
        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    @TearDown
    public void shutdown() throws Exception
    {
        // So that we are consistent with all other tests
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public byte[] serializeWithInitialization()
    {

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final Hessian2Output mapper = new Hessian2Output(bos);

        final byte[] data;
        try
        {
            mapper.startMessage();
            mapper.writeObject(this.allProducts);

            mapper.completeMessage();

            bos.close();
            data = bos.toByteArray();
        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

        return data;

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public byte[] serializeReuseMapper()
    {
        final byte[] data;
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try
        {
            this.reusedSerializer.init(bos);
            this.reusedSerializer.startMessage();
            this.reusedSerializer.writeObject(this.allProducts);

            this.reusedSerializer.completeMessage();

            bos.close();
            data = bos.toByteArray();

            reusedSerializer.free();

        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
        return data;
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<Product> deserializeWithInitialization()
    {

        final ByteArrayInputStream bis = new ByteArrayInputStream(this.serializedContent);
        final Hessian2Input mapper = new Hessian2Input(bis);

        final List<Product> products;
        try
        {
            mapper.startMessage();
            products = (List<Product>) mapper.readObject(List.class);
            mapper.completeMessage();

        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

        return products;

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<Product> deserializeReuseMapper()
    {

        final List<Product> products;
        final ByteArrayInputStream bis = new ByteArrayInputStream(this.serializedContent);

        try
        {
            this.reusedDeserializer.init(bis);
            this.reusedDeserializer.startMessage();

            products = (List<Product>) this.reusedDeserializer.readObject(List.class);

            this.reusedDeserializer.completeMessage();
            this.reusedDeserializer.free();

        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

        return products;
    }
}
