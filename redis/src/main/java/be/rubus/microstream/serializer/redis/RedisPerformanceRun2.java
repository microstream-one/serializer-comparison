package be.rubus.microstream.serializer.redis;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import be.rubus.microstream.serializer.data.SomeData;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.providers.PooledConnectionProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
// Remark: This is really slow as we access Redis 100 000 times for each run.
public class RedisPerformanceRun2
{

    public static void main(final String[] args) throws Exception
    {
        final Options opt = new OptionsBuilder()
                .include(RedisPerformanceRun2.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private List<SomeData> testData;

    private UnifiedJedis reusedClient;

    @Setup
    public void init() throws IOException
    {
        this.testData = GenerateData.testData(100_000);

        reusedClient = createClient();

        // Delete everything.
        reusedClient.del("data");
        // Make sure redis has data for the deserialization
        this.testData.forEach(
                data -> reusedClient.jsonSetWithEscape("data:" + data.getValue(), data)
        );

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
        reusedClient.del("data");
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
            this.testData.forEach(
                    data -> client.jsonSetWithEscape("data:" + data.getValue(), data)
            );
        }

        // We can't return the serialized content.

    }


    @Benchmark
    @Warmup(iterations = 1, time = 5)  // Run 5 seconds
    @Measurement(iterations = 5, time = 5)// Run 5 times 5 seconds
    public List<SomeData> deserializeWithInitialization()
    {
        final List<SomeData> data = new ArrayList<>();
        try (UnifiedJedis client = createClient())
        {
            Set<String> keys = client.keys("data:*");

            keys.forEach(
                    key -> data.add(client.jsonGet(key, SomeData.class))
            );
        }
        return data;

    }
}
