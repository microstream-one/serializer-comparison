package be.rubus.microstream.serializer.redis;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.providers.PooledConnectionProvider;

import java.util.List;

public class TestScenario1
{

    @Test
    void listStoredWrapped()
    {
        // Data
        final List<Product> allProducts = GenerateData.products(5);


        // setup serializer

        final HostAndPort config = new HostAndPort(Protocol.DEFAULT_HOST, 6379);
        PooledConnectionProvider provider = new PooledConnectionProvider(config);
        UnifiedJedis client = new UnifiedJedis(provider);

        // Serialize
        client.jsonSetWithEscape("product:all", new WrappedData(allProducts));


        // Deserialise
        WrappedData wd = client.jsonGet("product:all", WrappedData.class);
        // test for equality  , no specific order
        Assertions.assertThat(allProducts)
                .containsExactlyInAnyOrderElementsOf(wd.getData());


    }
}
