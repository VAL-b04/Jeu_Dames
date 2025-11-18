public class Plateau
{
    private Pion[][] cases;
    private int taille;

    public Plateau(int taille)
    {
        this.taille = taille;
        this.cases = new Pion[taille][taille];
        initialiser();
    }

    private void initialiser()
    {
        for (int i = 0; i < taille; i++)
        {
            for (int j = 0; j < taille; j++)
            {
                if (i < 3 && (i + j) % 2 != 0)
                {
                    cases[i][j] = new Pion(true, false);
                }
                else if (i > 4 && (i + j) % 2 != 0)
                {
                    cases[i][j] = new Pion(false, false);
                }
                else
                {
                    cases[i][j] = null;
                }
            }
        }
    }

    public Pion obtenirPion(Position position)
    {
        if (position.estValide(taille))
        {
            return cases[position.getLigne()][position.getColonne()];
        }
        return null;
    }

    public void deplacerPion(Position depart, Position arrivee)
    {
        Pion pion = obtenirPion(depart);
        cases[arrivee.getLigne()][arrivee.getColonne()] = pion;
        cases[depart.getLigne()][depart.getColonne()] = null;
    }

    public void supprimerPion(Position position)
    {
        if (position.estValide(taille))
        {
            cases[position.getLigne()][position.getColonne()] = null;
        }
    }

    public boolean caseEstVide(Position position)
    {
        return obtenirPion(position) == null;
    }

    public boolean estCaseNoire(Position position)
    {
        return (position.getLigne() + position.getColonne()) % 2 != 0;
    }

    public int getTaille()
    {
        return taille;
    }

    public Pion[][] getCases()
    {
        return cases;
    }
}