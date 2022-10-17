package be.rubus.microstream.serializer.hessian.custom;

import com.caucho.hessian.io.AbstractSerializerFactory;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.Serializer;

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
        return null;

    }

    @Override
    public Deserializer getDeserializer(Class cl) throws HessianProtocolException
    {
        if (cl.equals(LocalDateTime.class))
        {
            return new LocalDateTimeDeserializer();
        }
        return null;

    }

}
