package com.devgwon.growthsimulator.repository;

import com.devgwon.growthsimulator.entity.DailyReview;
import com.devgwon.growthsimulator.entity.DeveloperProfile;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyReviewRepository extends JpaRepository<DailyReview, Long> {

    List<DailyReview> findByProfileOrderByReviewDateDesc(DeveloperProfile profile);

    List<DailyReview> findTop3ByProfileOrderByReviewDateDesc(DeveloperProfile profile);

    Optional<DailyReview> findByProfileAndReviewDate(DeveloperProfile profile, LocalDate reviewDate);

    boolean existsByProfileAndReviewDate(DeveloperProfile profile, LocalDate reviewDate);

    List<DailyReview> findByProfileAndReviewDateBetweenOrderByReviewDateDesc(
            DeveloperProfile profile,
            LocalDate start,
            LocalDate end
    );
}
