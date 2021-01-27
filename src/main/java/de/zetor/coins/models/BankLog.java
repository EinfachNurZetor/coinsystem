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
public class BankLog {

    private String bankID;
    private String uuid;
    private String logMSG;
    private long time;

}
