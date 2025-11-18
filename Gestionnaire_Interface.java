import java.awt.Color;

public class Gestionnaire_Interface
{
    private int tailleCase;
    private int taillePlateau;
    private Position caseSelectionnee;
    private boolean pionSelectionne;

    public Gestionnaire_Interface(int tailleCase, int taillePlateau)
    {
        this.tailleCase = tailleCase;
        this.taillePlateau = taillePlateau;
        this.caseSelectionnee = null;
        this.pionSelectionne = false;
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

    public void afficher(Plateau plateau, Joueur joueurActuel)
    {
        StdDraw.clear(StdDraw.WHITE);
        dessinerCases(plateau);
        dessinerCasesDisponibles(plateau);
        dessinerPions(plateau);
        afficherInfosJoueur(joueurActuel);
        StdDraw.show();
    }

    private void dessinerCases(Plateau plateau)
    {
        for (int i = 0; i < taillePlateau; i++)
        {
            for (int j = 0; j < taillePlateau; j++)
            {
                dessinerCase(i, j);
            }
        }
    }

    private void dessinerCasesDisponibles(Plateau plateau)
    {
        if (!pionSelectionne || caseSelectionnee == null)
        {
            return;
        }

        Pion pion = plateau.obtenirPion(caseSelectionnee);
        if (pion == null)
        {
            return;
        }

        for (int i = 0; i < taillePlateau; i++)
        {
            for (int j = 0; j < taillePlateau; j++)
            {
                Position destination = new Position(i, j);
                Mouvement mouvement = new Mouvement(caseSelectionnee, destination);
                
                if (mouvement.estValide(plateau, pion))
                {
                    double centreX = j * tailleCase + tailleCase / 2.0;
                    double centreY = i * tailleCase + tailleCase / 2.0;
                    
                    StdDraw.setPenColor(new Color(100, 200, 100, 180));
                    StdDraw.filledCircle(centreX, centreY, tailleCase / 6.0);
                }
            }
        }
    }

    private void dessinerCase(int ligne, int colonne)
    {
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

        if (pionSelectionne && caseSelectionnee != null && ligne == caseSelectionnee.getLigne() && colonne == caseSelectionnee.getColonne())
        {
            StdDraw.setPenColor(50, 205, 50);
            StdDraw.setPenRadius(0.015);
            StdDraw.square(centreX, centreY, tailleCase / 2.0);
            StdDraw.setPenRadius();
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
                    dessinerPion(i, j, cases[i][j]);
                }
            }
        }
    }

    private void dessinerPion(int ligne, int colonne, Pion pion)
    {
        double centreX = colonne * tailleCase + tailleCase / 2.0;
        double centreY = ligne * tailleCase + tailleCase / 2.0;
        double rayon = tailleCase / 2.8;
        
        // Ombre du pion
        StdDraw.setPenColor(new Color(50, 50, 50, 100));
        StdDraw.filledCircle(centreX + 2, centreY - 2, rayon);
        
        // Couleur du pion
        if (pion.estNoir())
        {
            StdDraw.setPenColor(20, 20, 20);
        }
        else
        {
            StdDraw.setPenColor(255, 215, 0);
        }
        StdDraw.filledCircle(centreX, centreY, rayon);
        
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

    private void afficherInfosJoueur(Joueur joueurActuel)
    {
        double largeur = taillePlateau * tailleCase;
        double hauteur = taillePlateau * tailleCase;
        
        // Fond pour le texte
        StdDraw.setPenColor(240, 240, 240);
        StdDraw.filledRectangle(largeur / 2.0, hauteur + 25, largeur / 2.0, 25);
        
        // Texte
        StdDraw.setPenColor(0, 0, 0);
        StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        StdDraw.text(largeur / 2.0, hauteur + 25, joueurActuel.toString());
        StdDraw.setFont();
    }

    public Position obtenirClicSouris()
    {
        if (StdDraw.isMousePressed())
        {
            double mouseX = StdDraw.mouseX();
            double mouseY = StdDraw.mouseY();

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
    }

    public boolean aPionSelectionne()
    {
        return pionSelectionne;
    }

    public Position obtenirSelection()
    {
        return caseSelectionnee;
    }
}