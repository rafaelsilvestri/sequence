package com.github.rafaelsilvestri.sequence.domain;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SequenceRepository extends CrudRepository<Sequence, Long> {

    @Query("select coalesce(max(s.code),0)+1 from sequence s where s.type = :type")
    Long findNextCode(String type);
}
