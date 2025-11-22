package guru.qa.niffler.data.tpl;

import java.util.List;
//Этот класс делали что бы можно было работать с несколькими холдерами
//и закрыть все холдеры за один раз. Обертка над JdbcConnectionHolder
public class JdbcConnectionHolders implements AutoCloseable {

  private final List<JdbcConnectionHolder> holders;

  public JdbcConnectionHolders(List<JdbcConnectionHolder> holders) {
    this.holders = holders;
  }

  @Override
  public void close() {
    holders.forEach(JdbcConnectionHolder::close);
  }
}
