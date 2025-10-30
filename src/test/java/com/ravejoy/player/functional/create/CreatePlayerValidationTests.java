package com.ravejoy.player.functional.create;

import static com.ravejoy.player.http.StatusCode.BAD_REQUEST;

import com.ravejoy.player.assertions.ResponseAsserts;
import com.ravejoy.player.data.model.PlayerCreateData;
import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.*;
import io.qameta.allure.*;
import io.restassured.response.Response;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Epic("Player API")
@Feature("Create")
@Story("Validation")
public class CreatePlayerValidationTests {

  private PlayerCreateData valid() {
    return new PlayerCreateData(
        RunIds.login("ok"),
        RunIds.screen("scr"),
        Role.USER.value(),
        24,
        Gender.MALE,
        Password.VALID);
  }

  private Response call(PlayerSteps steps, PlayerCreateData d) {
    return steps.createRaw(
        Editor.SUPERVISOR.value(),
        d.login(),
        d.screenName(),
        d.role(),
        d.age(),
        d.gender(),
        d.password());
  }

  @DataProvider(name = "invalidCreatePassing", parallel = true)
  public Object[][] invalidCreatePassing() {
    return Stream.of(
            new Object[] {
              "empty login",
              (UnaryOperator<PlayerCreateData>)
                  d ->
                      new PlayerCreateData(
                          "", d.screenName(), d.role(), d.age(), d.gender(), d.password())
            },
            new Object[] {
              "empty screenName",
              (UnaryOperator<PlayerCreateData>)
                  d ->
                      new PlayerCreateData(
                          d.login(), "", d.role(), d.age(), d.gender(), d.password())
            },
            new Object[] {
              "negative age",
              (UnaryOperator<PlayerCreateData>)
                  d ->
                      new PlayerCreateData(
                          d.login(), d.screenName(), d.role(), -1, d.gender(), d.password())
            },
            new Object[] {
              "zero age",
              (UnaryOperator<PlayerCreateData>)
                  d ->
                      new PlayerCreateData(
                          d.login(), d.screenName(), d.role(), 0, d.gender(), d.password())
            },
            new Object[] {
              "invalid role",
              (UnaryOperator<PlayerCreateData>)
                  d ->
                      new PlayerCreateData(
                          d.login(), d.screenName(), "king", d.age(), d.gender(), d.password())
            })
        .toArray(Object[][]::new);
  }

  @Description("Invalid create data must be rejected with 400 Bad Request")
  @Test(
      dataProvider = "invalidCreatePassing",
      groups = {Groups.FUNCTIONAL, Groups.CONTRACT})
  public void createPlayerValidation(String title, UnaryOperator<PlayerCreateData> mutate) {
    var steps = new PlayerSteps();
    var broken = mutate.apply(valid());
    var resp = call(steps, broken);

    ResponseAsserts.assertStatus(resp, BAD_REQUEST);
    ResponseAsserts.assertJsonOrEmpty(resp);
  }

  @DataProvider(name = "invalidGender", parallel = true)
  public Object[][] invalidGender() {
    return new Object[][] {
      {
        (UnaryOperator<PlayerCreateData>)
            d ->
                new PlayerCreateData(
                    d.login(), d.screenName(), d.role(), d.age(), "foo", d.password())
      }
    };
  }

  @Issue("VAL-02")
  @Description("Gender must be validated against allowed values; expected 400")
  @Test(
      dataProvider = "invalidGender",
      groups = {Groups.KNOWN_ISSUES, Groups.CONTRACT})
  public void createPlayerValidationInvalidGender(UnaryOperator<PlayerCreateData> mutate) {
    var steps = new PlayerSteps();
    var resp = call(steps, mutate.apply(valid()));

    ResponseAsserts.assertStatus(resp, BAD_REQUEST);
    ResponseAsserts.assertJsonOrEmpty(resp);
  }

  @DataProvider(name = "emptyPassword", parallel = true)
  public Object[][] emptyPassword() {
    return new Object[][] {
      {
        (UnaryOperator<PlayerCreateData>)
            d -> new PlayerCreateData(d.login(), d.screenName(), d.role(), d.age(), d.gender(), "")
      }
    };
  }

  @Issue("VAL-01")
  @Description("Password is required by spec; empty password must be rejected with 400")
  @Test(
      dataProvider = "emptyPassword",
      groups = {Groups.KNOWN_ISSUES, Groups.CONTRACT})
  public void createPlayerValidationEmptyPassword(UnaryOperator<PlayerCreateData> mutate) {
    var steps = new PlayerSteps();
    var resp = call(steps, mutate.apply(valid()));

    ResponseAsserts.assertStatus(resp, BAD_REQUEST);
    ResponseAsserts.assertJsonOrEmpty(resp);
  }
}
