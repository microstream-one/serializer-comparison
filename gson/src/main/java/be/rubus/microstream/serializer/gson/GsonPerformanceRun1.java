package be.rubus.microstream.serializer.gson;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.openjdk.jmh.annotations.*;
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
public class GsonPerformanceRun1
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(GsonPerformanceRun1.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<Product> allProducts;

    private Gson reusedGson;

    private byte[] serializedContent;

    @Setup
    public void init()
    {
        this.allProducts = GenerateData.products(10_000);

        this.reusedGson = new Gson();
        this.prepareDeserializedData();
    }

    private void prepareDeserializedData()
    {
        final Gson gson = new Gson();
        this.serializedContent = gson.toJson(this.allProducts)
                .getBytes(StandardCharsets.UTF_8);
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

        final Gson gson = new Gson();

        return gson.toJson(this.allProducts)
                .getBytes(StandardCharsets.UTF_8);

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public byte[] serializeReuseMapper()
    {

        return this.reusedGson.toJson(this.allProducts)
                .getBytes(StandardCharsets.UTF_8);

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<Product> deserializeWithInitialization()
    {

        final Gson gson = new Gson();
        return gson.fromJson(new String(this.serializedContent), new TypeToken<ArrayList<Product>>()
        {
        }.getType());


    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<Product> deserializeReuseMapper()
    {

        return this.reusedGson.fromJson(new String(this.serializedContent), new TypeToken<ArrayList<Product>>()
        {
        }.getType());

    }
}
