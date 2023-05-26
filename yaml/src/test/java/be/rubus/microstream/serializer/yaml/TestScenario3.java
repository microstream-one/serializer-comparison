package be.rubus.microstream.serializer.yaml;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.shop.Shop;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.inspector.TrustedPrefixesTagInspector;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class TestScenario3
{

    // YAML doesn't support circular graph
    @Test
    void test() throws IOException
    {
        // Data
        final List<Shop> shops = GenerateData.testShopData(false);

        // setup serializer
        final LoaderOptions options = new LoaderOptions();
        options.setTagInspector(new TrustedPrefixesTagInspector(List.of("be.rubus.microstream")));

        final Yaml yaml = new Yaml(options);

        // Serialise
        final byte[] serializedContent = yaml.dumpAs(shops, Tag.BINARY, null)
                .getBytes(Charset.defaultCharset());

        // Deserialise
        final ByteArrayInputStream input = new ByteArrayInputStream(serializedContent);

        final List<Shop> data = yaml.loadAs(input, List.class);
        input.close();

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(shops);
    }

}
