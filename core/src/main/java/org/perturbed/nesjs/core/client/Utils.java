package org.perturbed.nesjs.core.client;


/**
 * Only used for debugging
 *
 * @author Matt
 */
public class Utils {

  public static final String toHexString(int value) {
    String _hex = Integer.toHexString(value).toUpperCase();

    if (_hex.length() % 2 == 0) {
      return "0x" + _hex;
    } else {
      return "0x0" + _hex;
    }
  }

  public static final String toBinaryString(int byteValue) {
    assert byteValue < 0x0100;

    String result = Integer.toBinaryString(byteValue);

    while (result.length() < 8) {
      result = "0" + result;
    }

    return result;
  }
}
