/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.resource;

import com.google.common.base.MoreObjects;
import com.google.common.io.Resources;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;

/**
 *
 * @author shevek
 */
public class TftpResourceDataProvider extends AbstractTftpDataProvider {

    public static final String PREFIX = "tftproot";
    private final String prefix;

    public TftpResourceDataProvider(@Nonnull String prefix) {
        this.prefix = prefix;
    }

    public TftpResourceDataProvider() {
        this(PREFIX);
    }

    @Nonnull
    public String getPrefix() {
        return prefix;
    }

    @Nonnull
    public ClassLoader getClassLoader() {
        return MoreObjects.firstNonNull(
                Thread.currentThread().getContextClassLoader(),
                getClass().getClassLoader());
    }

    @Override
    public TftpData open(String filename) throws IOException {
        String path = toPath(getPrefix(), filename);
        if (path == null)
            return null;
        ClassLoader loader = getClassLoader();
        URL resource = loader.getResource(path);
        if (resource == null)
            return null;
        byte[] data = Resources.toByteArray(resource);
        return new TftpByteArrayData(data);
    }
}