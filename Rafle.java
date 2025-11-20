import java.util.ArrayList;
import java.util.List;

public class Rafle
{
    private List<Position> chemin;
    private List<Position> pionsCaptures;
    private Position depart;
    
    public Rafle(Position depart)
    {
        this.depart = depart;
        this.chemin = new ArrayList<>();
        this.pionsCaptures = new ArrayList<>();
        this.chemin.add(depart);
    }
    
    // Constructeur de copie
    public Rafle(Rafle autre)
    {
        this.depart = autre.depart;
        this.chemin = new ArrayList<>(autre.chemin);
        this.pionsCaptures = new ArrayList<>(autre.pionsCaptures);
    }
    
    public void ajouterCapture(Position destination, Position pionCapture)
    {
        chemin.add(destination);
        pionsCaptures.add(pionCapture);
    }
    
    public Position getDepart()
    {
        return depart;
    }
    
    public Position getArrivee()
    {
        return chemin.get(chemin.size() - 1);
    }
    
    public List<Position> getChemin()
    {
        return chemin;
    }
    
    public List<Position> getPionsCaptures()
    {
        return pionsCaptures;
    }
    
    public int getNombreCaptures()
    {
        return pionsCaptures.size();
    }
    
    public boolean contientPosition(Position pos)
    {
        for (Position p : chemin)
        {
            if (p.equals(pos))
            {
                return true;
            }
        }
        return false;
    }
    
    public boolean pionDejaCapture(Position pos)
    {
        for (Position p : pionsCaptures)
        {
            if (p.equals(pos))
            {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Rafle: ");
        for (int i = 0; i < chemin.size(); i++)
        {
            sb.append(chemin.get(i));
            if (i < chemin.size() - 1)
            {
                sb.append(" -> ");
            }
        }
        sb.append(" (").append(getNombreCaptures()).append(" pions capturés)");
        return sb.toString();
    }
}