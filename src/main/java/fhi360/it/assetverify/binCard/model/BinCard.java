
package fhi360.it.assetverify.binCard.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "binCard")
public class BinCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String date;
    private String states;
    private String description;
    private String poNumber;
    private String vendor;
    private String unit;
    private String stockState;
    private String receivedFrom;
    private String issuedTo;
    private String quantityReceived;
    private String quantityIssued;
    private String openingBalance;
    private String balance;

    private Long inventoryId;
}
