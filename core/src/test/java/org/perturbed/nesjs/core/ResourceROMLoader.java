package org.perturbed.nesjs.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.perturbed.nesjs.core.client.ROM;

public class ResourceROMLoader {

  public static ROM loadROMResource(Class<?> clazz, String resourceName) throws IOException {
    InputStream in = clazz.getResourceAsStream(resourceName);
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    int next;
    while ((next = in.read()) != -1) {
      out.write(next);
    }

    in.close();

    byte[] buffer = out.toByteArray();
    int[] bytes = new int[buffer.length];

    for (int i = 0; i < buffer.length; i++) {
      bytes[i] = buffer[i] & 0xFF;
    }

    return new ROM(bytes);
  }
}
