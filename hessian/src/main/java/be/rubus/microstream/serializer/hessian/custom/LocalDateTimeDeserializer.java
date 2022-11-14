package be.rubus.microstream.serializer.hessian.custom;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeDeserializer
        extends AbstractDeserializer
{
    @Override
    public Class<?> getType()
    {
        return LocalDateTime.class;
    }

    @Override
    public boolean isReadResolve()
    {
        return false;
    }

    @Override
    public Object readObject(AbstractHessianInput in) throws IOException
    {
        return null;
    }

    @Override
    public Object readList(AbstractHessianInput in, int length) throws IOException
    {
        return null;
    }

    @Override
    public Object readLengthList(AbstractHessianInput in, int length) throws IOException
    {
        return null;
    }

    @Override
    public Object readMap(AbstractHessianInput in) throws IOException
    {
        throw new RuntimeException("Hessian 1 format (Map) is not supported");

    }

    @Override
    public Object[] createFields(int len)
    {
        return new String[len];
    }

    @Override
    public Object createField(String name)
    {
        return name;
    }

    public Object readObject(
            AbstractHessianInput in,
            Object[] fields
    )
            throws IOException
    {
        String[] fieldNames = (String[]) fields;

        int ref = in.addRef(null);

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

        Object value =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(initValue), ZoneId.of("UTC"));

        in.setRef(ref, value);

        return value;
    }

}
