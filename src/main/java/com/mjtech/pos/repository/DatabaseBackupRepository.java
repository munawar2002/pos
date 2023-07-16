package com.mjtech.pos.repository;

import com.mjtech.pos.entity.DatabaseBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DatabaseBackupRepository extends JpaRepository<DatabaseBackup, Integer>, JpaSpecificationExecutor<DatabaseBackup> {

    @Query("SELECT e FROM DatabaseBackup e WHERE DATE(e.backupDate) = DATE(:dateParameter)")
    List<DatabaseBackup> findByBackupDateOnly(Date dateParameter);
}
