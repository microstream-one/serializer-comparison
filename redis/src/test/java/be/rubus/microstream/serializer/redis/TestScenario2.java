package be.rubus.microstream.serializer.redis;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.SomeData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.providers.PooledConnectionProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TestScenario2
{

    @Test
    void listStoredIndividually()
    {
        // Data
        final List<SomeData> allData = GenerateData.testData(5);

        // setup serializer

        final HostAndPort config = new HostAndPort(Protocol.DEFAULT_HOST, 6379);
        PooledConnectionProvider provider = new PooledConnectionProvider(config);
        UnifiedJedis client = new UnifiedJedis(provider);

        // Serialize
        allData.forEach(
                data -> client.jsonSetWithEscape("data:" + data.getValue(), data)
        );


        // Deserialise
        // Retrieve the List
        Set<String> keys = client.keys("data:*");
        final List<SomeData> data = new ArrayList<>();

        keys.forEach(
                key -> data.add(client.jsonGet(key, SomeData.class))
        );

        // test for equality  , no specific order
        Assertions.assertThat(allData)
                .containsExactlyInAnyOrderElementsOf(data);


    }
}
