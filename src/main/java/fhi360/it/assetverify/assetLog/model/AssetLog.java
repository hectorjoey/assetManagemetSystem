package fhi360.it.assetverify.assetLog.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="assetLog")
public class AssetLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "date")
    private String date;

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

    @Column(name = "phone")
    private String phone;

    @Column(name = "status")
    private String status;

    private Long assetsId;

}
