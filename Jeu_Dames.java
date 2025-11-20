import java.util.List;

public class Jeu_Dames
{
    private Plateau plateau;
    private Joueur joueur1;
    private Joueur joueur2;
    private Joueur joueurActuel;
    private Gestionnaire_Interface gestionnaireInterface;
    private CalculateurRafles calculateurRafles;
    private List<Rafle> raflesMaximalesDisponibles;
    
    private static final int TAILLE_CASE = 100;
    private static final int TAILLE_PLATEAU = 8;

    public Jeu_Dames()
    {
        plateau = new Plateau(TAILLE_PLATEAU);
        initialiserJoueurs();
        gestionnaireInterface = new Gestionnaire_Interface(TAILLE_CASE, TAILLE_PLATEAU);
        calculateurRafles = new CalculateurRafles(plateau);
        raflesMaximalesDisponibles = null;
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
        
        // Recalculer les rafles disponibles pour le nouveau joueur
        raflesMaximalesDisponibles = calculerRaflesMaximales();
        
        System.out.println("\n========================================");
        System.out.println("Tour de " + joueurActuel);
        if (!raflesMaximalesDisponibles.isEmpty())
        {
            System.out.println("Rafles obligatoires disponibles : " + raflesMaximalesDisponibles.size());
            System.out.println("Nombre de captures : " + raflesMaximalesDisponibles.get(0).getNombreCaptures());
        }
        System.out.println("========================================\n");
    }

    public void demarrer()
    {
        gestionnaireInterface.initialiserFenetre();
        raflesMaximalesDisponibles = calculerRaflesMaximales();
        gestionnaireInterface.afficher(plateau, joueurActuel, raflesMaximalesDisponibles);

        while (true)
        {
            traiterTour();
            
            if (verifierFinPartie())
            {
                break;
            }
        }
    }

    private List<Rafle> calculerRaflesMaximales()
    {
        List<Rafle> toutesLesRafles = calculateurRafles.trouverToutesLesRafles(joueurActuel);
        return calculateurRafles.trouverRaflesMaximales(toutesLesRafles);
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
                traiterClicDestination(position);
            }
        }
    }

    private void selectionnerPion(Position position)
    {
        Pion pion = plateau.obtenirPion(position);
        
        if (pion == null || !pion.appartientAuJoueur(joueurActuel))
        {
            return;
        }
        
        // Si des rafles maximales existent, vérifier que ce pion peut les effectuer
        if (!raflesMaximalesDisponibles.isEmpty())
        {
            boolean pionPeutFaireRafleMax = false;
            for (Rafle r : raflesMaximalesDisponibles)
            {
                if (r.getDepart().equals(position))
                {
                    pionPeutFaireRafleMax = true;
                    break;
                }
            }
            
            if (!pionPeutFaireRafleMax)
            {
                System.out.println("Ce pion ne peut pas effectuer la rafle maximale !");
                return;
            }
        }
        
        gestionnaireInterface.selectionnerCase(position);
        gestionnaireInterface.afficher(plateau, joueurActuel, raflesMaximalesDisponibles);
        System.out.println("Pion sélectionné: " + position);
    }

    private void traiterClicDestination(Position destination)
    {
        Rafle rafleEnCours = gestionnaireInterface.getRafleEnCours();
        Position positionActuelle = (rafleEnCours == null) ? gestionnaireInterface.obtenirSelection() : rafleEnCours.getArrivee();
        
        // Si on est en cours de rafle
        if (rafleEnCours != null)
        {
            continuerRafle(destination);
        }
        else if (!raflesMaximalesDisponibles.isEmpty())
        {
            // Démarrer une nouvelle rafle
            demarrerRafle(destination);
        }
        else
        {
            // Mouvement simple (pas de captures disponibles)
            effectuerMouvementSimple(destination);
        }
    }

    private void demarrerRafle(Position destination)
    {
        Position depart = gestionnaireInterface.obtenirSelection();
        
        // Trouver quelle rafle commence par ce mouvement
        Rafle rafleChoisie = null;
        for (Rafle r : raflesMaximalesDisponibles)
        {
            if (r.getDepart().equals(depart) && r.getChemin().size() > 1 && r.getChemin().get(1).equals(destination))
            {
                rafleChoisie = r;
                break;
            }
        }
        
        if (rafleChoisie == null)
        {
            System.out.println("Ce mouvement ne fait pas partie d'une rafle maximale !");
            gestionnaireInterface.deselectionner();
            gestionnaireInterface.afficher(plateau, joueurActuel, raflesMaximalesDisponibles);
            return;
        }
        
        // Démarrer la rafle
        gestionnaireInterface.demarrerRafle(rafleChoisie);
        executerEtapeRafle(depart, destination, rafleChoisie.getPionsCaptures().get(0));
        
        // Si la rafle est terminée
        if (rafleChoisie.getChemin().size() == 2)
        {
            terminerRafle(rafleChoisie);
        }
        else
        {
            // Continuer la rafle
            gestionnaireInterface.avancerEtapeRafle();
            gestionnaireInterface.selectionnerCase(destination);
            
            // Filtrer les rafles possibles pour la suite
            List<Rafle> raflesSuite = filtrerRaflesContinuation(rafleChoisie, destination);
            gestionnaireInterface.afficher(plateau, joueurActuel, raflesSuite);
            
            System.out.println("Rafle en cours - Continuez la capture !");
        }
    }

    private void continuerRafle(Position destination)
    {
        Rafle rafleEnCours = gestionnaireInterface.getRafleEnCours();
        int etapeActuelle = gestionnaireInterface.getEtapeRafle();
        Position positionActuelle = rafleEnCours.getChemin().get(etapeActuelle);
        
        // Vérifier que c'est bien la prochaine étape de la rafle
        if (etapeActuelle + 1 >= rafleEnCours.getChemin().size())
        {
            System.out.println("Erreur : rafle terminée");
            return;
        }
        
        Position prochainePosAttendue = rafleEnCours.getChemin().get(etapeActuelle + 1);
        
        // Trouver toutes les rafles compatibles avec le choix
        List<Rafle> raflesCompatibles = filtrerRaflesContinuation(rafleEnCours, destination);
        
        if (raflesCompatibles.isEmpty())
        {
            System.out.println("Ce mouvement n'est pas valide dans cette rafle !");
            return;
        }
        
        // Choisir une rafle compatible (la première trouvée)
        Rafle rafleChoisie = raflesCompatibles.get(0);
        
        // Exécuter cette étape
        Position pionCapture = rafleChoisie.getPionsCaptures().get(etapeActuelle);
        executerEtapeRafle(positionActuelle, destination, pionCapture);
        
        // Vérifier si la rafle est terminée
        if (etapeActuelle + 2 >= rafleChoisie.getChemin().size())
        {
            terminerRafle(rafleChoisie);
        }
        else
        {
            // Continuer
            gestionnaireInterface.avancerEtapeRafle();
            gestionnaireInterface.selectionnerCase(destination);
            
            List<Rafle> raflesSuite = filtrerRaflesContinuation(rafleChoisie, destination);
            gestionnaireInterface.afficher(plateau, joueurActuel, raflesSuite);
            
            System.out.println("Rafle en cours - Continuez !");
        }
    }

    private List<Rafle> filtrerRaflesContinuation(Rafle rafleActuelle, Position positionActuelle)
    {
        List<Rafle> raflesSuite = new java.util.ArrayList<>();
        int etape = gestionnaireInterface.getEtapeRafle() + 1;
        
        for (Rafle r : raflesMaximalesDisponibles)
        {
            // Vérifier que cette rafle est compatible avec le chemin parcouru
            if (r.getChemin().size() > etape && cheminCompatible(r, rafleActuelle, etape) && r.getChemin().get(etape).equals(positionActuelle))
            {
                raflesSuite.add(r);
            }
        }
        
        return raflesSuite;
    }

    private boolean cheminCompatible(Rafle r1, Rafle r2, int longueur)
    {
        for (int i = 0; i < Math.min(longueur, Math.min(r1.getChemin().size(), r2.getChemin().size())); i++)
        {
            if (!r1.getChemin().get(i).equals(r2.getChemin().get(i)))
            {
                return false;
            }
        }
        return true;
    }

    private void executerEtapeRafle(Position depart, Position arrivee, Position pionCapture)
    {
        plateau.deplacerPion(depart, arrivee);
        plateau.supprimerPion(pionCapture);
        
        Joueur adversaire = (joueurActuel == joueur1) ? joueur2 : joueur1;
        adversaire.perdrePion();
        
        System.out.println("Capture: " + depart + " -> " + arrivee + " (pion capturé: " + pionCapture + ")");
    }

    private void terminerRafle(Rafle rafle)
    {
        Position arrivee = rafle.getArrivee();
        Pion pion = plateau.obtenirPion(arrivee);
        
        // Promotion en dame si nécessaire
        if (pion.doitEtrePromu(arrivee.getLigne(), plateau.getTaille()))
        {
            pion.promouvoir();
            System.out.println("DAME PROMUE !");
        }
        
        System.out.println("Rafle terminée ! " + rafle.getNombreCaptures() + " pions capturés.");
        
        gestionnaireInterface.deselectionner();
        changeTour();
        gestionnaireInterface.afficher(plateau, joueurActuel, raflesMaximalesDisponibles);
    }

    private void effectuerMouvementSimple(Position destination)
    {
        Position depart = gestionnaireInterface.obtenirSelection();
        Pion pion = plateau.obtenirPion(depart);
        
        Mouvement mouvement = new Mouvement(depart, destination);
        
        if (!mouvement.estValide(plateau, pion))
        {
            System.out.println("Mouvement invalide !");
            gestionnaireInterface.deselectionner();
            gestionnaireInterface.afficher(plateau, joueurActuel, raflesMaximalesDisponibles);
            return;
        }
        
        plateau.deplacerPion(depart, destination);
        
        if (pion.doitEtrePromu(destination.getLigne(), plateau.getTaille()))
        {
            pion.promouvoir();
            System.out.println("DAME PROMUE !");
        }
        
        System.out.println("Mouvement: " + depart + " -> " + destination);
        
        gestionnaireInterface.deselectionner();
        changeTour();
        gestionnaireInterface.afficher(plateau, joueurActuel, raflesMaximalesDisponibles);
    }

    private boolean verifierFinPartie()
    {
        if (joueur1.aPerdu())
        {
            System.out.println("\n" + joueur2.getNom() + " a gagné !");
            return true;
        }
        else if (joueur2.aPerdu())
        {
            System.out.println("\n" + joueur1.getNom() + " a gagné !");
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