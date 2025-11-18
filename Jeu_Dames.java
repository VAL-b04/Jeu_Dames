public class Jeu_Dames
{
    private Plateau plateau;
    private Joueur joueur1;
    private Joueur joueur2;
    private Joueur joueurActuel;
    private Gestionnaire_Interface gestionnaireInterface;
    private static final int TAILLE_CASE = 100;
    private static final int TAILLE_PLATEAU = 8;

    public Jeu_Dames()
    {
        plateau = new Plateau(TAILLE_PLATEAU);
        initialiserJoueurs();
        gestionnaireInterface = new Gestionnaire_Interface(TAILLE_CASE, TAILLE_PLATEAU);
    }

    public void initialiserJoueurs()
    {
        joueur1 = new Joueur("Joueur 1", true);
        joueur2 = new Joueur("Joueur 2", false);
        joueurActuel = joueur1;
    }

    public void changeTour()
    {
        if (joueurActuel == joueur1)
        {
            joueurActuel = joueur2;
        }
        else
        {
            joueurActuel = joueur1;
        }
        System.out.println("Tour de " + joueurActuel);
    }

    public void demarrer()
    {
        gestionnaireInterface.initialiserFenetre();
        gestionnaireInterface.afficher(plateau, joueurActuel);

        while (true)
        {
            traiterTour();
            
            if (verifierFinPartie())
            {
                break;
            }
        }
    }

    private void traiterTour()
    {
        Position position = gestionnaireInterface.obtenirClicSouris();
        
        if (position != null)
        {
            if (!gestionnaireInterface.aPionSelectionne())
            {
                selectionnerPion(position);
            }
            else
            {
                deplacerPionSelectionne(position);
            }
        }
    }

    private void selectionnerPion(Position position)
    {
        Pion pion = plateau.obtenirPion(position);
        
        if (pion != null && pion.appartientAuJoueur(joueurActuel))
        {
            gestionnaireInterface.selectionnerCase(position);
            gestionnaireInterface.afficher(plateau, joueurActuel);
            System.out.println("Pion sélectionné: " + position);
        }
    }

    private void deplacerPionSelectionne(Position destination)
    {
        Position depart = gestionnaireInterface.obtenirSelection();
        Pion pion = plateau.obtenirPion(depart);
        
        Mouvement mouvement = new Mouvement(depart, destination);
        
        if (mouvement.estValide(plateau, pion))
        {
            executerMouvement(mouvement);
            gestionnaireInterface.deselectionner();
            changeTour();
            gestionnaireInterface.afficher(plateau, joueurActuel);
        }
        else
        {
            gestionnaireInterface.deselectionner();
            gestionnaireInterface.afficher(plateau, joueurActuel);
            System.out.println("Mouvement invalide");
        }
    }

    private void executerMouvement(Mouvement mouvement)
    {
        Position depart = mouvement.getDepart();
        Position arrivee = mouvement.getArrivee();
        
        Pion pion = plateau.obtenirPion(depart);
        
        if (mouvement.estCapture())
        {
            // Trouver et supprimer le pion capturé
            supprimerPionCapture(depart, arrivee, pion);
            
            Joueur adversaire = (joueurActuel == joueur1) ? joueur2 : joueur1;
            adversaire.perdrePion();
        }
        
        plateau.deplacerPion(depart, arrivee);
        
        if (pion.doitEtrePromu(arrivee.getLigne(), plateau.getTaille()))
        {
            pion.promouvoir();
        }
    }
    
    private void supprimerPionCapture(Position depart, Position arrivee, Pion pion)
    {
        int directionLigne = (depart.getLigne() < arrivee.getLigne()) ? 1 : -1;
        int directionColonne = (depart.getColonne() < arrivee.getColonne()) ? 1 : -1;
        int distance = Math.abs(depart.getLigne() - arrivee.getLigne());
        
        // Parcourir le chemin pour trouver le pion à capturer
        for (int i = 1; i < distance; i++)
        {
            int ligneCourante = depart.getLigne() + (i * directionLigne);
            int colonneCourante = depart.getColonne() + (i * directionColonne);
            Position posCourante = new Position(ligneCourante, colonneCourante);
            
            Pion pionSurChemin = plateau.obtenirPion(posCourante);
            
            if (pionSurChemin != null && pionSurChemin.estNoir() != pion.estNoir())
            {
                plateau.supprimerPion(posCourante);
                return;
            }
        }
    }

    private boolean verifierFinPartie()
    {
        if (joueur1.aPerdu())
        {
            System.out.println(joueur2.getNom() + " a gagné !");
            return true;
        }
        else if (joueur2.aPerdu())
        {
            System.out.println(joueur1.getNom() + " a gagné !");
            return true;
        }
        return false;
    }

    public static void main(String[] args)
    {
        Jeu_Dames jeu = new Jeu_Dames();
        jeu.demarrer();
    }
}