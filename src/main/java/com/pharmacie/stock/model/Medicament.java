package com.pharmacie.stock.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Entity
@Table(name = "medicaments")
public class Medicament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du médicament est obligatoire")
    private String nom;

    private String categorie;

    private String fournisseur;

    @Min(value = 0, message = "La quantité en stock ne peut pas être négative")
    private int quantiteStock;

    @Min(value = 0, message = "Le seuil d'alerte ne peut pas être négatif")
    private int quantiteMinimale;

    @Positive(message = "Le prix unitaire doit être positif")
    private double prixUnitaire;

    @NotNull(message = "La date d'expiration est obligatoire")
    private LocalDate dateExpiration;

    public Medicament() {
    }

    public Medicament(String nom, String categorie, String fournisseur, int quantiteStock,
                       int quantiteMinimale, double prixUnitaire, LocalDate dateExpiration) {
        this.nom = nom;
        this.categorie = categorie;
        this.fournisseur = fournisseur;
        this.quantiteStock = quantiteStock;
        this.quantiteMinimale = quantiteMinimale;
        this.prixUnitaire = prixUnitaire;
        this.dateExpiration = dateExpiration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        this.fournisseur = fournisseur;
    }

    public int getQuantiteStock() {
        return quantiteStock;
    }

    public void setQuantiteStock(int quantiteStock) {
        this.quantiteStock = quantiteStock;
    }

    public int getQuantiteMinimale() {
        return quantiteMinimale;
    }

    public void setQuantiteMinimale(int quantiteMinimale) {
        this.quantiteMinimale = quantiteMinimale;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public LocalDate getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }
}
