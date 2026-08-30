package com.pharmacie.stock.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pharmacie.stock.exception.MedicamentNotFoundException;
import com.pharmacie.stock.exception.StockInsuffisantException;
import com.pharmacie.stock.model.Medicament;
import com.pharmacie.stock.repository.MedicamentRepository;

@Service
public class MedicamentServiceImpl implements MedicamentService {

    private final MedicamentRepository repository;

    @Autowired
    public MedicamentServiceImpl(MedicamentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Medicament ajouterMedicament(Medicament medicament) {
        return repository.save(medicament);
    }

    @Override
    public Medicament modifierMedicament(Long id, Medicament medicament) {
        Medicament existant = rechercherParId(id);
        existant.setNom(medicament.getNom());
        existant.setCategorie(medicament.getCategorie());
        existant.setFournisseur(medicament.getFournisseur());
        existant.setQuantiteStock(medicament.getQuantiteStock());
        existant.setQuantiteMinimale(medicament.getQuantiteMinimale());
        existant.setPrixUnitaire(medicament.getPrixUnitaire());
        existant.setDateExpiration(medicament.getDateExpiration());
        return repository.save(existant);
    }

    @Override
    public void supprimerMedicament(Long id) {
        Medicament existant = rechercherParId(id);
        repository.delete(existant);
    }

    @Override
    public Medicament rechercherParId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new MedicamentNotFoundException(id));
    }

    @Override
    public List<Medicament> rechercherParNom(String nom) {
        return repository.findByNomContainingIgnoreCase(nom);
    }

    @Override
    public List<Medicament> listerTous() {
        return repository.findAll();
    }

    @Override
    public Medicament entreeStock(Long id, int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité à ajouter doit être positive");
        }
        Medicament medicament = rechercherParId(id);
        medicament.setQuantiteStock(medicament.getQuantiteStock() + quantite);
        return repository.save(medicament);
    }

    @Override
    public Medicament sortieStock(Long id, int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité à retirer doit être positive");
        }
        Medicament medicament = rechercherParId(id);
        if (medicament.getQuantiteStock() < quantite) {
            throw new StockInsuffisantException(medicament.getNom(), quantite, medicament.getQuantiteStock());
        }
        medicament.setQuantiteStock(medicament.getQuantiteStock() - quantite);
        return repository.save(medicament);
    }

    @Override
    public List<Medicament> alertesStockFaible() {
        return repository.findAll().stream()
                .filter(m -> m.getQuantiteStock() <= m.getQuantiteMinimale())
                .toList();
    }

    @Override
    public List<Medicament> alertesExpirationProche(int nombreDeJours) {
        LocalDate limite = LocalDate.now().plusDays(nombreDeJours);
        return repository.findByDateExpirationBefore(limite);
    }
}
