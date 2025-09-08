package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "replication_config")
public class ReplicationConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "source_query", columnDefinition = "TEXT")
    private String sourceQuery;

    private String schedule;

    @Column(name = "mongo_collection")
    private String mongoCollection;

    @Column(name = "mongo_index")
    private String mongoIndex;

    @Column(name = "mongo_index_name")
    private String mongoIndexName;

    @Column(name = "last_run")
    private LocalDateTime lastRun;

    private Boolean active = true;

    // --- getters / setters ---
    public String getMongoIndexName() { return mongoIndexName; }
    public void setMongoIndexName(String mongoIndexName) { this.mongoIndexName = mongoIndexName; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSourceQuery() { return sourceQuery; }
    public void setSourceQuery(String sourceQuery) { this.sourceQuery = sourceQuery; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    public String getMongoCollection() { return mongoCollection; }
    public void setMongoCollection(String mongoCollection) { this.mongoCollection = mongoCollection; }

    public String getMongoIndex() { return mongoIndex; }
    public void setMongoIndex(String mongoIndex) { this.mongoIndex = mongoIndex; }

    public LocalDateTime getLastRun() { return lastRun; }
    public void setLastRun(LocalDateTime lastRun) { this.lastRun = lastRun; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
