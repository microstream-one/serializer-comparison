package be.rubus.microstream.serializer.data.shop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Warehouse implements Serializable
{

    private List<StockItem> stockItems;

    public Warehouse()
    {
        this.stockItems = new ArrayList<>();
    }

    public List<StockItem> getStockItems()
    {
        return new ArrayList<>(this.stockItems);
    }

    public void addStockItem(final StockItem stockItem)
    {
        this.stockItems.add(stockItem);
    }

    // Setters required for JAXB

    public void setStockItems(final List<StockItem> stockItems)
    {
        this.stockItems = stockItems;
    }
}
