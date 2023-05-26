package be.rubus.microstream.serializer.redis;

import be.rubus.microstream.serializer.data.Product;

import java.util.List;

/**
 * Since we cannot store a List as top level data in Redis.
 */
public class WrappedData
{
    private final List<Product> data;

    public WrappedData(List<Product> data)
    {
        this.data = data;
    }

    public List<Product> getData()
    {
        return data;
    }
}
