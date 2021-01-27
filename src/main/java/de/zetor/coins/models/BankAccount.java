package de.zetor.coins.models;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BankAccount {

    private String bankID;
    private String uuid;
    private String bankname;
    private int coins;

}
