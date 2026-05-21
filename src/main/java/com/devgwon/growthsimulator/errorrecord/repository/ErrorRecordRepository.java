package com.devgwon.growthsimulator.errorrecord.repository;

import com.devgwon.growthsimulator.character.domain.DeveloperProfile;
import com.devgwon.growthsimulator.errorrecord.domain.ErrorRecord;
import com.devgwon.growthsimulator.errorrecord.domain.ErrorStatus;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ErrorRecordRepository extends JpaRepository<ErrorRecord, Long> {

    @EntityGraph(attributePaths = {"growthSubCategory", "growthSubCategory.category", "quest"})
    List<ErrorRecord> findByProfileOrderByOccurredAtDescCreatedAtDesc(DeveloperProfile profile);

    @EntityGraph(attributePaths = {"growthSubCategory", "growthSubCategory.category", "quest"})
    List<ErrorRecord> findByProfileAndStatusOrderByOccurredAtDescCreatedAtDesc(
            DeveloperProfile profile,
            ErrorStatus status
    );

    @EntityGraph(attributePaths = {"growthSubCategory", "growthSubCategory.category", "quest"})
    @Query("""
            select e from ErrorRecord e
            where e.profile = :profile
              and (
                lower(e.title) like lower(concat('%', :keyword, '%'))
                or lower(e.errorName) like lower(concat('%', :keyword, '%'))
                or lower(e.situation) like lower(concat('%', :keyword, '%'))
                or lower(e.solution) like lower(concat('%', :keyword, '%'))
              )
            order by e.occurredAt desc, e.createdAt desc
            """)
    List<ErrorRecord> searchByKeyword(
            @Param("profile") DeveloperProfile profile,
            @Param("keyword") String keyword
    );

    @EntityGraph(attributePaths = {"growthSubCategory", "growthSubCategory.category", "quest"})
    @Query("""
            select e from ErrorRecord e
            where e.profile = :profile
              and e.status = :status
              and (
                lower(e.title) like lower(concat('%', :keyword, '%'))
                or lower(e.errorName) like lower(concat('%', :keyword, '%'))
                or lower(e.situation) like lower(concat('%', :keyword, '%'))
                or lower(e.solution) like lower(concat('%', :keyword, '%'))
              )
            order by e.occurredAt desc, e.createdAt desc
            """)
    List<ErrorRecord> searchByStatusAndKeyword(
            @Param("profile") DeveloperProfile profile,
            @Param("status") ErrorStatus status,
            @Param("keyword") String keyword
    );

    @EntityGraph(attributePaths = {"growthSubCategory", "growthSubCategory.category", "quest"})
    List<ErrorRecord> findTop3ByProfileAndStatusNotOrderByOccurredAtDescCreatedAtDesc(
            DeveloperProfile profile,
            ErrorStatus status
    );

    long countByProfileAndStatus(DeveloperProfile profile, ErrorStatus status);
}
