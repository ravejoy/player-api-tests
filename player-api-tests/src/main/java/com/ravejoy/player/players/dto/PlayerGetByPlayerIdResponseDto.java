package com.ravejoy.player.players.dto;

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public record PlayerGetByPlayerIdResponseDto(
    int age,
    String gender,
    long id,
    String login,
    String password,
    String role,
    String screenName) {}
