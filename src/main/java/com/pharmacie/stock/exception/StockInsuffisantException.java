package com.pharmacie.stock.exception;

public class StockInsuffisantException extends RuntimeException {
    public StockInsuffisantException(String nomMedicament, int quantiteDemandee, int quantiteDisponible) {
        super(String.format(
            "Stock insuffisant pour '%s' : demandé=%d, disponible=%d",
            nomMedicament, quantiteDemandee, quantiteDisponible));
    }
}
