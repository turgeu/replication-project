package com.example.demo.controller;

import com.example.demo.service.MySQLService;
import com.example.demo.service.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/replicate")
public class ReplicationController {

    @Autowired
    private MySQLService mySQLService;

    @Autowired
    private MongoService mongoService;

    @GetMapping
    public String replicate() {
        List<Map<String, Object>> data = mySQLService.fetchData(
                "SELECT id, product_int_id, name, value FROM product_attributes"
        );

        mongoService.saveData("INKA_PRODUCT_PV_LIST_BY_ID", data);
        return "Replication tamamlandı. " + data.size() + " kayıt aktarıldı.";
    }
}
