package de.zetor.coins.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class bankLog {

    private String bankID;
    private UUID uuid;
    private String logMSG;

}
