package org.seasar.javelin.bottleneckeye.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.seasar.javelin.bottleneckeye.model.persistence.PersistenceModel;

/**
 * ���f���̒��񉻂��s���N���X�B
 *
 */
public class ModelSerializer
{
    private static JAXBContext  context;

    private static Marshaller   marshaller;

    private static Unmarshaller unmarshaller;

    static
    {
        try
        {
            context = JAXBContext.newInstance(PersistenceModel.class);
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            unmarshaller = context.createUnmarshaller();
        }
        catch (JAXBException ex)
        {
            // ignore
            ex.printStackTrace();
        }
    }

    /**
     * ���f����XML������ɒ��񉻂���B
     * @param persistence ���f���̃��[�g
     * @return XML�������byte�z��
     * @throws IOException 
     */
    public synchronized static byte[] serialize(PersistenceModel persistence)
        throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try
        {
            marshaller.marshal(persistence, out);
        }
        catch (JAXBException ex)
        {
            IOException ioe = new IOException();
            ioe.initCause(ex);
            throw ioe;
        }

        return out.toByteArray();
    }

    /**
     * XML������̃X�g���[�������f���ɕϊ�����
     * @param in �X�g���[��
     * @return ���f��
     * @throws IOException 
     */
    public synchronized static PersistenceModel deserialize(InputStream in)
        throws IOException
    {
        PersistenceModel persistence;
        try
        {
            persistence = (PersistenceModel)unmarshaller.unmarshal(in);
        }
        catch (JAXBException ex)
        {
            IOException ioe = new IOException();
            ioe.initCause(ex);
            throw ioe;
        }

        return persistence;
    }
}
