package guru.qa.niffler.api.core;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

public enum ThreadSafeCookieStore implements CookieStore {
  INSTANCE;

  private final ThreadLocal<CookieStore> cs = ThreadLocal.withInitial(
      ThreadSafeCookieStore::inMemoryCookieStore
  );

  @Override
  public void add(URI uri, HttpCookie cookie) {
    cs.get().add(uri, cookie);
  }

  @Override
  public List<HttpCookie> get(URI uri) {
    return cs.get().get(uri);
  }

  @Override
  public List<HttpCookie> getCookies() {
    return cs.get().getCookies();
  }

  @Override
  public List<URI> getURIs() {
    return cs.get().getURIs();
  }

  @Override
  public boolean remove(URI uri, HttpCookie cookie) {
    return cs.get().remove(uri, cookie);
  }

  @Override
  public boolean removeAll() {
    return cs.get().removeAll();
  }

  public String xsrfCookie() {
    return cs.get().getCookies()
        .stream()
        .filter(c -> c.getName().equals("XSRF-TOKEN"))
        .findFirst()
        .get()
        .getValue();
  }

  private static CookieStore inMemoryCookieStore() {
    return new CookieManager(null, CookiePolicy.ACCEPT_ALL).getCookieStore();
  }
}
