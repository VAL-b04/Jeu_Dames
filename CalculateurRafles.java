import java.util.ArrayList;
import java.util.List;

public class CalculateurRafles
{
    private Plateau plateau;
    private int taillePlateau;
    
    public CalculateurRafles(Plateau plateau)
    {
        this.plateau = plateau;
        this.taillePlateau = plateau.getTaille();
    }

    public List<Rafle> trouverToutesLesRafles(Joueur joueur)
    {
        List<Rafle> toutesLesRafles = new ArrayList<>();
        
        for (int i = 0; i < taillePlateau; i++)
        {
            for (int j = 0; j < taillePlateau; j++)
            {
                Position pos = new Position(i, j);
                Pion pion = plateau.obtenirPion(pos);
                
                if (pion != null && pion.appartientAuJoueur(joueur))
                {
                    List<Rafle> raflesPion = calculerRaflesDepuisPosition(pos, pion);
                    toutesLesRafles.addAll(raflesPion);
                }
            }
        }
        
        return toutesLesRafles;
    }

    public List<Rafle> calculerRaflesDepuisPosition(Position depart, Pion pion)
    {
        List<Rafle> rafles = new ArrayList<>();
        Rafle rafleInitiale = new Rafle(depart);
        
        calculerRaflesRecursif(depart, pion, rafleInitiale, rafles);
        
        // Ne garder que les rafles qui capturent au moins un pion
        List<Rafle> raflesValides = new ArrayList<>();
        for (Rafle r : rafles)
        {
            if (r.getNombreCaptures() > 0)
            {
                raflesValides.add(r);
            }
        }
        
        return raflesValides;
    }

    private void calculerRaflesRecursif(Position positionActuelle, Pion pion, Rafle rafleEnCours, List<Rafle> resultat)
    {
        boolean capturesPossibles = false;
        
        // Tester toutes les directions
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        
        for (int[] dir : directions)
        {
            if (pion.estDame())
            {
                // Pour les dames, tester toutes les distances
                capturesPossibles |= testerCapturesDame(positionActuelle, pion, dir, rafleEnCours, resultat);
            }
            else
            {
                // Pour les pions normaux, distance fixe de 2
                capturesPossibles |= testerCapturePion(positionActuelle, pion, dir, rafleEnCours, resultat);
            }
        }
        
        // Si aucune capture n'est possible, cette rafle est terminée
        if (!capturesPossibles && rafleEnCours.getNombreCaptures() > 0)
        {
            resultat.add(new Rafle(rafleEnCours));
        }
    }
    
    private boolean testerCapturePion(Position pos, Pion pion, int[] dir, Rafle rafleEnCours, List<Rafle> resultat)
    {
        int ligneMilieu = pos.getLigne() + dir[0];
        int colonneMilieu = pos.getColonne() + dir[1];
        int ligneArrivee = pos.getLigne() + dir[0] * 2;
        int colonneArrivee = pos.getColonne() + dir[1] * 2;
        
        Position posMilieu = new Position(ligneMilieu, colonneMilieu);
        Position posArrivee = new Position(ligneArrivee, colonneArrivee);
        
        // Vérifier si la capture est valide
        if (!posArrivee.estValide(taillePlateau)) return false;
        if (!plateau.estCaseNoire(posArrivee)) return false;
        if (!plateau.caseEstVide(posArrivee)) return false;
        if (rafleEnCours.contientPosition(posArrivee)) return false;
        
        Pion pionMilieu = plateau.obtenirPion(posMilieu);
        if (pionMilieu == null) return false;
        if (pionMilieu.estNoir() == pion.estNoir()) return false;
        if (rafleEnCours.pionDejaCapture(posMilieu)) return false;
        
        // Vérifier la direction pour les pions normaux
        if (!pion.estDame() && !pion.peutAvancerVers(dir[0])) return false;
        
        // Capture valide ! Continuer la recherche
        Rafle nouvelleRafle = new Rafle(rafleEnCours);
        nouvelleRafle.ajouterCapture(posArrivee, posMilieu);
        calculerRaflesRecursif(posArrivee, pion, nouvelleRafle, resultat);
        
        return true;
    }
    
    private boolean testerCapturesDame(Position pos, Pion pion, int[] dir, Rafle rafleEnCours, List<Rafle> resultat)
    {
        boolean captureTrouvee = false;
        Position pionCapture = null;
        int distancePionCapture = 0;
        
        // Parcourir la diagonale pour trouver un pion à capturer
        for (int dist = 1; dist < taillePlateau; dist++)
        {
            int ligne = pos.getLigne() + dir[0] * dist;
            int colonne = pos.getColonne() + dir[1] * dist;
            Position posCourante = new Position(ligne, colonne);
            
            if (!posCourante.estValide(taillePlateau)) break;
            if (rafleEnCours.contientPosition(posCourante)) break;
            
            Pion pionSurChemin = plateau.obtenirPion(posCourante);
            
            if (pionSurChemin != null)
            {
                // Si c'est un pion adverse et pas déjà capturé
                if (pionSurChemin.estNoir() != pion.estNoir() && 
                    !rafleEnCours.pionDejaCapture(posCourante))
                {
                    pionCapture = posCourante;
                    distancePionCapture = dist;
                    break;
                }
                else
                {
                    // Pion allié ou déjà capturé : on ne peut pas aller plus loin
                    break;
                }
            }
        }
        
        // Si un pion à capturer a été trouvé, tester toutes les cases d'arrivée possibles
        if (pionCapture != null)
        {
            for (int dist = distancePionCapture + 1; dist < taillePlateau; dist++)
            {
                int ligne = pos.getLigne() + dir[0] * dist;
                int colonne = pos.getColonne() + dir[1] * dist;
                Position posArrivee = new Position(ligne, colonne);
                
                if (!posArrivee.estValide(taillePlateau)) break;
                if (!plateau.estCaseNoire(posArrivee)) continue;
                if (!plateau.caseEstVide(posArrivee)) break;
                if (rafleEnCours.contientPosition(posArrivee)) break;
                
                // Arrivée valide après capture
                Rafle nouvelleRafle = new Rafle(rafleEnCours);
                nouvelleRafle.ajouterCapture(posArrivee, pionCapture);
                calculerRaflesRecursif(posArrivee, pion, nouvelleRafle, resultat);
                captureTrouvee = true;
            }
        }
        
        return captureTrouvee;
    }

    public List<Rafle> trouverRaflesMaximales(List<Rafle> rafles)
    {
        if (rafles.isEmpty()) return new ArrayList<>();
        
        int maxCaptures = 0;
        for (Rafle r : rafles)
        {
            if (r.getNombreCaptures() > maxCaptures)
            {
                maxCaptures = r.getNombreCaptures();
            }
        }
        
        List<Rafle> raflesMax = new ArrayList<>();
        for (Rafle r : rafles)
        {
            if (r.getNombreCaptures() == maxCaptures)
            {
                raflesMax.add(r);
            }
        }
        
        return raflesMax;
    }
}