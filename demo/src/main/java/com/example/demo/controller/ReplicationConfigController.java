package com.example.demo.controller;

import com.example.demo.model.ReplicationConfig;
import com.example.demo.repository.ReplicationConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/configs")
@CrossOrigin(origins = "http://localhost:3000")
public class ReplicationConfigController {

    @Autowired
    private ReplicationConfigRepository repository;

    @GetMapping
    public List<ReplicationConfig> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ReplicationConfig getOne(@PathVariable Integer id) {
        return repository.findById(id).orElseThrow();
    }

    @PostMapping
    public ReplicationConfig create(@RequestBody ReplicationConfig in) {
        in.setId(null);
        return repository.save(in);
    }

    @PutMapping("/{id}")
    public ReplicationConfig update(@PathVariable Integer id, @RequestBody ReplicationConfig in) {
        ReplicationConfig cfg = repository.findById(id).orElseThrow();
        cfg.setSourceQuery(in.getSourceQuery());
        cfg.setSchedule(in.getSchedule());
        cfg.setMongoCollection(in.getMongoCollection());
        cfg.setMongoIndex(in.getMongoIndex());
        cfg.setActive(in.getActive());
        return repository.save(cfg);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        repository.deleteById(id);
    }
}
