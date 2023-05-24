package be.rubus.microstream.serializer.yaml.custom;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.representer.Representer;

import java.time.LocalDateTime;

public class MyCustomRepresenter extends Representer
{

    public MyCustomRepresenter()
    {
        super(new DumperOptions());
        representers.put(LocalDateTime.class, new LocalDateTimePresenter());
    }
}
