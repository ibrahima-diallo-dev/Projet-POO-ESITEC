package sn.esitec.poo.cahiertexte;

import org.junit.jupiter.api.Test;
import sn.esitec.poo.cahiertexte.model.Role;
import sn.esitec.poo.cahiertexte.model.Seance;
import sn.esitec.poo.cahiertexte.model.StatutSeance;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    @Test
    void roleFromString_shouldReturnExpectedRole() {
        assertEquals(Role.ENSEIGNANT, Role.fromString("ENSEIGNANT"));
        assertEquals(Role.CHEF_DEPARTEMENT, Role.fromString("CHEF_DEPARTEMENT"));
    }

    @Test
    void statutFromString_shouldReturnExpectedStatut() {
        assertEquals(StatutSeance.EN_ATTENTE, StatutSeance.fromString("EN_ATTENTE"));
        assertEquals(StatutSeance.REJETEE, StatutSeance.fromString("REJETEE"));
    }

    @Test
    void seanceEstModifiable_shouldBeTrueOnlyWhenEnAttente() {
        Seance seance = new Seance(
                1,
                LocalDate.of(2026, 3, 6),
                LocalTime.of(10, 0),
                120,
                "Contenu séance",
                "Observations",
                StatutSeance.EN_ATTENTE,
                null,
                1,
                1
        );

        assertTrue(seance.estModifiable());

        seance.setStatut(StatutSeance.VALIDEE);
        assertFalse(seance.estModifiable());
    }
}
