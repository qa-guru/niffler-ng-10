package guru.qa.niffler.page.component;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.Bubble;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.ScreenshotConditions.image;
import static guru.qa.niffler.condition.StatConditions.statBubble;

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
  public StatComponent checkStatisticImage(BufferedImage expectedImage) {
    chart.shouldHave(image(expectedImage));
    return this;
  }

  @Step("Check that stat contains bubbles {expectedBubbles}")
  @Nonnull
  public StatComponent checkBubbles(Bubble... expectedBubbles) {
    bubbles.should(statBubble(expectedBubbles));
    return this;
  }
}
