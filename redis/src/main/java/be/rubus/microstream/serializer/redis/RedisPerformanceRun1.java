package be.rubus.microstream.serializer.redis;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.providers.PooledConnectionProvider;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class RedisPerformanceRun1
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(RedisPerformanceRun1.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<Product> data;

    private UnifiedJedis reusedClient;

    @Setup
    public void init() throws IOException
    {
        this.data = GenerateData.products(10_000);

        reusedClient = createClient();

        // Delete everything.
        reusedClient.del("product");

        // Make sure redis has data for the deserialization
        reusedClient.jsonSetWithEscape("product:all", new WrappedData(this.data));
    }

    private static UnifiedJedis createClient()
    {
        final HostAndPort config = new HostAndPort(Protocol.DEFAULT_HOST, 6379);
        PooledConnectionProvider provider = new PooledConnectionProvider(config);
        return new UnifiedJedis(provider);
    }

    @TearDown
    public void shutdown() throws Exception
    {
        // Delete everything.
        reusedClient.del("product");

        reusedClient.close();
    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void serializeWithInitialization()
    {
        try (UnifiedJedis client = createClient())
        {

            // Serialize
            client.jsonSetWithEscape("product:serialize", new WrappedData(this.data));
        }

        // We can't return the serialized content.

    }


    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<Product> deserializeWithInitialization()
    {
        final List<Product> data;
        try (UnifiedJedis client = createClient())
        {
            WrappedData wrappedData = client.jsonGet("product:all", WrappedData.class);
            data = wrappedData.getData();
        }
        return data;

    }

    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public void serializeUsingReusedClient()
    {
        // Serialize
        reusedClient.jsonSetWithEscape("product:serialize", new WrappedData(this.data));


        // We can't return the serialized content.

    }


    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<Product> deserializeUsingReusedClient()
    {
        final List<Product> data;

        WrappedData wrappedData = reusedClient.jsonGet("product:all", WrappedData.class);
        data = wrappedData.getData();

        return data;

    }
}
