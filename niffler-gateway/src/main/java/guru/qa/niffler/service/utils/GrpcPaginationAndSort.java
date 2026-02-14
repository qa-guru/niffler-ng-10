package guru.qa.niffler.service.utils;

import guru.qa.niffler.grpc.Direction;
import guru.qa.niffler.grpc.PageInfo;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;

public class GrpcPaginationAndSort {
  private final Pageable pageable;

  public GrpcPaginationAndSort(@Nonnull Pageable pageable) {
    this.pageable = pageable;
  }

  public @Nonnull PageInfo pageInfo() {
    return PageInfo.newBuilder()
        .setPage(pageable.getPageNumber())
        .setSize(pageable.getPageSize())
        .addAllSort(pageable.getSort().stream()
            .map(s -> guru.qa.niffler.grpc.Sort.newBuilder()
                .setDirection(Direction.valueOf(s.getDirection().name()))
                .setProperty(s.getProperty())
                .build())
            .toList())
        .build();
  }
}
