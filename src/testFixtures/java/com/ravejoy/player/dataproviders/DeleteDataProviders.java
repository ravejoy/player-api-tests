package com.ravejoy.player.dataproviders;

import static com.ravejoy.player.http.StatusCode.FORBIDDEN;

import com.ravejoy.player.testsupport.Editor;
import com.ravejoy.player.testsupport.Role;
import java.util.stream.Stream;
import org.testng.annotations.DataProvider;

public final class DeleteDataProviders {
  private DeleteDataProviders() {}

  @DataProvider(name = "rolesToCreate", parallel = true)
  public static Object[][] rolesToCreate() {
    return Stream.of(new Object[] {Role.USER}).toArray(Object[][]::new);
  }

  @DataProvider(name = "rbacAllowedMatrix", parallel = true)
  public static Object[][] rbacAllowedMatrix() {
    return Stream.of(
            new Object[] {Editor.SUPERVISOR, Role.USER},
            new Object[] {Editor.SUPERVISOR, Role.ADMIN})
        .toArray(Object[][]::new);
  }

  @DataProvider(name = "rbacUserNegativeMatrix", parallel = true)
  public static Object[][] rbacUserNegativeMatrix() {
    return Stream.of(new Object[] {Editor.USER, Role.USER}, new Object[] {Editor.USER, Role.ADMIN})
        .toArray(Object[][]::new);
  }

  @DataProvider(name = "rbacMatrix", parallel = true)
  public static Object[][] rbacMatrixCompat() {
    return Stream.of(
            new Object[] {Editor.USER, Role.USER, FORBIDDEN},
            new Object[] {Editor.USER, Role.ADMIN, FORBIDDEN})
        .toArray(Object[][]::new);
  }
}
