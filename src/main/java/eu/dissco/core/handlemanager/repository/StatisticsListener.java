package eu.dissco.core.handlemanager.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteType;
import org.jooq.impl.DefaultExecuteListener;

public class StatisticsListener extends DefaultExecuteListener {

  public static final Map<ExecuteType, Integer> STATISTICS = new ConcurrentHashMap<>();

  @Override
  public void start(ExecuteContext ctx) {
    STATISTICS.compute(ctx.type(), (k, v) -> v == null ? 1 : v + 1);
  }

}
