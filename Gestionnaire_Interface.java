import java.awt.Color;
import java.util.List;

public class Gestionnaire_Interface
{
    private int tailleCase;
    private int taillePlateau;
    private Position caseSelectionnee;
    private boolean pionSelectionne;
    private Rafle rafleEnCours;
    private int etapeRafle;
    private boolean abandonneClique;

    public Gestionnaire_Interface(int tailleCase, int taillePlateau)
    {
        this.tailleCase = tailleCase;
        this.taillePlateau = taillePlateau;
        this.caseSelectionnee = null;
        this.pionSelectionne = false;
        this.rafleEnCours = null;
        this.etapeRafle = 0;
        this.abandonneClique = false;
    }

    public void initialiserFenetre()
    {
        int largeur = taillePlateau * tailleCase;
        int hauteur = taillePlateau * tailleCase + 50;

        StdDraw.setCanvasSize(largeur, hauteur);
        StdDraw.setXscale(0, largeur);
        StdDraw.setYscale(0, hauteur);
        StdDraw.enableDoubleBuffering();
    }

    public void afficher(Plateau plateau, Joueur joueurActuel, List<Rafle> raflesDisponibles)
    {
        StdDraw.clear(StdDraw.WHITE);
        dessinerCases(plateau, raflesDisponibles);
        dessinerCheminRafle();
        dessinerCasesDisponibles(plateau, joueurActuel, raflesDisponibles);
        dessinerPions(plateau);
        afficherInfosJoueur(joueurActuel, raflesDisponibles);
        afficherBoutonAbandon();
        StdDraw.show();
    }

    private void dessinerCases(Plateau plateau, List<Rafle> raflesDisponibles)
    {
        for (int i = 0; i < taillePlateau; i++)
        {
            for (int j = 0; j < taillePlateau; j++)
            {
                dessinerCase(i, j, plateau, raflesDisponibles);
            }
        }
    }

    private void dessinerCheminRafle()
    {
        if (rafleEnCours == null || rafleEnCours.getChemin().size() <= 1) return;
        
        List<Position> chemin = rafleEnCours.getChemin();
        StdDraw.setPenColor(new Color(255, 100, 0, 150));
        StdDraw.setPenRadius(0.01);
        
        for (int i = 0; i < chemin.size() - 1; i++)
        {
            Position p1 = chemin.get(i);
            Position p2 = chemin.get(i + 1);
            
            double x1 = p1.getColonne() * tailleCase + tailleCase / 2.0;
            double y1 = p1.getLigne() * tailleCase + tailleCase / 2.0;
            double x2 = p2.getColonne() * tailleCase + tailleCase / 2.0;
            double y2 = p2.getLigne() * tailleCase + tailleCase / 2.0;
            
            StdDraw.line(x1, y1, x2, y2);
        }
        
        StdDraw.setPenRadius();
    }

    private void dessinerCasesDisponibles(Plateau plateau, Joueur joueurActuel, List<Rafle> raflesDisponibles)
    {
        if (!pionSelectionne || caseSelectionnee == null) return;
        
        Pion pion = plateau.obtenirPion(caseSelectionnee);
        if (pion == null) return;
        
        // Si on est en cours de rafle, montrer seulement les prochaines étapes possibles (en ROUGE)
        if (rafleEnCours != null)
        {
            for (Rafle r : raflesDisponibles)
            {
                if (r.getChemin().size() > etapeRafle + 1)
                {
                    Position prochaine = r.getChemin().get(etapeRafle + 1);
                    dessinerIndicateurCase(prochaine, true); // Rouge car c'est une capture
                }
            }
        }
        else if (!raflesDisponibles.isEmpty())
        {
            // Si des rafles sont disponibles, afficher les captures possibles en ROUGE
            for (Rafle r : raflesDisponibles)
            {
                if (r.getDepart().equals(caseSelectionnee) && r.getChemin().size() > 1)
                {
                    // Afficher toutes les étapes de la rafle en ROUGE
                    for (int i = 1; i < r.getChemin().size(); i++)
                    {
                        Position etape = r.getChemin().get(i);
                        dessinerIndicateurCase(etape, true); // Rouge car capture obligatoire
                    }
                }
            }
        }
        else
        {
            // Pas de captures obligatoires, afficher les mouvements simples en VERT
            for (int i = 0; i < taillePlateau; i++)
            {
                for (int j = 0; j < taillePlateau; j++)
                {
                    Position destination = new Position(i, j);
                    Mouvement mouvement = new Mouvement(caseSelectionnee, destination);
                    
                    if (mouvement.estValide(plateau, pion))
                    {
                        dessinerIndicateurCase(destination, false); // Vert car mouvement simple
                    }
                }
            }
        }
    }
    
    private void dessinerIndicateurCase(Position pos, boolean estCapture)
    {
        double centreX = pos.getColonne() * tailleCase + tailleCase / 2.0;
        double centreY = pos.getLigne() * tailleCase + tailleCase / 2.0;
        
        if (estCapture)
        {
            // ROUGE pour les captures
            StdDraw.setPenColor(new Color(255, 100, 100, 200));
        }
        else
        {
            // VERT pour les mouvements simples
            StdDraw.setPenColor(new Color(100, 255, 100, 200));
        }
        StdDraw.filledCircle(centreX, centreY, tailleCase / 6.0);
    }

    private void dessinerCase(int ligne, int colonne, Plateau plateau, List<Rafle> raflesDisponibles)
    {
        // Couleur de base de la case
        if ((ligne + colonne) % 2 != 0)
        {
            StdDraw.setPenColor(139, 69, 19);
        }
        else
        {
            StdDraw.setPenColor(245, 222, 179);
        }

        double centreX = colonne * tailleCase + tailleCase / 2.0;
        double centreY = ligne * tailleCase + tailleCase / 2.0;
        StdDraw.filledSquare(centreX, centreY, tailleCase / 2.0);

        // Contour pour le pion sélectionné
        if (pionSelectionne && caseSelectionnee != null && ligne == caseSelectionnee.getLigne() && colonne == caseSelectionnee.getColonne())
        {
            // Si des rafles sont disponibles, contour ROUGE pour indiquer capture obligatoire
            if (!raflesDisponibles.isEmpty())
            {
                StdDraw.setPenColor(255, 50, 50);
            }
            else
            {
                StdDraw.setPenColor(50, 205, 50);
            }
            StdDraw.setPenRadius(0.015);
            StdDraw.square(centreX, centreY, tailleCase / 2.0);
            StdDraw.setPenRadius();
        }
        // Contour ROUGE autour des pions qui PEUVENT faire une rafle (même non sélectionnés)
        else if (!raflesDisponibles.isEmpty() && !pionSelectionne)
        {
            Position pos = new Position(ligne, colonne);
            Pion pion = plateau.obtenirPion(pos);
            
            if (pion != null)
            {
                // Vérifier si ce pion peut faire une rafle maximale
                for (Rafle r : raflesDisponibles)
                {
                    if (r.getDepart().equals(pos))
                    {
                        StdDraw.setPenColor(255, 50, 50);
                        StdDraw.setPenRadius(0.015);
                        StdDraw.square(centreX, centreY, tailleCase / 2.0);
                        StdDraw.setPenRadius();
                        break;
                    }
                }
            }
        }
    }

    private void dessinerPions(Plateau plateau)
    {
        Pion[][] cases = plateau.getCases();
        
        for (int i = 0; i < taillePlateau; i++)
        {
            for (int j = 0; j < taillePlateau; j++)
            {
                if (cases[i][j] != null)
                {
                    // Griser les pions capturés dans la rafle en cours
                    boolean estCapture = rafleEnCours != null && rafleEnCours.pionDejaCapture(new Position(i, j));
                    dessinerPion(i, j, cases[i][j], estCapture);
                }
            }
        }
    }

    private void dessinerPion(int ligne, int colonne, Pion pion, boolean estCapture)
    {
        double centreX = colonne * tailleCase + tailleCase / 2.0;
        double centreY = ligne * tailleCase + tailleCase / 2.0;
        double rayon = tailleCase / 2.8;
        
        // Ombre du pion
        if (!estCapture)
        {
            StdDraw.setPenColor(new Color(50, 50, 50, 100));
            StdDraw.filledCircle(centreX + 2, centreY - 2, rayon);
        }
        
        // Couleur du pion
        if (estCapture)
        {
            StdDraw.setPenColor(new Color(150, 150, 150, 150));
        }
        else if (pion.estNoir())
        {
            StdDraw.setPenColor(20, 20, 20);
        }
        else
        {
            StdDraw.setPenColor(255, 215, 0);
        }
        StdDraw.filledCircle(centreX, centreY, rayon);
        
        if (!estCapture)
        {
            // Contour du pion
            StdDraw.setPenColor(0, 0, 0);
            StdDraw.setPenRadius(0.005);
            StdDraw.circle(centreX, centreY, rayon);
            StdDraw.setPenRadius();
            
            // Brillance
            StdDraw.setPenColor(new Color(255, 255, 255, 150));
            StdDraw.filledCircle(centreX - rayon/3, centreY + rayon/3, rayon/4);

            // Symbole pour les dames
            if (pion.estDame())
            {
                StdDraw.setPenColor(255, 255, 255);
                StdDraw.setPenRadius(0.008);
                StdDraw.circle(centreX, centreY, rayon / 2.0);
                StdDraw.circle(centreX, centreY, rayon / 2.5);
                StdDraw.setPenRadius();
            }
        }
    }

    private void afficherInfosJoueur(Joueur joueurActuel, List<Rafle> raflesDisponibles)
    {
        double largeur = taillePlateau * tailleCase;
        double hauteur = taillePlateau * tailleCase;
        
        // Fond pour le texte
        StdDraw.setPenColor(240, 240, 240);
        StdDraw.filledRectangle(largeur / 2.0, hauteur + 25, largeur / 2.0, 25);
        
        // Texte
        StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        
        String texte = joueurActuel.toString();
        
        // Ajouter indication si capture obligatoire
        if (!raflesDisponibles.isEmpty())
        {
            StdDraw.setPenColor(255, 0, 0);
            int maxCaptures = raflesDisponibles.get(0).getNombreCaptures();
            texte += " - RAFLE OBLIGATOIRE (" + maxCaptures + " pions)";
        }
        else
        {
            StdDraw.setPenColor(0, 0, 0);
        }
        
        StdDraw.text(largeur / 2.0, hauteur + 25, texte);
        StdDraw.setFont();
    }

    private void afficherBoutonAbandon()
    {
        double largeur = taillePlateau * tailleCase;
        double hauteur = taillePlateau * tailleCase;
        double boutonX = largeur - 65;
        double boutonY = hauteur + 25;
        double largeurBouton = 70;
        double hauteurBouton = 28;
        
        boolean estSurBouton = isInsideButton(StdDraw.mouseX(), StdDraw.mouseY(), 
                                              boutonX - largeurBouton / 2.0, boutonX + largeurBouton / 2.0,
                                              boutonY - hauteurBouton / 2.0, boutonY + hauteurBouton / 2.0);
        
        // Fond du bouton
        if (estSurBouton)
        {
            StdDraw.setPenColor(200, 50, 50);
        }
        else
        {
            StdDraw.setPenColor(231, 76, 60);
        }
        StdDraw.filledRectangle(boutonX, boutonY, largeurBouton / 2.0, hauteurBouton / 2.0);
        
        // Contour
        StdDraw.setPenColor(20, 20, 20);
        StdDraw.setPenRadius(0.003);
        StdDraw.rectangle(boutonX, boutonY, largeurBouton / 2.0, hauteurBouton / 2.0);
        StdDraw.setPenRadius();
        
        // Texte
        StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        StdDraw.setPenColor(255, 255, 255);
        StdDraw.text(boutonX, boutonY, "ABANDON");
    }

    private static boolean isInsideButton(double x, double y, double xMin, double xMax, double yMin, double yMax)
    {
        return x >= xMin && x <= xMax && y >= yMin && y <= yMax;
    }

    public Position obtenirClicSouris()
    {
        if (StdDraw.isMousePressed())
        {
            double mouseX = StdDraw.mouseX();
            double mouseY = StdDraw.mouseY();
            
            double largeur = taillePlateau * tailleCase;
            double hauteur = taillePlateau * tailleCase;
            double boutonX = largeur - 60;
            double boutonY = hauteur + 25;
            
            // Vérifier si le bouton Abandon est cliqué
            if (isInsideButton(mouseX, mouseY, boutonX - 25, boutonX + 25, boutonY - 15, boutonY + 15))
            {
                abandonneClique = true;
                attendreRelachementSouris();
                return null;
            }

            int colonne = (int)(mouseX / tailleCase);
            int ligne = (int)(mouseY / tailleCase);

            Position position = new Position(ligne, colonne);
            
            attendreRelachementSouris();

            if (position.estValide(taillePlateau))
            {
                return position;
            }
        }
        return null;
    }

    private void attendreRelachementSouris()
    {
        while (StdDraw.isMousePressed())
        {
            StdDraw.pause(10);
        }
    }

    public void selectionnerCase(Position position)
    {
        this.caseSelectionnee = position;
        this.pionSelectionne = true;
    }

    public void deselectionner()
    {
        this.caseSelectionnee = null;
        this.pionSelectionne = false;
        this.rafleEnCours = null;
        this.etapeRafle = 0;
    }

    public boolean aPionSelectionne()
    {
        return pionSelectionne;
    }

    public Position obtenirSelection()
    {
        return caseSelectionnee;
    }
    
    public void demarrerRafle(Rafle rafle)
    {
        this.rafleEnCours = rafle;
        this.etapeRafle = 0;
    }
    
    public void avancerEtapeRafle()
    {
        this.etapeRafle++;
    }
    
    public Rafle getRafleEnCours()
    {
        return rafleEnCours;
    }
    
    public int getEtapeRafle()
    {
        return etapeRafle;
    }

    public boolean estAbandonneClique()
    {
        boolean resultat = abandonneClique;
        abandonneClique = false;
        return resultat;
    }
}