package com.ravejoy.player.dataproviders;

import com.ravejoy.player.testsupport.Editor;
import com.ravejoy.player.testsupport.Role;
import java.util.stream.Stream;
import org.testng.annotations.DataProvider;

public final class UpdateDataProviders {
  private UpdateDataProviders() {}

  @DataProvider(name = "invalidAges", parallel = true)
  public static Object[][] invalidAges() {
    return Stream.of(new Object[] {15}, new Object[] {60}, new Object[] {0}, new Object[] {-1})
        .toArray(Object[][]::new);
  }

  @DataProvider(name = "rbacEditors", parallel = true)
  public static Object[][] rbacEditors() {
    return Stream.of(
            new Object[] {Editor.USER, Role.USER, 403},
            new Object[] {Editor.USER, Role.ADMIN, 403},
            new Object[] {Editor.ADMIN, Role.ADMIN, 403},
            new Object[] {Editor.SUPERVISOR, Role.USER, 200},
            new Object[] {Editor.SUPERVISOR, Role.ADMIN, 200})
        .toArray(Object[][]::new);
  }

  @DataProvider(name = "rolesToCreate", parallel = true)
  public static Object[][] rolesToCreate() {
    return Stream.of(new Object[] {Role.USER}, new Object[] {Role.ADMIN}).toArray(Object[][]::new);
  }
}
