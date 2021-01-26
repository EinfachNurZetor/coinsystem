package de.zetor.coins.models;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class bankAccount {

    private String bankID;
    private UUID uuid;
    private String bankname;
    private Integer coins;

}
