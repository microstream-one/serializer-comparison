package be.rubus.microstream.serializer.jaxb;

import be.rubus.microstream.serializer.data.shop.Shop;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

/*
We need a root class with @XmlRootElement
 */
@XmlRootElement
public class XmlModel3
{

    private List<Shop> shops;

    public List<Shop> getShops()
    {
        return this.shops;
    }

    public void setShops(final List<Shop> shops)
    {
        this.shops = shops;
    }
}
