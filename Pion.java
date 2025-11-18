public class Pion
{
    private boolean estNoir;
    private boolean estDame;

    public Pion(boolean estNoir, boolean estDame)
    {
        this.estNoir = estNoir;
        this.estDame = estDame;
    }

    public boolean estNoir()
    {
        return estNoir;
    }

    public boolean estDame()
    {
        return estDame;
    }

    public void promouvoir()
    {
        this.estDame = true;
    }

    public boolean appartientAuJoueur(Joueur joueur)
    {
        return this.estNoir == joueur.estNoir();
    }

    public boolean peutAvancerVers(int directionLigne)
    {
        if (estDame)
        {
            return true;
        }
        
        if (estNoir && directionLigne > 0)
        {
            return true;
        }
        
        if (!estNoir && directionLigne < 0)
        {
            return true;
        }
        
        return false;
    }

    public boolean doitEtrePromu(int ligne, int taillePlateau)
    {
        if (estDame)
        {
            return false;
        }
        
        if (estNoir && ligne == taillePlateau - 1)
        {
            return true;
        }
        
        if (!estNoir && ligne == 0)
        {
            return true;
        }
        
        return false;
    }
}