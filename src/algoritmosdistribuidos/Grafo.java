
package algoritmosdistribuidos;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Pablo Riutort
 * @author Alfredo Ucendo
 */
public final class Grafo {

    final static int RAIZ = 0; //id asociada a la raiz
    private final List<Integer> nodes; //ids
    private final List<Enlace> links; // relaciones a->b

    public Grafo(String filePath) {
        nodes = new ArrayList<>();
        links = new ArrayList<>();
        try {
            FileInputStream fstream = new FileInputStream(filePath);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            br.readLine();//salta la cabecera
            //leemos linea por linea
            while ((strLine = br.readLine()) != null && !strLine.equals("}")) {// mientras no sea la última linea
                String par[] = strLine.split(" -> "); //separa en dos dejando solo los nodos
                if (par.length == 2) {
                    int a = Integer.parseInt(par[0]);//lee id nodo a
                    int b = Integer.parseInt(par[1]);//lee id nodo b

                    if (!exists(a)) {
                        nodes.add(a); //se añade el nodo a
                    }

                    if (!exists(b)) {//no existe
                        nodes.add(b); //se añade el nodo a
                    }
                    //añadimos relación
                    Enlace rel = new Enlace(a, b);
                    if (!links.contains(rel)) {
                        links.add(rel);
                    }
                }
            }
            //Close the input stream
            in.close();
        } catch (IOException e) {//Catch exception if any
            String error = "Error leyendo el fichero que contiene el grafo: " + e.getMessage();
            Logger.getLogger(Grafo.class.getName()).log(Level.SEVERE, null, error);            
        }
    }

    public boolean exists(int id) {
        return (nodes.indexOf(id) != -1);
    }

    public int size() {
        return nodes.size();
    }

    public int getNode(int index) {
        return (nodes.get(index));
    }
    
    public List<Integer> getPredecesores(int id) {
        List<Integer> predecesores = new ArrayList<>();
        for (Enlace e : links) {
            if (e.Post() == id) {
                predecesores.add(e.Pre());
            }
        }
        return predecesores;
    }    
    
    public List<Integer> getSucesores(int id) {
        List<Integer> sucesores = new ArrayList<>();
        for (Enlace e : links) {
            if (e.Pre() == id) {
                sucesores.add(e.Post());
            }
        }
        return sucesores;
    }

    public List<Integer> getNodes() {
        return nodes;
    }

    public List<Enlace> getLinks() {
        return links;
    }

    //dado una lista de nodos devuelve la lista de enlaces entre nodos
    static List <Enlace> getLinksFromNodes(Nodo[]nodos){
        List<Enlace> links = new ArrayList<>();
        for(Nodo n : nodos){
            int idPadre = n.predecesores().isEmpty()? -1 : n.predecesores().get(0); //TODO: cambiar por getidPadre cuando funcione con beanstalk
            int id = n.getNodeId();
            if (idPadre != -1 && id != RAIZ){
                links.add(new Enlace(idPadre, id));
            }
        }
        return links;
    }
    
    static String graphToString(String nombre,Nodo[]nodos){
        List<Enlace> enlaces = getLinksFromNodes(nodos);
        String res="";
        if (nombre == null) res="Grafo";
        
        res += ":\n";
        for (Enlace rel : enlaces) {
            res += ""+(rel.Pre() + " -> " + rel.Post()+"\n");
        }
        return res+="\n";
    }
    
    /**
     * Pasamos el árbol de expansion mínima a un formato que pueda leer un .dot
     * @param nodos
     * @return 
     */
    static String graphToDot(Nodo[]nodos) {
        List<Enlace> enlaces = getLinksFromNodes(nodos);
        String res= "digraph G";
        
        res += "{\n";
        for (Enlace rel : enlaces) {
            res += ""+(rel.Pre() + " -> " + rel.Post()+"\n");
        }
        return res+="}";
    }
}
