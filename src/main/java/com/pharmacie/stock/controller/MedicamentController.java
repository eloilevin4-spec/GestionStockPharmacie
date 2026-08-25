package com.pharmacie.stock.controller;

import com.pharmacie.stock.model.Medicament;
import com.pharmacie.stock.service.MedicamentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicaments")
public class MedicamentController {

    private final MedicamentService service;

    @Autowired
    public MedicamentController(MedicamentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Medicament ajouter(@Valid @RequestBody Medicament medicament) {
        return service.ajouterMedicament(medicament);
    }

    @PutMapping("/{id}")
    public Medicament modifier(@PathVariable Long id, @Valid @RequestBody Medicament medicament) {
        return service.modifierMedicament(id, medicament);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimerMedicament(id);
    }

    @GetMapping("/{id}")
    public Medicament parId(@PathVariable Long id) {
        return service.rechercherParId(id);
    }

    @GetMapping
    public List<Medicament> tous(@RequestParam(required = false) String nom) {
        if (nom != null && !nom.isBlank()) {
            return service.rechercherParNom(nom);
        }
        return service.listerTous();
    }

    @PostMapping("/{id}/entree")
    public Medicament entreeStock(@PathVariable Long id, @RequestParam int quantite) {
        return service.entreeStock(id, quantite);
    }

    @PostMapping("/{id}/sortie")
    public Medicament sortieStock(@PathVariable Long id, @RequestParam int quantite) {
        return service.sortieStock(id, quantite);
    }

    @GetMapping("/alertes/stock-faible")
    public List<Medicament> alertesStockFaible() {
        return service.alertesStockFaible();
    }

    @GetMapping("/alertes/expiration")
    public List<Medicament> alertesExpiration(@RequestParam(defaultValue = "30") int jours) {
        return service.alertesExpirationProche(jours);
    }
}
