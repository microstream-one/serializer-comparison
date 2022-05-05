package be.rubus.microstream.serializer.data;

import io.github.threetenjaxb.core.LocalDateTimeXmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SomeData implements Serializable
{

    private String name;
    private long value;

    private LocalDateTime now;
    private int x = 19;
    private BigDecimal big;
    private int[] intArray;

    public SomeData(final int i)
    {
        this.name = "Test Object " + i;
        this.value = 1000000000000L + i;
        this.now = LocalDateTime.now();
        this.x = i;
        this.big = BigDecimal.valueOf(1000000000.00 + i);
        this.intArray = new int[]{-i, -i, -i, -i};
    }

    public SomeData()
    {
        // For the JSON handlers
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public long getValue()
    {
        return this.value;
    }

    public void setValue(final long value)
    {
        this.value = value;
    }

    @XmlJavaTypeAdapter(LocalDateTimeXmlAdapter.class)
    public LocalDateTime getNow()
    {
        return this.now;
    }

    public void setNow(final LocalDateTime now)
    {
        this.now = now;
    }

    public int getX()
    {
        return this.x;
    }

    public void setX(final int x)
    {
        this.x = x;
    }

    public BigDecimal getBig()
    {
        return this.big;
    }

    public void setBig(final BigDecimal big)
    {
        this.big = big;
    }

    public int[] getIntArray()
    {
        return this.intArray;
    }

    public void setIntArray(final int[] intArray)
    {
        this.intArray = intArray;
    }

}
