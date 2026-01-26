package guru.qa.niffler.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CurrencyValues {
  RUB("₽"), USD("$"), EUR("€"), KZT("₸");

  public final String symbol;
}