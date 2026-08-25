package com.pharmacie.stock.service;

import com.pharmacie.stock.model.Medicament;

import java.util.List;

public interface MedicamentService {

    Medicament ajouterMedicament(Medicament medicament);

    Medicament modifierMedicament(Long id, Medicament medicament);

    void supprimerMedicament(Long id);

    Medicament rechercherParId(Long id);

    List<Medicament> rechercherParNom(String nom);

    List<Medicament> listerTous();

    Medicament entreeStock(Long id, int quantite);

    Medicament sortieStock(Long id, int quantite);

    List<Medicament> alertesStockFaible();

    List<Medicament> alertesExpirationProche(int nombreDeJours);
}
