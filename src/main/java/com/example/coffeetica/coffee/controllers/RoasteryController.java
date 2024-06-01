package com.example.coffeetica.coffee.controllers;

import com.example.coffeetica.coffee.models.RoasteryDTO;
import com.example.coffeetica.coffee.services.RoasteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class RoasteryController {

    @Autowired
    private RoasteryService roasteryService;

    @GetMapping("/api/roasteries/")
    public List<RoasteryDTO> getAllRoasteries() {
        return roasteryService.findAllRoasteries();
    }

    @GetMapping("/api/roasteries/{id}")
    public ResponseEntity<RoasteryDTO> getRoasteryById(@PathVariable Long id) {
        Optional<RoasteryDTO> roasteryDTO = roasteryService.findRoasteryById(id);
        return roasteryDTO
                .map(roastery -> new ResponseEntity<>(roastery, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/api/roasteries/")
    public ResponseEntity<RoasteryDTO> createRoastery(@RequestBody RoasteryDTO roasteryDTO) {
        RoasteryDTO savedRoasteryDTO = roasteryService.saveRoastery(roasteryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoasteryDTO);
    }

    @PutMapping("/api/roasteries/{id}")
    public ResponseEntity<RoasteryDTO> updateRoastery(@PathVariable Long id, @RequestBody RoasteryDTO roasteryDetails) {
        try {
            RoasteryDTO updatedRoasteryDTO = roasteryService.updateRoastery(id, roasteryDetails);
            return ResponseEntity.ok(updatedRoasteryDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/api/roasteries/{id}")
    public ResponseEntity deleteRoastery(@PathVariable Long id) {
        roasteryService.deleteRoastery(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
