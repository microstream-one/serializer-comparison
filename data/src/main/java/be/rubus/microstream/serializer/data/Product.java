package be.rubus.microstream.serializer.data;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

public class Product implements Serializable
{
    private long id;
    private String name;
    private String description;
    private int rating;

    /*
    Required for the JSON handling
     */
    public Product()
    {
    }

    public Product(
            final long id,
            final String name,
            final String description,
            final int rating
    )
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rating = rating;
    }

    public long getId()
    {
        return this.id;
    }

    public void setId(final long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public int getRating()
    {
        return this.rating;
    }

    public void setRating(final int rating)
    {
        this.rating = rating;
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
        final Product product = (Product) o;
        return this.id == product.id;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.id);
    }

    @Override
    public String toString()
    {
        return new StringJoiner(", ", Product.class.getSimpleName() + "[", "]")
                .add("id=" + this.id)
                .add("name='" + this.name + "'")
                .add("description='" + this.description + "'")
                .add("rating=" + this.rating)
                .toString();
    }
}
