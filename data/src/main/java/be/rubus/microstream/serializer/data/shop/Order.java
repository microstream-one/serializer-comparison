package be.rubus.microstream.serializer.data.shop;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable
{

    private String customerName;

    private LocalDate orderDate;

    private List<OrderLine> orderLines;

    public Order()
    {
    }

    public Order(final String customerName, final LocalDate orderDate, final List<OrderLine> orderLines)
    {
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.orderLines = orderLines;
    }

    public String getCustomerName()
    {
        return this.customerName;
    }

    public LocalDate getOrderDate()
    {
        return this.orderDate;
    }

    public List<OrderLine> getOrderLines()
    {
        return new ArrayList<>(this.orderLines);
    }

    // Setters required for JAXB
    public void setCustomerName(final String customerName)
    {
        this.customerName = customerName;
    }

    public void setOrderDate(final LocalDate orderDate)
    {
        this.orderDate = orderDate;
    }

    public void setOrderLines(final List<OrderLine> orderLines)
    {
        this.orderLines = orderLines;
    }
}
