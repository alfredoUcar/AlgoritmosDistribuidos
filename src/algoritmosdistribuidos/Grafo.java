/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmosdistribuidos;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 *
 * @author pablo
 */
public class Grafo {

    static final int RAIZ = 0; //id de la raiz
//    private List<Nodo> nodes;
    private List<Integer> nodes; //ids
    private List<Enlace> links;

    public Grafo(String filePath) {
        nodes = new ArrayList<>();
        links = new ArrayList<>();
        try {
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream(filePath);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            br.readLine();//salta la cabecera
            //Read File Line By Line
            while ((strLine = br.readLine()) != null && strLine != "}") {
                String par[] = strLine.split(" -> "); //separa en dos dejando solo los nodes
                if (par.length == 2) {
                    int a = Integer.parseInt(par[0]);//lee id nodo a
                    int b = Integer.parseInt(par[1]);//lee id nodo b

                    if (nodes.isEmpty() || !exists(a)) {
                        //nodos.add(new Nodo(a, -1)); //se añade el nodo a
                        nodes.add(a); //se añade el nodo a
                    }

                    if (!exists(b)) {//no existe
                        //nodos.add(new Nodo(b, -1)); //se añade el nodo b
                        nodes.add(b); //se añade el nodo a
                    }
                    //añadimos relación
                    //addEnlace(a, b);
                    Enlace rel = new Enlace(a, b);
                    if (!existsLink(rel)) {
                        links.add(rel);
                    }
                }
            }
            //Close the input stream
            in.close();
        } catch (IOException e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public boolean exists(int id) {
        return (nodes.indexOf(id) != -1);
        //return (getNodo(id).getNodeId() == id);
    }

    public void print() {
        System.out.println("Grafo:");
        for (Enlace rel : links) {
            System.out.println(rel.Pre() + " => " + rel.Post());
        }
    }

    public int size() {
        return nodes.size();
    }

    public int getNode(int index) {
        return (nodes.get(index));
    }

    public List<Integer> getSucesoresRaiz() {
        List<Integer> sucesores = new ArrayList<>();
        for (Enlace e : links) {
            if (e.Pre() == RAIZ) {
                sucesores.add(e.Post());
            }
        }
        return sucesores;
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

    List<Integer> getNodes() {
        return nodes;
    }

    List<Enlace> getLinks() {
        return links;
    }

    public boolean existsLink(Enlace e) {
        return links.contains(e);
    }

    //dado una lista de nodos devuelve la lista de enlaces que representa
    //el spanning tree
    static List <Enlace> getSpanningTree(Nodo[]nodos){
        List<Enlace> spanningTree = new ArrayList<>();
        for(Nodo n : nodos){
            int idPadre = n.predecesores().isEmpty()? -1 : n.predecesores().get(0); //TODO: cambiar por getidPadre cuando funcione con beanstalk
            int id = n.getNodeId();
            if (idPadre != -1 && id != RAIZ){
                spanningTree.add(new Enlace(idPadre, id));
            }
        }
        return spanningTree;
    }
    
    static void printSpanningTree(List<Enlace> enlaces) {
        System.out.println("Spanning Tree:");
        for (Enlace rel : enlaces) {
            System.out.println(rel.Pre() + " => " + rel.Post());
        }
    }
    
    static void printSpanningTree(Nodo[]nodos){
        printSpanningTree(getSpanningTree(nodos));
    }

}
