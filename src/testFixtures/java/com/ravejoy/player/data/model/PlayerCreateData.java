package com.ravejoy.player.data.model;

public record PlayerCreateData(
    String login, String screenName, String role, int age, String gender, String password) {}
