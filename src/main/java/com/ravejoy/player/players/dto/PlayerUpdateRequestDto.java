package com.ravejoy.player.players.dto;

public record PlayerUpdateRequestDto(
    Integer age, String gender, String login, String password, String role, String screenName) {}
