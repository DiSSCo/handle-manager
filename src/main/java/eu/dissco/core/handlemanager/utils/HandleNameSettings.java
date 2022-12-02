package eu.dissco.core.handlemanager.utils;

public class HandleNameSettings {
  public static final int LENGTH = 11;
  public static final String ALPHA_NUM = "ABCDEFGHJKLMNPQRSTUVWXYZ1234567890";
  public static final String PREFIX = "20.5000.1025/";
  public static final int MAX_HANDLES = 1000;

  private HandleNameSettings() {
    throw new IllegalStateException("Utility class");
  }


}
