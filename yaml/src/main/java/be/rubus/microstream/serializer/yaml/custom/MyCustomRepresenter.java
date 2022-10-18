package be.rubus.microstream.serializer.yaml.custom;

import org.yaml.snakeyaml.representer.Representer;

import java.time.LocalDateTime;

public class MyCustomRepresenter extends Representer
{

    public MyCustomRepresenter()
    {
        representers.put(LocalDateTime.class, new LocalDateTimePresenter());
    }
}
