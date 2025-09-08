package com.example.demo.service;

import com.example.demo.model.ReplicationConfig;
import com.example.demo.repository.ReplicationConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.List;

@Component
public class ReplicationScheduler {

    private final ReplicationConfigRepository configRepository;
    private final MySQLService mySQLService;
    private final MongoService mongoService;

    @Autowired
    public ReplicationScheduler(ReplicationConfigRepository configRepository, MySQLService mySQLService, MongoService mongoService) {
        this.configRepository = configRepository;
        this.mySQLService = mySQLService;
        this.mongoService = mongoService;
    }

    // Polling: her 30 saniyede bir DB'yi kontrol eder
    @Scheduled(fixedRate = 30000)
    public void pollAndExecute() {
        List<ReplicationConfig> configs = configRepository.findByActiveTrue();
        for (ReplicationConfig cfg : configs) {
            try {
                if (shouldRun(cfg)) {
                    runReplicationFor(cfg);
                }
            } catch (Exception e) {
                System.err.println("Replication error for config id=" + cfg.getId());
                e.printStackTrace();
            }
        }
    }
    private String normalizeCronToQuartz(String expr) {
        // 5 alanlı "min hour dom mon dow" -> başa saniye=0 ekle
        // DOM ve DOW aynı anda '*' ise DOW'u '?' yap
        String s = expr.trim().replaceAll("\\s+", " ");
        String[] p = s.split(" ");
        if (p.length == 5) {
            String min = p[0], hour = p[1], dom = p[2], mon = p[3], dow = p[4];
            if ("*".equals(dom) && "*".equals(dow)) dow = "?";
            return String.join(" ", "0", min, hour, dom, mon, dow);
        }
        if (p.length == 6) {
            if ("*".equals(p[3]) && "*".equals(p[5])) p[5] = "?";
            return String.join(" ", p);
        }
        throw new IllegalArgumentException("Cron must have 5 or 6 fields: " + expr);
    }

    private boolean shouldRun(ReplicationConfig cfg) {
        String sched = cfg.getSchedule();
        LocalDateTime last = cfg.getLastRun();
        LocalDateTime now = LocalDateTime.now();

        if (sched == null || sched.isBlank()) return false;

        // sadece rakamsa saniye bazlı interval
        if (sched.matches("^\\d+$")) {
            long seconds = Long.parseLong(sched);
            return last == null || Duration.between(last, now).getSeconds() >= seconds;
        }

        try {
            String quartzCron = normalizeCronToQuartz(sched);
            CronExpression cron = CronExpression.parse(quartzCron);

            Instant base = (last != null)
                    ? last.atZone(ZoneId.systemDefault()).toInstant()
                    : Instant.now().minusSeconds(1);
            Instant next = cron.next(base);
            return next != null && !next.isAfter(Instant.now());
        } catch (Exception ex) {
            System.err.println("Invalid cron expression for config id=" + cfg.getId() + " -> " + sched);
            return false;
        }
    }


    private void runReplicationFor(ReplicationConfig cfg) {
        System.out.println("Replication başladı for config id=" + cfg.getId());

        List<java.util.Map<String, Object>> data = mySQLService.fetchData(cfg.getSourceQuery());

        mongoService.saveData(cfg.getMongoCollection(), data);

        // index adı + yönlü alanlar
        mongoService.createIndexIfNotExists(
                cfg.getMongoCollection(),
                cfg.getMongoIndexName(),
                cfg.getMongoIndex()
        );

        cfg.setLastRun(LocalDateTime.now());
        configRepository.save(cfg);

        System.out.println("Replication tamamlandı for config id=" + cfg.getId()
                + " -> " + (data != null ? data.size() : 0) + " kayıt aktarıldı.");
    }

}
