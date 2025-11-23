import java.awt.Color;
import java.awt.Font;

public class Menu
{
    private int largeur;
    private int hauteur;
    private int modeJeu;
    private String gagnant;
    
    public static final int MODE_MENU = 0;
    public static final int MODE_FIN_PARTIE = 1;

    public Menu()
    {
        this.largeur = 800;
        this.hauteur = 850;
        this.modeJeu = MODE_MENU;
        this.gagnant = null;
    }

    public void initialiserFenetre()
    {
        StdDraw.setCanvasSize(largeur, hauteur);
        StdDraw.setXscale(0, largeur);
        StdDraw.setYscale(0, hauteur);
        StdDraw.enableDoubleBuffering();
    }

    public void afficher()
    {
        StdDraw.clear(new Color(245, 245, 245));
        
        if (modeJeu == MODE_MENU)
        {
            afficherMenuPrincipal();
        }
        else if (modeJeu == MODE_FIN_PARTIE)
        {
            afficherEcranVictoire();
        }
        
        StdDraw.show();
    }

    private void afficherMenuPrincipal()
    {
        // Titre
        StdDraw.setFont(new Font("Arial", Font.BOLD, 48));
        StdDraw.setPenColor(20, 20, 20);
        StdDraw.text(largeur / 2.0, hauteur - 100, "JEU DE DAMES");
        
        // Sous-titre
        StdDraw.setFont(new Font("Arial", Font.ITALIC, 18));
        StdDraw.setPenColor(100, 100, 100);
        StdDraw.text(largeur / 2.0, hauteur - 150, "Classique et Stratégique");
        
        // Décoration - pions
        dessinerPionDeco(largeur / 2.0 - 200, hauteur - 220, true, false);
        dessinerPionDeco(largeur / 2.0 + 200, hauteur - 220, false, false);
        dessinerPionDeco(largeur / 2.0 - 220, hauteur - 240, true, true);
        dessinerPionDeco(largeur / 2.0 + 220, hauteur - 240, false, true);
        
        // Bouton Nouvelle Partie
        dessinerBouton(largeur / 2.0, hauteur / 2.0 + 80, "NOUVELLE PARTIE", new Color(52, 152, 219), new Color(41, 128, 185));
        
        // Bouton Quitter
        dessinerBouton(largeur / 2.0, hauteur / 2.0 - 20, "QUITTER", new Color(231, 76, 60), new Color(192, 57, 43));
        
        // Info en bas
        StdDraw.setFont(new Font("Arial", Font.PLAIN, 12));
        StdDraw.setPenColor(150, 150, 150);
        StdDraw.text(largeur / 2.0, 50, "Fait par Valentin Beuret");
    }

    private void afficherEcranVictoire()
    {
        // Fond semi-transparent
        StdDraw.setPenColor(new Color(0, 0, 0, 100));
        StdDraw.filledRectangle(largeur / 2.0, hauteur / 2.0, largeur / 2.0, hauteur / 2.0);
        
        // Boîte de victoire
        StdDraw.setPenColor(255, 255, 255);
        StdDraw.filledRectangle(largeur / 2.0, hauteur / 2.0, 280, 200);
        
        // Contour
        StdDraw.setPenColor(20, 20, 20);
        StdDraw.setPenRadius(0.005);
        StdDraw.rectangle(largeur / 2.0, hauteur / 2.0, 280, 200);
        StdDraw.setPenRadius();
        
        // Titre de victoire
        StdDraw.setFont(new Font("Arial", Font.BOLD, 36));
        StdDraw.setPenColor(255, 215, 0);
        StdDraw.text(largeur / 2.0, hauteur / 2.0 + 130, "VICTOIRE !");
        
        // Message du gagnant
        StdDraw.setFont(new Font("Arial", Font.BOLD, 26));
        if (gagnant != null && gagnant.contains("Noir"))
        {
            StdDraw.setPenColor(20, 20, 20);
        }
        else
        {
            StdDraw.setPenColor(255, 215, 0);
        }
        if (gagnant != null)
        {
            StdDraw.text(largeur / 2.0, hauteur / 2.0 + 60, gagnant);
        }
        
        // Texte additif
        StdDraw.setFont(new Font("Arial", Font.ITALIC, 16));
        StdDraw.setPenColor(100, 100, 100);
        StdDraw.text(largeur / 2.0, hauteur / 2.0 + 20, "ont remporté la partie !");
        
        // Bouton Retour au menu
        dessinerBouton(largeur / 2.0, hauteur / 2.0 - 80, "RETOUR AU MENU", new Color(46, 204, 113), new Color(39, 174, 96));
    }

    private void dessinerPionDeco(double x, double y, boolean estNoir, boolean estDame)
    {
        double rayon = 15;
        
        // Couleur du pion
        if (estNoir)
        {
            StdDraw.setPenColor(20, 20, 20);
        }
        else
        {
            StdDraw.setPenColor(255, 215, 0);
        }
        StdDraw.filledCircle(x, y, rayon);
        
        // Contour
        StdDraw.setPenColor(0, 0, 0);
        StdDraw.setPenRadius(0.003);
        StdDraw.circle(x, y, rayon);
        StdDraw.setPenRadius();
        
        // Symbole dame
        if (estDame)
        {
            StdDraw.setPenColor(255, 255, 255);
            StdDraw.setPenRadius(0.005);
            StdDraw.circle(x, y, rayon / 1.8);
            StdDraw.circle(x, y, rayon / 2.3);
            StdDraw.setPenRadius();
        }
    }

    private void dessinerBouton(double x, double y, String texte, Color couleurNormale, Color couleurHover)
    {
        double largeurBouton = 200;
        double hauteurBouton = 50;
        
        double xMin = x - largeurBouton / 2.0;
        double xMax = x + largeurBouton / 2.0;
        double yMin = y - hauteurBouton / 2.0;
        double yMax = y + hauteurBouton / 2.0;
        
        boolean estSurBouton = isInsideButton(StdDraw.mouseX(), StdDraw.mouseY(), xMin, xMax, yMin, yMax);
        
        // Fond
        if (estSurBouton)
        {
            StdDraw.setPenColor(couleurHover);
        }
        else
        {
            StdDraw.setPenColor(couleurNormale);
        }
        StdDraw.filledRectangle(x, y, largeurBouton / 2.0, hauteurBouton / 2.0);
        
        // Contour
        StdDraw.setPenColor(20, 20, 20);
        StdDraw.setPenRadius(0.003);
        StdDraw.rectangle(x, y, largeurBouton / 2.0, hauteurBouton / 2.0);
        StdDraw.setPenRadius();
        
        // Texte
        StdDraw.setFont(new Font("Arial", Font.BOLD, 16));
        StdDraw.setPenColor(255, 255, 255);
        StdDraw.text(x, y, texte);
    }

    public static boolean isInsideButton(double x, double y, double xMin, double xMax, double yMin, double yMax)
    {
        return x >= xMin && x <= xMax && y >= yMin && y <= yMax;
    }

    public void traiterClicMenuPrincipal()
    {
        if (StdDraw.isMousePressed())
        {
            double mouseX = StdDraw.mouseX();
            double mouseY = StdDraw.mouseY();
            
            // Bouton NOUVELLE PARTIE
            if (isInsideButton(mouseX, mouseY, largeur / 2.0 - 100, largeur / 2.0 + 100, hauteur / 2.0 + 55, hauteur / 2.0 + 105))
            {
                modeJeu = 2; // Signale au jeu de démarrer
            }
            // Bouton QUITTER
            else if (isInsideButton(mouseX, mouseY, largeur / 2.0 - 100, largeur / 2.0 + 100, hauteur / 2.0 - 45, hauteur / 2.0 + 5))
            {
                System.exit(0);
            }
            
            attendreRelachementSouris();
        }
    }

    public void traiterClicVictoire()
    {
        if (StdDraw.isMousePressed())
        {
            double mouseX = StdDraw.mouseX();
            double mouseY = StdDraw.mouseY();
            
            // Bouton RETOUR AU MENU
            if (isInsideButton(mouseX, mouseY, largeur / 2.0 - 100, largeur / 2.0 + 100, hauteur / 2.0 - 105, hauteur / 2.0 - 55))
            {
                modeJeu = MODE_MENU;
                gagnant = null;
            }
            
            attendreRelachementSouris();
        }
    }

    private void attendreRelachementSouris()
    {
        while (StdDraw.isMousePressed())
        {
            StdDraw.pause(10);
        }
    }

    public int getModeJeu()
    {
        return modeJeu;
    }

    public void demarrerPartie()
    {
        modeJeu = 2; // Mode jeu lancé
    }

    public void afficherVictoire(String nomGagnant)
    {
        this.gagnant = nomGagnant;
        this.modeJeu = MODE_FIN_PARTIE;
    }

    public void resetMenu()
    {
        modeJeu = MODE_MENU;
        gagnant = null;
    }
}