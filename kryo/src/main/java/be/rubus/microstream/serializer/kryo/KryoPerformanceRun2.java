package be.rubus.microstream.serializer.kryo;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.SomeData;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class KryoPerformanceRun2
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(KryoPerformanceRun2.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<SomeData> testData;

    private Kryo reusedKryo;

    private List<byte[]> serializedContent;

    @Setup
    public void init()
    {
        this.testData = GenerateData.testData(100_000);

        this.reusedKryo = new Kryo();

        this.reusedKryo.register(SomeData.class);
        this.reusedKryo.register(LocalDateTime.class);
        this.reusedKryo.register(BigDecimal.class);
        this.reusedKryo.register(int[].class);

        this.prepareDeserializedData();
    }

    private void prepareDeserializedData()
    {

        final Kryo kryo = new Kryo();

        kryo.register(SomeData.class);
        kryo.register(LocalDateTime.class);
        kryo.register(BigDecimal.class);
        kryo.register(int[].class);

        this.serializedContent = new ArrayList<>();

        for (final SomeData input : this.testData)
        {
            final ByteArrayOutputStream data = new ByteArrayOutputStream();
            final Output output = new Output(data);

            kryo.writeClassAndObject(output, input);
            output.close();
            this.serializedContent.add(data.toByteArray());

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
    public void serializeWithInitialization(final Blackhole blackhole)
    {

        final Kryo kryo = new Kryo();

        kryo.register(SomeData.class);
        kryo.register(LocalDateTime.class);
        kryo.register(BigDecimal.class);
        kryo.register(int[].class);

        for (final SomeData input : this.testData)
        {
            final ByteArrayOutputStream data = new ByteArrayOutputStream();
            final Output output = new Output(data);
            kryo.writeClassAndObject(output, input);
            output.close();
            blackhole.consume(output.toBytes());
        }
    }

    @Benchmark
    @Warmup(iterations = 1)
    @Measurement(iterations = 5)
    public void serializeWithReuse(final Blackhole blackhole)
    {
        for (final SomeData input : this.testData)
        {
            final ByteArrayOutputStream data = new ByteArrayOutputStream();
            final Output output = new Output(data);
            this.reusedKryo.writeClassAndObject(output, input);
            output.close();
            blackhole.consume(output.toBytes());
        }

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserializeWithInitialization(final Blackhole blackhole)
    {

        final Kryo kryo = new Kryo();

        kryo.register(SomeData.class);
        kryo.register(LocalDateTime.class);
        kryo.register(BigDecimal.class);
        kryo.register(int[].class);

        for (final byte[] data : this.serializedContent)
        {
            final Input input = new Input(data);

            final SomeData someData = (SomeData) kryo.readClassAndObject(input);
            input.close();
            blackhole.consume(someData);
        }

    }


    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserializeWithReuse(final Blackhole blackhole)
    {

        for (final byte[] data : this.serializedContent)
        {
            final Input input = new Input(data);

            final SomeData someData = (SomeData) this.reusedKryo.readClassAndObject(input);
            input.close();
            blackhole.consume(someData);
        }
    }
}
