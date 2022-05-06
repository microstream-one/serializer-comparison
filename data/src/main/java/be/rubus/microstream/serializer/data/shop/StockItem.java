package be.rubus.microstream.serializer.data.shop;

import java.io.Serializable;

public class StockItem implements Serializable
{

    private ShopProduct shopProduct;
    private int count;

    public StockItem()
    {
    }

    public StockItem(final ShopProduct shopProduct, final int count)
    {
        this.shopProduct = shopProduct;
        this.count = count;
    }

    public ShopProduct getShopProduct()
    {
        return this.shopProduct;
    }

    public int getCount()
    {
        return this.count;
    }

    // Setters required for JAXB

    public void setShopProduct(final ShopProduct shopProduct)
    {
        this.shopProduct = shopProduct;
    }

    public void setCount(final int count)
    {
        this.count = count;
    }
}
