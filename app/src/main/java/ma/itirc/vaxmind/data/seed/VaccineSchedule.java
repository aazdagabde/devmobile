package ma.itirc.vaxmind.data.seed;

import java.util.ArrayList;
import java.util.List;

import ma.itirc.vaxmind.data.entity.Vaccine;

/** Calendrier national fixe “Âge   /   Vaccins” */
public final class VaccineSchedule {

    private static final String[][] BASE = {
            {"Naissance",                  "BCG + Anti-Polio VPo + Anti-HVB1"},
            {"6ème semaine (1 mois ½)",    "DTC1 + Anti-Polio1 + Anti-HVB2 + Anti-Hib1"},
            {"10ème semaine (2 mois ½)",   "DTC2 + Anti-Polio2 + Anti-Hib2"},
            {"14ème semaine (3 mois ½)",   "DTC3 + Anti-Polio3 + Anti-Hib3"},
            {"9ème mois",                  "Anti-Rougeole + Ar* + HVB3"},
            {"18ème mois (1 an ½)",        "Rappel DTC-Polio"},
            {"6 ans",                      "Anti-Rougeole + Anti-Rubéole"}
    };

    private VaccineSchedule() {}   // utilitaire

    /** Retourne la liste pré-remplie pour l'utilisateur donné */
    public static List<Vaccine> generate(long userId) {
        List<Vaccine> list = new ArrayList<>();
        for (String[] row : BASE) {
            Vaccine v   = new Vaccine();
            v.userId    = userId;
            v.plannedDate = row[0];   // ici on stocke l'âge comme libellé simple
            v.name      = row[1];
            v.doneDate  = null;
            v.notes     = null;
            list.add(v);
        }
        return list;
    }
}
