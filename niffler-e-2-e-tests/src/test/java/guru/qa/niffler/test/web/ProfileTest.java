package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;
import static guru.qa.niffler.utils.RandomDataUtils.randomName;

@WebTest
public class ProfileTest {

  @Test
  @User(
      categories = @Category(
          archived = true
      )
  )
  @ApiLogin
  void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
    final String categoryName = user.testData().categoryDescriptions()[0];

    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .checkArchivedCategoryExists(categoryName);
  }

  @Test
  @User(
      categories = @Category(
          archived = false
      )
  )
  @ApiLogin
  void activeCategoryShouldPresentInCategoriesList(UserJson user) {
    final String categoryName = user.testData().categoryDescriptions()[0];

    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .checkCategoryExists(categoryName);
  }

  @User
  @ApiLogin
  @ScreenShotTest(value = "expected-avatar.png")
  void shouldUpdateProfileWithAllFieldsSet(BufferedImage expectedAvatar) throws IOException {
    final String newName = randomName();

    ProfilePage profilePage = Selenide.open(ProfilePage.URL, ProfilePage.class)
        .uploadPhotoFromClasspath("img/cat.jpeg")
        .setName(newName)
        .submitProfile()
        .checkAlert("Profile successfully updated");

    Selenide.refresh();

    profilePage.checkName(newName)
        .checkPhotoExist()
        .checkPhoto(expectedAvatar);
  }

  @Test
  @User
  @ApiLogin
  void shouldUpdateProfileWithOnlyRequiredFields() {
    final String newName = randomName();

    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .setName(newName)
        .submitProfile()
        .checkAlert("Profile successfully updated");

    Selenide.refresh();

    new ProfilePage().checkName(newName);
  }

  @Test
  @User
  @ApiLogin
  void shouldAddNewCategory() {
    String newCategory = randomCategoryName();

    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .addCategory(newCategory)
        .checkAlert("You've added new category:")
        .checkCategoryExists(newCategory);
  }

  @Test
  @User(
      categories = {
          @Category(name = "Food"),
          @Category(name = "Bars"),
          @Category(name = "Clothes"),
          @Category(name = "Friends"),
          @Category(name = "Music"),
          @Category(name = "Sports"),
          @Category(name = "Walks"),
          @Category(name = "Books")
      }
  )
  @ApiLogin
  void shouldForbidAddingMoreThat8Categories() {
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .checkThatCategoryInputDisabled();
  }
}
