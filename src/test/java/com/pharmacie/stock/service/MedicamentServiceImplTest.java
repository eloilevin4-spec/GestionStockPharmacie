package com.pharmacie.stock.service;

import com.pharmacie.stock.exception.MedicamentNotFoundException;
import com.pharmacie.stock.exception.StockInsuffisantException;
import com.pharmacie.stock.model.Medicament;
import com.pharmacie.stock.repository.MedicamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicamentServiceImplTest {

    @Mock
    private MedicamentRepository repository;

    @InjectMocks
    private MedicamentServiceImpl service;

    private Medicament doliprane;

    @BeforeEach
    void setUp() {
        doliprane = new Medicament("Doliprane 1000mg", "Antalgique", "Sanofi",
                50, 20, 2.50, LocalDate.now().plusMonths(6));
        doliprane.setId(1L);
    }

    @Test
    void ajouterMedicament_doitEnregistrerEtRetournerLeMedicament() {
        when(repository.save(any(Medicament.class))).thenReturn(doliprane);

        Medicament resultat = service.ajouterMedicament(doliprane);

        assertThat(resultat.getNom()).isEqualTo("Doliprane 1000mg");
        verify(repository, times(1)).save(doliprane);
    }

    @Test
    void rechercherParId_medicamentInexistant_doitLeverUneException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.rechercherParId(99L))
                .isInstanceOf(MedicamentNotFoundException.class);
    }

    @Test
    void entreeStock_doitAugmenterLaQuantite() {
        when(repository.findById(1L)).thenReturn(Optional.of(doliprane));
        when(repository.save(any(Medicament.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Medicament resultat = service.entreeStock(1L, 30);

        assertThat(resultat.getQuantiteStock()).isEqualTo(80);
    }

    @Test
    void sortieStock_stockSuffisant_doitDiminuerLaQuantite() {
        when(repository.findById(1L)).thenReturn(Optional.of(doliprane));
        when(repository.save(any(Medicament.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Medicament resultat = service.sortieStock(1L, 20);

        assertThat(resultat.getQuantiteStock()).isEqualTo(30);
    }

    @Test
    void sortieStock_stockInsuffisant_doitLeverUneException() {
        when(repository.findById(1L)).thenReturn(Optional.of(doliprane));

        assertThatThrownBy(() -> service.sortieStock(1L, 1000))
                .isInstanceOf(StockInsuffisantException.class)
                .hasMessageContaining("Stock insuffisant");
    }

    @Test
    void alertesStockFaible_doitRetournerLesMedicamentsSousLeSeuil() {
        Medicament stockFaible = new Medicament("Amoxicilline", "Antibiotique", "Biogaran",
                5, 10, 4.20, LocalDate.now().plusMonths(3));
        Medicament stockOk = new Medicament("Vitamine C", "Complément", "UPSA",
                100, 10, 3.00, LocalDate.now().plusMonths(12));

        when(repository.findAll()).thenReturn(List.of(stockFaible, stockOk));

        List<Medicament> resultat = service.alertesStockFaible();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getNom()).isEqualTo("Amoxicilline");
    }

    @Test
    void alertesExpirationProche_doitAppelerLeRepositoryAvecLaBonneDateLimite() {
        when(repository.findByDateExpirationBefore(any(LocalDate.class))).thenReturn(List.of(doliprane));

        List<Medicament> resultat = service.alertesExpirationProche(30);

        assertThat(resultat).isNotEmpty();
        verify(repository).findByDateExpirationBefore(any(LocalDate.class));
    }
}
