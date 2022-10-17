package be.rubus.microstream.serializer.hessian.custom;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LocalDateTimeSerializer
        implements Serializer
{

    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException
    {
        if (obj == null)
        {
            out.writeNull();
        }
        else
        {
            Class<?> cl = obj.getClass();

            if (out.addRef(obj))
            {
                return;
            }

            int ref = out.writeObjectBegin(cl.getName());

            if (ref < -1)
            {
                out.writeString("value");
                out.writeUTCDate(((LocalDateTime) obj).toInstant(ZoneOffset.UTC)
                                         .toEpochMilli());
                out.writeMapEnd();
            }
            else
            {
                if (ref == -1)
                {
                    out.writeInt(1);
                    out.writeString("value");
                    out.writeObjectBegin(cl.getName());
                }

                out.writeUTCDate(((LocalDateTime) obj).toInstant(ZoneOffset.UTC)
                                         .toEpochMilli());
            }
        }
    }
}