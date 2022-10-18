package be.rubus.microstream.serializer.data.shop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShopProduct implements Serializable
{

    private String name;
    private double price;
    private double sellingPrice;
    private List<Shop> shops;  // The shops where the product can be bought.

    public ShopProduct()
    {
    }

    public ShopProduct(final String name, final double price, final double sellingPrice)
    {
        this.name = name;
        this.price = price;
        this.sellingPrice = sellingPrice;
        this.shops = new ArrayList<>();
    }

    public String getName()
    {
        return this.name;
    }

    public double getPrice()
    {
        return this.price;
    }

    public double getSellingPrice()
    {
        return this.sellingPrice;
    }

    public List<Shop> getShops()
    {
        return new ArrayList<>(this.shops);
    }

    public void addToShop(final Shop shop)
    {
        this.shops.add(shop);
    }

    // Setters required for JAXB

    public void setName(final String name)
    {
        this.name = name;
    }

    public void setPrice(final double price)
    {
        this.price = price;
    }

    public void setSellingPrice(final double sellingPrice)
    {
        this.sellingPrice = sellingPrice;
    }

    public void setShops(final List<Shop> shops)
    {
        this.shops = shops;
    }
}
