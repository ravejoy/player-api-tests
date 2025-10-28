package com.ravejoy.player.players.dto;

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public record PlayerUpdateResponseDto(
    int age,
    String gender,
    long id,
    String login,
    String role,
    String screenName) {}
