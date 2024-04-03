package fhi360.it.assetverify.binCard.repository;

import fhi360.it.assetverify.binCard.model.BinCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BinCardRepository extends JpaRepository<BinCard, Long> {

    @Query("Select b from BinCard b where b.states=:keyword OR b.poNumber=:keyword OR b.description=:keyboard OR b.issuedTo =: keyword")
    Page<BinCard> findAll(Pageable pageable, @Param("keyword") String keyword);

    Page<BinCard> findByOrderByIdAsc(Pageable pageable);

    List<BinCard> findByInventoryId(Long inventoryId);
//    List<BinCard> findByInventoryId(Long inventoryId);
}
