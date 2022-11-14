package be.rubus.microstream.serializer.data;

import io.github.threetenjaxb.core.LocalDateTimeXmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

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
        this.now = this.now.minusNanos(this.now.getNano());  // Only keep milliseconds, not the nanosecond field.
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof SomeData))
        {
            return false;
        }

        SomeData someData = (SomeData) o;

        if (value != someData.value)
        {
            return false;
        }
        if (x != someData.x)
        {
            return false;
        }
        if (!Objects.equals(name, someData.name))
        {
            return false;
        }
        if (!Objects.equals(now, someData.now))
        {
            return false;
        }
        if (!Objects.equals(big, someData.big))
        {
            return false;
        }
        return Arrays.equals(intArray, someData.intArray);
    }

    @Override
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (int) (value ^ (value >>> 32));
        result = 31 * result + (now != null ? now.hashCode() : 0);
        result = 31 * result + x;
        result = 31 * result + (big != null ? big.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(intArray);
        return result;
    }

    @Override
    public String toString()
    {
        return new StringJoiner(", ", SomeData.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("value=" + value)
                .add("now=" + now)
                .add("x=" + x)
                .add("big=" + big)
                .add("intArray=" + Arrays.toString(intArray))
                .toString();
    }
}
