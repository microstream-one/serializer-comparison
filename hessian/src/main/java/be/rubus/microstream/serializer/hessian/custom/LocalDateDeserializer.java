package be.rubus.microstream.serializer.hessian.custom;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateDeserializer
        extends AbstractDeserializer
{
    @Override
    public Class<?> getType()
    {
        return LocalDate.class;
    }

    @Override
    public boolean isReadResolve()
    {
        return false;
    }

    @Override
    public Object readObject(final AbstractHessianInput in) throws IOException
    {
        return null;
    }

    @Override
    public Object readList(final AbstractHessianInput in, final int length) throws IOException
    {
        return null;
    }

    @Override
    public Object readLengthList(final AbstractHessianInput in, final int length) throws IOException
    {
        return null;
    }

    @Override
    public Object readMap(final AbstractHessianInput in) throws IOException
    {
        throw new RuntimeException("Hessian 1 format (Map) is not supported");

    }

    @Override
    public Object[] createFields(final int len)
    {
        return new String[len];
    }

    @Override
    public Object createField(final String name)
    {
        return name;
    }

    public Object readObject(
            final AbstractHessianInput in,
            final Object[] fields
    )
            throws IOException
    {
        final String[] fieldNames = (String[]) fields;

        final int ref = in.addRef(null);

        long initValue = Long.MIN_VALUE;

        for (String key : fieldNames)
        {
            if (key.equals("value"))
            {
                initValue = in.readLong();
            }
            else
            {
                // Ignore field. Should not happen when Serializer is used.
                in.readObject();
            }
        }

        final Object value =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(initValue), ZoneId.of("UTC"))
                        .toLocalDate();

        in.setRef(ref, value);

        return value;
    }

}
