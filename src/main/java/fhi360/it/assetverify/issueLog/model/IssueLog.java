package fhi360.it.assetverify.issueLog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "issue_log")
public class IssueLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String states;
    private String description;
    private String dateReceived;
    private String dateIssued;
    private String poNumber;
    private String vendor;
    private String unit;
    private String stockState;
    private String quantityReceived;
    private String issuedTo;
    private String quantityIssued;
    private String balance;
    private Long inventoryId;
//    private Long userId;

}
