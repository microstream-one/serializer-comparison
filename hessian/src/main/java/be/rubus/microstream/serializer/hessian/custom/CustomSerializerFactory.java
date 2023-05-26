package be.rubus.microstream.serializer.hessian.custom;

import com.caucho.hessian.io.AbstractSerializerFactory;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.Serializer;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomSerializerFactory extends AbstractSerializerFactory
{
    @Override
    public Serializer getSerializer(Class cl) throws HessianProtocolException
    {
        if (cl.equals(LocalDateTime.class))
        {
            return new LocalDateTimeSerializer();
        }
        if (cl.equals(LocalDate.class))
        {
            return new LocalDateSerializer();
        }
        return null;

    }

    @Override
    public Deserializer getDeserializer(Class cl) throws HessianProtocolException
    {
        if (cl.equals(LocalDateTime.class))
        {
            return new LocalDateTimeDeserializer();
        }
        if (cl.equals(LocalDate.class))
        {
            return new LocalDateDeserializer();
        }
        return null;

    }

}
