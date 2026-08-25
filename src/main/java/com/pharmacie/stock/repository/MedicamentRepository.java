package com.pharmacie.stock.repository;

import com.pharmacie.stock.model.Medicament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MedicamentRepository extends JpaRepository<Medicament, Long> {

    List<Medicament> findByNomContainingIgnoreCase(String nom);

    List<Medicament> findByQuantiteStockLessThanEqual(int seuil);

    List<Medicament> findByDateExpirationBefore(LocalDate date);
}
