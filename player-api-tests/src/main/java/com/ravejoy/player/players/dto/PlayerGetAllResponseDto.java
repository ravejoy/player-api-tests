package com.ravejoy.player.players.dto;

import java.util.List;

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public record PlayerGetAllResponseDto(List<PlayerItem> players) {}
