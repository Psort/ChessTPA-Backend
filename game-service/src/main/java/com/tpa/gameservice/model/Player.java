package com.tpa.gameservice.model;

import com.tpa.gameservice.type.PlayerColor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "player")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Player {
    private String username;
    private PlayerColor color;
}
