package eu.dissco.core.handlemanager.domain.repsitoryobjects;

import java.nio.charset.StandardCharsets;

public class HandleFullRow {

  private final String handle;
  private final String idx;
  private final String type;
  private final String data;
  private static final String TTL_TYPE = "0";
  private static final String TTL = "86400";
  private final String recordTimestamp;
  private static final String ADMIN_READ = String.valueOf(true);
  private static final String ADMIN_WRITE = String.valueOf(true);
  private static final String PUB_READ = String.valueOf(true);
  private static final String PUB_WRITE = String.valueOf(false);

  public HandleFullRow(HandleAttribute attribute, long recordTimestamp) {
    this.handle = new String(attribute.getHandle(), StandardCharsets.UTF_8);
    this.idx = String.valueOf(attribute.getIndex());
    this.type = attribute.getType();
    this.data = new String(attribute.getData(), StandardCharsets.UTF_8).replace(",", "\\,");
    this.recordTimestamp = String.valueOf(recordTimestamp);
  }

  public byte[] getCsvRow() {
    return (handle + ","
        + idx + ","
        + type + ","
        + data + ","
        + TTL_TYPE + ","
        + TTL + ","
        + recordTimestamp + ","
        + "\\N," // Leave refs null
        + ADMIN_READ + ","
        + ADMIN_WRITE + ","
        + PUB_READ + ","
        + PUB_WRITE + "\n").getBytes(StandardCharsets.UTF_8);
  }
}
