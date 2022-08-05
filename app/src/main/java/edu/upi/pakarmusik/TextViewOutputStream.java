package edu.upi.pakarmusik;

import java.io.*;
import java.nio.charset.Charset;

import android.widget.TextView;

public class TextViewOutputStream extends OutputStream
{
    private final TextView m_txtArea;
    private final ByteArrayOutputStream buffer;
    private static final int BUFFER_SIZE = 10;
    private final String encoding = Charset.defaultCharset().name();

    public TextViewOutputStream(TextView txtArea)
    {
        m_txtArea = txtArea;
        buffer = new ByteArrayOutputStream();
    }

    public void write(int b) {
        // Modifica per IE4.0
        if(b == 0x0d)
            return;

        //System.out.println("b " + Encoder.intToHexString(b));
        buffer.write(b);
        //strBuffer.app+= Character.to

        if(b == 0x0a || buffer.size() == BUFFER_SIZE)
        {
            flush();
        }
    }

    public void flush()
    {
        try {
            m_txtArea.append(buffer.toString(encoding));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        buffer.reset();
    }

}
