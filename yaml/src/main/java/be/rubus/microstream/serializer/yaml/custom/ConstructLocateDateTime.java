package be.rubus.microstream.serializer.yaml.custom;

import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ConstructLocateDateTime extends AbstractConstruct
{
    @Override
    public Object construct(Node node)
    {

        final String value = ((ScalarNode) node).getValue();
        final long initValue;
        try
        {
            initValue = Long.parseLong(value);
        } catch (NumberFormatException e)
        {
            // What exception?
            throw new RuntimeException(e);
        }

        return
                LocalDateTime.ofInstant(Instant.ofEpochMilli(initValue), ZoneId.of("UTC"));

    }


}
