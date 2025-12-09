package guru.qa.niffler.page.component;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class StatComponent extends BaseComponent<StatComponent> {

  public StatComponent() {
    super($("#stat"));
  }

  private final ElementsCollection bubbles = self.$("#legend-container").$$("li");
  private final SelenideElement statisticCanvas = $("canvas[role='img']");

  @Step("Check that statistic bubbles contain texts {0}")
  @Nonnull
  public StatComponent checkStatisticBubblesContains(String... texts) {
    bubbles.should(CollectionCondition.texts(texts));
    return this;
  }

  @Step("Check that statistic image matches the expected image")
  @Nonnull
  public StatComponent checkStatisticImage(BufferedImage expectedImage) throws IOException {
    Selenide.sleep(2000);
    BufferedImage actualImage = ImageIO.read(Objects.requireNonNull(statisticCanvas.screenshot()));
    assertFalse(
        new ScreenDiffResult(
            actualImage,
            expectedImage
        )
    );
    return this;
  }
}
