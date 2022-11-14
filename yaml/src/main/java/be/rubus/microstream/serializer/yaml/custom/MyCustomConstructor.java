package be.rubus.microstream.serializer.yaml.custom;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import java.time.LocalDateTime;

public class MyCustomConstructor extends Constructor
{

    public MyCustomConstructor()
    {
        yamlConstructors.put(new Tag(LocalDateTime.class
                                             .getName()), new ConstructLocateDateTime());
    }
}
