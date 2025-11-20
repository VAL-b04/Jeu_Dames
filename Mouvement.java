
public class Mouvement
{
    private Position depart;
    private Position arrivee;

    public Mouvement(Position depart, Position arrivee)
    {
        this.depart = depart;
        this.arrivee = arrivee;
    }

    public Position getDepart()
    {
        return depart;
    }

    public Position getArrivee()
    {
        return arrivee;
    }

    public boolean estValide(Plateau plateau, Pion pion)
    {
        if (!arrivee.estValide(plateau.getTaille()))
        {
            return false;
        }

        if (!plateau.caseEstVide(arrivee))
        {
            return false;
        }

        if (!plateau.estCaseNoire(arrivee))
        {
            return false;
        }

        int diffLigne = depart.distanceLigne(arrivee);
        int diffColonne = depart.distanceColonne(arrivee);
        
        // Vérifier que c'est un mouvement diagonal
        if (Math.abs(diffLigne) != diffColonne)
        {
            return false;
        }

        // Si c'est une dame, elle peut traverser tout le plateau
        if (pion.estDame())
        {
            return estMouvementDameValide(plateau, pion, diffLigne, diffColonne);
        }

        // Pour les pions normaux
        if (Math.abs(diffLigne) == 1 && diffColonne == 1)
        {
            return estMouvementSimpleValide(pion, diffLigne);
        }

        if (Math.abs(diffLigne) == 2 && diffColonne == 2)
        {
            return estCaptureValide(plateau, pion, diffLigne);
        }

        return false;
    }

    public boolean estValideAvecCaptureObligatoire(Plateau plateau, Pion pion, Joueur joueur)
    {
        if (!estValide(plateau, pion))
        {
            return false;
        }

        if (plateau.joueurPeutCapturer(joueur))
        {
            return estCapture();
        }

        return true;
    }

    private boolean estMouvementSimpleValide(Pion pion, int directionLigne)
    {
        return pion.peutAvancerVers(directionLigne);
    }

    private boolean estCaptureValide(Plateau plateau, Pion pion, int directionLigne)
    {
        if (!pion.peutAvancerVers(directionLigne))
        {
            return false;
        }

        Position caseMilieu = obtenirCaseCapturee();
        Pion pionMilieu = plateau.obtenirPion(caseMilieu);

        if (pionMilieu == null)
        {
            return false;
        }

        return pionMilieu.estNoir() != pion.estNoir();
    }
    
    private boolean estMouvementDameValide(Plateau plateau, Pion pion, int diffLigne, int diffColonne)
    {
        // Vérifier que le chemin est libre
        int directionLigne = (diffLigne > 0) ? 1 : -1;
        int directionColonne = (depart.getColonne() < arrivee.getColonne()) ? 1 : -1;
        
        int distance = Math.abs(diffLigne);
        int pionsTrouves = 0;
        Position positionPionTrouve = null;
        
        // Parcourir la diagonale
        for (int i = 1; i < distance; i++)
        {
            int ligneCourante = depart.getLigne() + (i * directionLigne);
            int colonneCourante = depart.getColonne() + (i * directionColonne);
            Position posCourante = new Position(ligneCourante, colonneCourante);
            
            Pion pionSurChemin = plateau.obtenirPion(posCourante);
            
            if (pionSurChemin != null)
            {
                pionsTrouves++;
                positionPionTrouve = posCourante;
                
                // Plus d'un pion sur le chemin = invalide
                if (pionsTrouves > 1)
                {
                    return false;
                }
            }
        }
        
        // Aucun pion = mouvement simple valide
        if (pionsTrouves == 0)
        {
            return true;
        }
        
        // Un pion = capture possible si c'est un pion adverse
        if (pionsTrouves == 1)
        {
            Pion pionCapture = plateau.obtenirPion(positionPionTrouve);
            return pionCapture.estNoir() != pion.estNoir();
        }
        
        return false;
    }

    public boolean estCapture()
    {
        int diffLigne = Math.abs(depart.distanceLigne(arrivee));
        return diffLigne >= 2;
    }

    public Position obtenirCaseCapturee()
    {
        int distance = Math.abs(depart.distanceLigne(arrivee));
        if (distance == 2)
        {
            return depart.positionMilieu(arrivee);
        }
        
        int directionLigne = (depart.getLigne() < arrivee.getLigne()) ? 1 : -1;
        int directionColonne = (depart.getColonne() < arrivee.getColonne()) ? 1 : -1;
        
        return new Position(arrivee.getLigne() - directionLigne, arrivee.getColonne() - directionColonne);
    }
}