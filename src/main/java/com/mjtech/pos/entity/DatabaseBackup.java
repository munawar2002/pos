package com.mjtech.pos.entity;

import com.mjtech.pos.constant.DatabaseBackupStatus;
import com.mjtech.pos.util.ActiveUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DATABASE_BACKUP")
@Builder
public class DatabaseBackup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "BACKUP_FILENAME")
    private String backupFileName;

    @Column(name = "BACKUP_DATE")
    private Date backupDate;

    @Column(name = "UPLOAD_STATUS")
    @Enumerated(EnumType.STRING)
    private DatabaseBackupStatus uploadStatus;

    @Column(name = "SHARING_STATUS")
    @Enumerated(EnumType.STRING)
    private DatabaseBackupStatus sharingStatus;

    @Column(name = "SCRIPT_STATUS")
    @Enumerated(EnumType.STRING)
    private DatabaseBackupStatus scriptStatus;

    @Column(name = "CLIENT_NAME")
    private String clientName;

    @Column(name = "GOOGLE_DRIVE_FILE_ID")
    private String googleDriveFileId;

    @Column(name = "BACKUP_RESPONSE")
    private String backupResponse;

    @Column(name = "created_by")
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = new Date();
        createdBy = ActiveUser.getActiveUsername();
    }
}
