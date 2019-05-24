package com.softalanta.batchlay.utils;

import com.google.common.io.ByteSource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class MultipartFileByteSource extends ByteSource
{
    private MultipartFile file;

    public MultipartFileByteSource( MultipartFile file )
    {
        this.file = file;
    }

    @Override
    public InputStream openStream() throws IOException
    {
        try
        {
            return file.getInputStream();
        }
        catch ( IOException ioe )
        {
            return null;
        }
    }
}
