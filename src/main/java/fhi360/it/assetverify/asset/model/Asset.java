package fhi360.it.assetverify.asset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "assets")
public class Asset implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "description")
    private String description;

    @Column(name = "assetId")
    private String assetId;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "modelNumber")
    private String modelNumber;

    @Column(name = "serialNumber")
    private String serialNumber;

    @Column(name = "poNumber")
    private String poNumber;

    @Column(name = "dateReceived")
    private String dateReceived;

    @Column(name = "purchasedPriceInDollars")
    private String purchasedPriceInDollars;

    @Column(name = "purchasePrice")
    private String purchasePrice;

    @Column(name = "depreciationValue")
    private String depreciationValue;

    @Column(name = "funder")
    private String funder;

    @Column(name = "project")
    private String project;

    @Column(name = "usefulLifeSpan")
    private String usefulLifeSpan;

    @Setter
    @Column(name = "currentAgeOfAsset")
    private String currentAgeOfAsset;

    @Column(name = "condition")
    private String condition;

    @Column(name = "states")
    private String states;

    @Column(name = "facility")
    private String facility;

    @Column(name = "locationAndAssignee")
    private String locationAndAssignee;

    @Column(name = "emailAddress")
    private String emailAddress;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "status")
    private String status;

    @Column(name = "receivedBy")
    private String receivedBy;

}
