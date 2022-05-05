package be.rubus.microstream.serializer.jaxb;

import be.rubus.microstream.serializer.data.Product;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

/*
We need a root class with @XmlRootElement
 */
@XmlRootElement
public class XmlModel
{

    private List<Product> products;

    public List<Product> getProducts()
    {
        return this.products;
    }

    public void setProducts(final List<Product> products)
    {
        this.products = products;
    }
}
