package eu.dissco.core.handlemanager.service;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceUtils {

  private ServiceUtils() {
  }

  public static <T> Collector<T, ?, T> toSingle() {
    return Collectors.collectingAndThen(
        Collectors.toList(),
        list -> {
          if (list.size() != 1) {
            throw new IllegalStateException();
          }
          return list.get(0);
        }
    );
  }

}
