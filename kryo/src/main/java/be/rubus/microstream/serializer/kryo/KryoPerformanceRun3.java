package be.rubus.microstream.serializer.kryo;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.shop.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class KryoPerformanceRun3
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(KryoPerformanceRun3.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<Shop> allShops;

    private Kryo reusedKryo;

    private byte[] serializedContent;

    @Setup
    public void init()
    {
        //System.setProperty("kryo.unsafe", "false"); Does not avoid all reflection warnings/errors
        this.allShops = GenerateData.testShopData(true);

        this.reusedKryo = createKryoInstance();

        this.prepareDeserializedData();
    }

    private void prepareDeserializedData()
    {

        final Kryo kryo = createKryoInstance();

        final ByteArrayOutputStream data = new ByteArrayOutputStream();
        final Output output = new Output(data);

        kryo.writeClassAndObject(output, this.allShops);
        output.close();
        this.serializedContent = data.toByteArray();

    }

    private Kryo createKryoInstance()
    {
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
        return kryo;
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

        final Kryo kryo = createKryoInstance();

        final ByteArrayOutputStream data = new ByteArrayOutputStream();
        final Output output = new Output(data);

        kryo.writeClassAndObject(output, this.allShops);
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

        this.reusedKryo.writeClassAndObject(output, this.allShops);
        output.close();

        return data.toByteArray();
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<Shop> deserializeWithInitialization()
    {

        final Kryo kryo = createKryoInstance();

        final Input input = new Input(this.serializedContent);
        final List<Shop> data = (List<Shop>) kryo.readClassAndObject(input);
        input.close();
        return data;
    }


    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<Shop> deserializeWithReuse()
    {

        final Input input = new Input(this.serializedContent);
        final List<Shop> data = (List<Shop>) this.reusedKryo.readClassAndObject(input);
        input.close();
        return data;

    }
}
