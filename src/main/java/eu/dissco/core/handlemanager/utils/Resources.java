package eu.dissco.core.handlemanager.utils;

public class Resources {

  private Resources() {
    throw new IllegalStateException("Utility class");
  }

  public static byte[] genAdminHandle() {
    return decodeAdmin();
  }

  private static byte[] decodeAdmin() {
    String admin = "0fff000000153330303a302e4e412f32302e353030302e31303235000000c8";
    byte[] adminByte = new byte[admin.length() / 2];
    for (int i = 0; i < admin.length(); i += 2) {
      adminByte[i / 2] = hexToByte(admin.substring(i, i + 2));
    }
    return adminByte;
  }

  private static byte hexToByte(String hexString) {
    int firstDigit = toDigit(hexString.charAt(0));
    int secondDigit = toDigit(hexString.charAt(1));
    return (byte) ((firstDigit << 4) + secondDigit);
  }

  private static int toDigit(char hexChar) {
    return Character.digit(hexChar, 16);
  }

}
