package com.mjtech.pos.repository;

import com.mjtech.pos.entity.GeneralLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralLedgerRepository extends JpaRepository<GeneralLedger, Integer>, JpaSpecificationExecutor<GeneralLedger> {
}
