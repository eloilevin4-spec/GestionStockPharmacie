package com.pharmacie.stock.exception;

public class MedicamentNotFoundException extends RuntimeException {
    public MedicamentNotFoundException(Long id) {
        super("Aucun médicament trouvé avec l'id : " + id);
    }
}
