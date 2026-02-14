package guru.qa.niffler.gprc.util;

import guru.qa.niffler.grpc.Direction;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class GrpcPaginationAndSort {
  private final int page;
  private final int size;
  private final @Nullable List<guru.qa.niffler.grpc.Sort> sort;

  public GrpcPaginationAndSort(int page, int size, @Nullable List<guru.qa.niffler.grpc.Sort> sort) {
    this.page = page;
    this.size = size;
    this.sort = sort;
  }

  public @Nonnull Pageable pageable() {
    return PageRequest.of(
        page,
        size,
        sort()
    );
  }

  private Sort sort() {
    if (sort != null) {
      return Sort.by(
          sort.stream().map(s ->
              new Sort.Order(
                  s.getDirection() == Direction.DIRECTION_UNSPECIFIED
                      ? Sort.DEFAULT_DIRECTION
                      :
                      Sort.Direction.valueOf(s.getDirection().name()),
                  s.getProperty()
              )).toList()
      );
    }
    return Sort.unsorted();
  }
}
