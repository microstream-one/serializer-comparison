package be.rubus.microstream.serializer.kryo;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class KryoPerformanceRun1
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(KryoPerformanceRun1.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<Product> allProducts;

    private Kryo reusedKryo;

    private byte[] serializedContent;

    @Setup
    public void init()
    {
        //System.setProperty("kryo.unsafe", "false"); Does not avoid all reflection warnings/errors
        this.allProducts = GenerateData.products(10_000);

        this.reusedKryo = new Kryo();

        this.reusedKryo.register(ArrayList.class);
        this.reusedKryo.register(Product.class);

        this.prepareDeserializedData();
    }

    private void prepareDeserializedData()
    {

        final Kryo kryo = new Kryo();

        kryo.register(ArrayList.class);
        kryo.register(Product.class);

        final ByteArrayOutputStream data = new ByteArrayOutputStream();
        final Output output = new Output(data);

        kryo.writeClassAndObject(output, this.allProducts);
        output.close();
        this.serializedContent = data.toByteArray();


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

        final Kryo kryo = new Kryo();

        kryo.register(ArrayList.class);
        kryo.register(Product.class);

        final ByteArrayOutputStream data = new ByteArrayOutputStream();
        final Output output = new Output(data);

        kryo.writeClassAndObject(output, this.allProducts);
        output.close();

        return data.toByteArray();
    }

    @Benchmark
    @Warmup(iterations = 1)
    @Measurement(iterations = 5)
    public byte[] serializeWithReuse()
    {

        final ByteArrayOutputStream data = new ByteArrayOutputStream();
        final Output output = new Output(data);

        this.reusedKryo.writeClassAndObject(output, this.allProducts);
        output.close();

        return data.toByteArray();
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<Product> deserializeWithInitialization()
    {

        final Kryo kryo = new Kryo();

        kryo.register(ArrayList.class);
        kryo.register(Product.class);

        final Input input = new Input(this.serializedContent);
        final List<Product> data = (List<Product>) kryo.readClassAndObject(input);
        input.close();
        return data;
    }


    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<Product> deserializeWithReuse()
    {

        final Input input = new Input(this.serializedContent);
        final List<Product> data = (List<Product>) this.reusedKryo.readClassAndObject(input);
        input.close();
        return data;

    }
}
