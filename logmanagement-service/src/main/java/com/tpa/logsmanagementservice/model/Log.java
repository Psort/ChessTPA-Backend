package com.tpa.logsmanagementservice.model;

import com.tpa.logsmanagementservice.type.LogType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "logs")
public class Log {
    @Id
    @GeneratedValue
    private Long id;
    private String serviceName;
    private LogType type;
    private String message;
    private ZonedDateTime timestamp;

}
