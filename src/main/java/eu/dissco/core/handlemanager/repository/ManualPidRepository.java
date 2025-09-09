package eu.dissco.core.handlemanager.repository;

import static eu.dissco.core.handlemanager.jooqobjects.Tables.MANUAL_PID;

import eu.dissco.core.handlemanager.properties.ApplicationProperties;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ManualPidRepository {

  private final ApplicationProperties applicationProperties;
  private final DSLContext context;

  public Set<String> getPids(int h){
    return new HashSet<>(context.select(MANUAL_PID.PID)
        .from(MANUAL_PID)
        .where(MANUAL_PID.PREFIX.eq(applicationProperties.getPrefix()))
        .limit(h)
        .fetchInto(String.class));
  }

  public void deleteTakenPids(List<String> pids){
    context.deleteFrom(MANUAL_PID)
        .where(MANUAL_PID.PREFIX.in(pids))
        .execute();
  }


}
