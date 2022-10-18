package be.rubus.microstream.serializer.data.shop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Shop implements Serializable
{

    private String name;
    private Warehouse store;
    private List<Order> orders;

    public Shop()
    {
    }

    public Shop(final String name, final Warehouse store)
    {
        this.name = name;
        this.store = store;
        this.orders = new ArrayList<>();
    }

    public String getName()
    {
        return this.name;
    }

    public Warehouse getStore()
    {
        return this.store;
    }

    public List<Order> getOrders()
    {
        return new ArrayList<>(this.orders);
    }

    // Setters required for JAXB
    public void setName(final String name)
    {
        this.name = name;
    }

    public void setStore(final Warehouse store)
    {
        this.store = store;
    }

    public void setOrders(final List<Order> orders)
    {
        this.orders = orders;
    }

    public void addOrder(final Order order)
    {
        this.orders.add(order);
    }


    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }

        final Shop shop = (Shop) o;

        return this.name.equals(shop.name);
    }

    @Override
    public int hashCode()
    {
        return this.name.hashCode();
    }
}
