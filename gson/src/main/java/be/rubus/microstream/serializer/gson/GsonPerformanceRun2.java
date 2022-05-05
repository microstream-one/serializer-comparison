package be.rubus.microstream.serializer.gson;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.SomeData;
import com.google.gson.Gson;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class GsonPerformanceRun2
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(GsonPerformanceRun2.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<SomeData> testData;

    private Gson reusedGson;

    private List<byte[]> serializedContent;

    @Setup
    public void init()
    {
        this.testData = GenerateData.testData(100_000);

        this.reusedGson = new Gson();
        this.prepareDeserializedData();
    }

    private void prepareDeserializedData()
    {
        final Gson gson = new Gson();

        this.serializedContent = new ArrayList<>();

        for (final SomeData input : this.testData)
        {

            this.serializedContent.add(gson.toJson(input)
                                               .getBytes(StandardCharsets.UTF_8));

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

        final Gson gson = new Gson();

        for (final SomeData input : this.testData)
        {
            blackhole.consume(gson.toJson(input)
                                      .getBytes(StandardCharsets.UTF_8));
        }

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void serializeReuseMapper(final Blackhole blackhole)
    {

        for (final SomeData input : this.testData)
        {
            blackhole.consume(this.reusedGson.toJson(input)
                                      .getBytes(StandardCharsets.UTF_8));
        }

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserializeWithInitialization(final Blackhole blackhole)
    {

        final Gson gson = new Gson();

        for (final byte[] input : this.serializedContent)
        {
            blackhole.consume(gson.fromJson(new String(input), SomeData.class));
        }


    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void deserializeReuseMapper(final Blackhole blackhole)
    {

        for (final byte[] input : this.serializedContent)
        {
            blackhole.consume(this.reusedGson.fromJson(new String(input), SomeData.class));
        }

    }
}
