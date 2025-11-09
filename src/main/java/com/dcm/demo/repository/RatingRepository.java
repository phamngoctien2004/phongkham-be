package com.dcm.demo.repository;

import com.dcm.demo.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    @Query("""
            SELECT AVG(r.ratingScore), COUNT(r)
            FROM Rating r
            WHERE r.user.doctor.id = :doctorId
            """)
    Object[] getAverageRatingByDoctor(Integer doctorId);
}
