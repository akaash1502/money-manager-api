package in.springboot.moneymanager.repository;

import in.springboot.moneymanager.entity.ExpenseEntity;
import in.springboot.moneymanager.entity.IncomeEntity;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {

    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    //JPQL Query
    @Query("SELECT SUM(e.amount) FROM IncomeEntity e WHERE e.profile.id = :profileId")
    BigDecimal findTotalIncomeByProfileId(@Param("profileId") Long profileId);

    // select * from tbl_incomes where profile_id = ?1 and date between ?2 and ?3 order by date desc and name like '%?4%'
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );


    // select * from tbl_incomes where profile_id = ?1 and date between ?2 and ?3
    List<IncomeEntity> findByProfileIdAndDateBetween(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate
    );
}
