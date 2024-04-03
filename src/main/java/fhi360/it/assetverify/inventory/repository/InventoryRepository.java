package fhi360.it.assetverify.inventory.repository;

import fhi360.it.assetverify.inventory.model.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("Select i from Inventory i where i.states =:keyword OR i.description=:keyword OR i.dateReceived =: keyword OR i.poNumber=: keyword")
    Page<Inventory> findAll(Pageable pageable, @Param("keyword") String keyword);
    Page<Inventory> findByOrderByIdAsc(Pageable pageable);
    List<Inventory> findByDateReceivedBetween (String startDate, String endDate);

    Page<Inventory> findByDateReceivedBetween(String startDate, String endDate, Pageable pageable);

    List<Inventory> findByDescription(String description);

    Inventory findOneByDescription(String description);

    Page<Inventory> findByStates(String state, Pageable pageable);

}