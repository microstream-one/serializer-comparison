package be.rubus.microstream.serializer.yaml;

import be.rubus.microstream.serializer.data.GenerateData;
import be.rubus.microstream.serializer.data.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class TestScenario1
{

    @Test
    void test() throws IOException
    {
        // Data
        final List<Product> allProducts = GenerateData.products(5);

        // setup serializer
        final Yaml yaml = new Yaml();

        // Serialise
        final byte[] serializedContent = yaml.dumpAs(allProducts, Tag.BINARY, null)
                .getBytes(Charset.defaultCharset());

        // Deserialise
        final List<Product> data;

        final ByteArrayInputStream input = new ByteArrayInputStream(serializedContent);

        data = yaml.loadAs(input, List.class);
        input.close();

        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(allProducts);
    }

}
