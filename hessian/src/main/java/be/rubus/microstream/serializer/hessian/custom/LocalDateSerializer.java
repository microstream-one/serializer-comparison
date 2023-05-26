package be.rubus.microstream.serializer.hessian.custom;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LocalDateSerializer
        implements Serializer
{

    @Override
    public void writeObject(final Object obj, final AbstractHessianOutput out) throws IOException
    {
        if (obj == null)
        {
            out.writeNull();
        }
        else
        {
            final Class<?> cl = obj.getClass();

            if (out.addRef(obj))
            {
                return;
            }

            final int ref = out.writeObjectBegin(cl.getName());

            if (ref < -1)
            {
                out.writeString("value");
                out.writeLong(((LocalDate) obj).atTime(0,0).toInstant(ZoneOffset.UTC)
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

                out.writeLong(((LocalDateTime) obj).toInstant(ZoneOffset.UTC)
                                         .toEpochMilli());
            }
        }
    }
}