package be.rubus.microstream.serializer.jaxb;

import be.rubus.microstream.serializer.data.SomeData;
import jakarta.xml.bind.annotation.XmlRootElement;

/*
We need a root class with @XmlRootElement
 */
@XmlRootElement
public class XmlModel2
{

    private SomeData someData;

    public SomeData getSomeData()
    {
        return this.someData;
    }

    public void setSomeData(final SomeData someData)
    {
        this.someData = someData;
    }
}
