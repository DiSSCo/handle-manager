package eu.dissco.core.handlemanager.domain.repsitoryobjects;

import java.nio.charset.StandardCharsets;

public class HandleFullRow {

  private final byte[] handle;
  private final int idx;
  private final byte[] type;
  private final byte[] data;
  private static final int TTL = 86400;
  private final long recordTimestamp;
  private static final boolean ADMIN_READ = false;
  private static final boolean ADMIN_WRITE = false;
  private static final boolean PUB_READ = true;
  private static final boolean PUB_WRITE = false;

  public HandleFullRow(HandleAttribute attribute, long recordTimestamp) {
    this.handle = attribute.getHandle();
    this.idx = attribute.getIndex();
    this.type = attribute.getType().getBytes(StandardCharsets.UTF_8);
    this.data = attribute.getData();
    this.recordTimestamp = recordTimestamp;
  }

  public byte[] getCsvRow() {
    return (new String(handle, StandardCharsets.UTF_8) + "," +
        idx + "," +
        new String(type, StandardCharsets.UTF_8) + "," +
        new String(data, StandardCharsets.UTF_8) + "," +
        TTL + "," +
        recordTimestamp + "," +
        ADMIN_READ + "," +
        ADMIN_WRITE + "," +
        PUB_READ + "," +
        PUB_WRITE + ",").getBytes(StandardCharsets.UTF_8);
  }

}
