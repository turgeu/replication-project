package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MongoService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void saveData(String collectionName, List<Map<String, Object>> data) {
        if (data != null && !data.isEmpty()) {
            mongoTemplate.insert(data, collectionName);
            System.out.println(data.size() + " satır MongoDB'ye yazıldı (collection: " + collectionName + ").");
        }
    }

    /*
     * Örn:
     *  indexName = "idx_comp_id"
     *  indexSpec = "atr_int_id:ASC, atr_int_source:ASC"
     */
    public void createIndexIfNotExists(String collectionName, String indexName, String indexSpec) {
        if (collectionName == null || collectionName.isBlank()) return;
        if (indexName == null || indexName.isBlank()) return;
        if (indexSpec == null || indexSpec.isBlank()) return;

        IndexOperations ops = mongoTemplate.indexOps(collectionName);

        // aynı isimde index var mı?
        List<IndexInfo> infos = ops.getIndexInfo();
        boolean exists = infos.stream().anyMatch(i -> indexName.equals(i.getName()));
        if (exists) {
            System.out.printf("MongoDB index zaten var: %s on %s%n", indexName, collectionName);
            return;
        }

        Index idx = new Index().named(indexName);

        // "field[:ASC|DESC]" virgülle ayrılmış
        String[] parts = indexSpec.split(",");
        for (String part : parts) {
            String p = part.trim();
            if (p.isEmpty()) continue;
            String[] fv = p.split(":");
            String field = fv[0].trim();
            Sort.Direction dir = (fv.length > 1 && fv[1].trim().equalsIgnoreCase("DESC"))
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            idx = idx.on(field, dir);
        }

        ops.createIndex(idx); // ensureIndex depreceated, createIndex kullan
        System.out.printf("MongoDB index oluşturuldu: %s on %s (%s)%n", indexName, collectionName, indexSpec);
    }
}
