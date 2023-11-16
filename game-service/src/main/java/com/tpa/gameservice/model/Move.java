package com.tpa.gameservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "move")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Move {
    private Player player;
    private String[] coordinates;
}
