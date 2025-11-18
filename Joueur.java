public class Joueur
{
    private String nom;
    private boolean estNoir;
    private int nombrePions;

    public static final int NOMBRE_PIONS_DEPART = 12;

    public Joueur(String nom, boolean estNoir)
    {
        this.nom = nom;
        this.estNoir = estNoir;
        this.nombrePions = NOMBRE_PIONS_DEPART;
    }

    // Getters
    public String getNom()
    {
        return nom;
    }

    public boolean estNoir()
    {
        return estNoir;
    }

    public int getNombrePions()
    {
        return nombrePions;
    }

    // Méthodes de gestion
    public void perdrePion()
    {
        if (nombrePions > 0)
        {
            nombrePions--;
        }
    }

    public boolean aPerdu()
    {
        return nombrePions <= 0;
    }

    @Override
    public String toString()
    {
        return nom + " (" + (estNoir ? "Noirs" : "Jaunes") + ") - Pions restants: " + nombrePions;
    }
}