package fhi360.it.assetverify.issueLog.repository;

import fhi360.it.assetverify.issueLog.model.IssueLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssueLogRepository extends JpaRepository<IssueLog, Long> {
    @Query("Select i from IssueLog i where i.states=:keyword OR i.description=:keyboard OR i.poNumber =: keyword OR i.issuedTo =: keyword")
    Page<IssueLog> findAll(Pageable pageable, @Param("keyword") String keyword);

    Page<IssueLog> findByOrderByIdAsc(Pageable pageable);

    List<IssueLog> findByInventoryId(Long inventoryId);
//    Page<IssueLog> findByDateReceivedBetween(String startDate, String endDate, Pageable pageable);
//    Page<IssueLog> findByDateIssuedBetween(String startDate, String endDate, Pageable pageable);
    Page<IssueLog> findByDateBetween(String startDate, String endDate, Pageable pageable);

    List<IssueLog> findByDateReceivedBetween(String startDate, String endDate);

    List<IssueLog> findByDescriptionContaining(String description);

    List<IssueLog> findByDescription(String description);

    List<IssueLog> findByDateReceived(String date);

    List<IssueLog> findByStates(String states, Pageable pageable);
}
