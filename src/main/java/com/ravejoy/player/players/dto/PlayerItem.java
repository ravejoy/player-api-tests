package com.ravejoy.player.players.dto;

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public record PlayerItem(int age, String gender, long id, String role, String screenName) {}
