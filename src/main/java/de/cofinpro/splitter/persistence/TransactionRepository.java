package de.cofinpro.splitter.persistence;

import de.cofinpro.splitter.model.PairBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    void deleteAllByDateIsBefore(@NotNull LocalDate date);

    @Query(value = "SELECT P1.NAME AS firstPerson, P2.NAME AS secondPerson, SUM(T.AMOUNT) AS balance " +
            "FROM TRANSACTION AS T JOIN PERSON P1 on P1.ID = T.FIRST_ID " +
            " JOIN PERSON P2 on P2.ID = T.SECOND_ID WHERE T.DATE <= :date GROUP BY firstPerson, secondPerson",
            nativeQuery = true)
    List<PairBalance> getBalances(@Param("date") LocalDate balanceDate);
}