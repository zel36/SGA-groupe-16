package sga.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Représente un créneau horaire pour un jour de la semaine.
 */
public class CreneauHoraire {
    private DayOfWeek jour; // java.time.DayOfWeek
    private LocalTime debut;
    private LocalTime fin;

    public CreneauHoraire() {}

    public CreneauHoraire(DayOfWeek jour, LocalTime debut, LocalTime fin) {
        this.jour = jour;
        this.debut = debut;
        this.fin = fin;
    }

    public DayOfWeek getJour() {
        return jour;
    }

    public void setJour(DayOfWeek jour) {
        this.jour = jour;
    }

    public LocalTime getDebut() {
        return debut;
    }

    public void setDebut(LocalTime debut) {
        this.debut = debut;
    }

    public LocalTime getFin() {
        return fin;
    }

    public void setFin(LocalTime fin) {
        this.fin = fin;
    }

    @Override
    public String toString() {
        return jour + " " + debut + "-" + fin;
    }
}
