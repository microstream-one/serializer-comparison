package be.rubus.microstream.serializer.hibernate.model;

import be.rubus.microstream.serializer.data.Product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "product")
public class ProductEntity implements Serializable
{
    @Id
    private long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private int rating;

    /*
    Required for the Hibernate handling
     */
    public ProductEntity()
    {
    }

    public ProductEntity(final Product product)
    {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.rating = product.getRating();
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
        final ProductEntity productEntity = (ProductEntity) o;
        return this.id == productEntity.id;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.id);
    }

    @Override
    public String toString()
    {
        return new StringJoiner(", ", ProductEntity.class.getSimpleName() + "[", "]")
                .add("id=" + this.id)
                .add("name='" + this.name + "'")
                .add("description='" + this.description + "'")
                .add("rating=" + this.rating)
                .toString();
    }
}
