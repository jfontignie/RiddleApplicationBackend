package com.jfn.riddle.backend.repository;

import com.jfn.riddle.backend.domain.Riddle;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Riddle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RiddleRepository extends JpaRepository<Riddle, Long> {

}
