package be.rubus.microstream.serializer.yaml.custom;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LocalDateTimePresenter implements Represent
{
    @Override
    public Node representData(Object obj)
    {
        final String value = String.valueOf(((LocalDateTime) obj).toInstant(ZoneOffset.UTC)
                                                    .toEpochMilli());
        return new ScalarNode(new Tag(LocalDateTime.class
                                              .getName()), value, null, null, DumperOptions.ScalarStyle.PLAIN);
    }
}
