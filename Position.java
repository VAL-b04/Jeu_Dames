public class Position
{
    private int ligne;
    private int colonne;

    public Position(int ligne, int colonne)
    {
        this.ligne = ligne;
        this.colonne = colonne;
    }

    public int getLigne()
    {
        return ligne;
    }

    public int getColonne()
    {
        return colonne;
    }

    public boolean estValide(int taillePlateau)
    {
        return ligne >= 0 && ligne < taillePlateau && colonne >= 0 && colonne < taillePlateau;
    }

    public int distanceLigne(Position autre)
    {
        return autre.ligne - this.ligne;
    }

    public int distanceColonne(Position autre)
    {
        return Math.abs(autre.colonne - this.colonne);
    }

    public Position positionMilieu(Position autre)
    {
        int ligneMilieu = (this.ligne + autre.ligne) / 2;
        int colonneMilieu = (this.colonne + autre.colonne) / 2;
        return new Position(ligneMilieu, colonneMilieu);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Position)
        {
            Position autre = (Position) obj;
            return this.ligne == autre.ligne && this.colonne == autre.colonne;
        }
        return false;
    }

    @Override
    public String toString()
    {
        return "(" + ligne + ", " + colonne + ")";
    }
}