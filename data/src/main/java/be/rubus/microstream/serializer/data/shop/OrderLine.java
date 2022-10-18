package be.rubus.microstream.serializer.data.shop;

import java.io.Serializable;

public class OrderLine implements Serializable
{

    private ShopProduct shopProduct;
    private int amount;

    public OrderLine()
    {
    }

    public OrderLine(final ShopProduct shopProduct, final int amount)
    {
        this.shopProduct = shopProduct;
        this.amount = amount;
    }

    public ShopProduct getProduct()
    {
        return this.shopProduct;
    }

    public int getAmount()
    {
        return this.amount;
    }

    // Setters required for JAXB
    public void setShopProduct(final ShopProduct shopProduct)
    {
        this.shopProduct = shopProduct;
    }

    public void setAmount(final int amount)
    {
        this.amount = amount;
    }
}
