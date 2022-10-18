package be.rubus.microstream.serializer.yaml;

import be.rubus.microstream.serializer.data.SomeData;
import be.rubus.microstream.serializer.yaml.custom.MyCustomConstructor;
import be.rubus.microstream.serializer.yaml.custom.MyCustomRepresenter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class TestScenario2
{

    @Test
    void test() throws IOException
    {
        // Data
        SomeData someData = new SomeData(42);

        // setup serializer
        final Yaml yamlOut = new Yaml(new MyCustomRepresenter());

        // Serialise
        final byte[] serializedContent = yamlOut.dumpAs(someData, Tag.BINARY, null)
                .getBytes(Charset.defaultCharset());


        // Deserialise
        final SomeData data;
        final Yaml yamlIn = new Yaml(new MyCustomConstructor());

        final ByteArrayInputStream input = new ByteArrayInputStream(serializedContent);

        data = yamlIn.loadAs(input, SomeData.class);
        input.close();


        // test for equality
        Assertions.assertThat(data)
                .isEqualTo(someData);
    }
}
