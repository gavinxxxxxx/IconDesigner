package me.gavin.util;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Base64Helper
 *
 * @author gavin.xiong 2017/12/6
 */
public class Base64Helper {

    public static String toBase64(Object obj) {
        return Base64.encodeToString(toBytes(obj), Base64.DEFAULT);
    }

    public static Object fromBase64(String string) {
        return fromBytes(Base64.decode(string, Base64.DEFAULT));
    }

    private static byte[] toBytes(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            return gzip(bos.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }

    private static Object fromBytes(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(unGzip(bytes));
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            return null;
        }
    }

    private static byte[] gzip(byte[] bytes) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPOutputStream zos = new GZIPOutputStream(bos)) {
            zos.write(bytes);
            zos.finish();
            return bos.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    private static byte[] unGzip(byte[] bytes) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             GZIPInputStream zis = new GZIPInputStream(bis)) {
            byte[] buffer = new byte[4096];
            int temp;
            while ((temp = zis.read(buffer)) > 0)
                bos.write(buffer, 0, temp);
            return bos.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }
}
