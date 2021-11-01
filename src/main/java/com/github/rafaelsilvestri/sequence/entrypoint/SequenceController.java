package com.github.rafaelsilvestri.sequence.entrypoint;

import com.github.rafaelsilvestri.sequence.domain.SequenceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/sequences")
public class SequenceController {

    private final SequenceService service;

    /**
     * @param type group of sequences
     * @return 200 if success otherwise 500
     */
    @PostMapping("/db/{type}")
    public ResponseEntity<Void> createWithDb(@PathVariable("type") String type) {
        service.createWithDb(type);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/redis/{type}")
    public ResponseEntity<Void> createWithRedis(@PathVariable("type") String type) {
        service.createWithRedis(type);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/memcached/{type}")
    public ResponseEntity<Void> createWithMemcached(@PathVariable("type") String type) {
        service.createWithMemcached(type);
        return ResponseEntity.ok().build();
    }
}
