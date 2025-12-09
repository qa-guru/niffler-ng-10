package guru.qa.niffler.page.component;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.extension.ScreenShotTestExtension;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.StatConditions.color;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class StatComponent extends BaseComponent<StatComponent> {

  public StatComponent() {
    super($("#stat"));
  }

  private final ElementsCollection bubbles = self.$("#legend-container").$$("li");
  private final SelenideElement chart = $("canvas[role='img']");

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
    assertFalse(
        new ScreenDiffResult(
            chartScreenshot(),
            expectedImage
        ),
        ScreenShotTestExtension.ASSERT_SCREEN_MESSAGE
    );
    return this;
  }

  @Step("Get screenshot of stat chart")
  @Nonnull
  public BufferedImage chartScreenshot() throws IOException {
    return ImageIO.read(requireNonNull(chart.screenshot()));
  }

  @Step("Check that stat bubbles contains colors {expectedColors}")
  @Nonnull
  public StatComponent checkBubbles(Color... expectedColors) {
    bubbles.should(color(expectedColors));
    return this;
  }
}
