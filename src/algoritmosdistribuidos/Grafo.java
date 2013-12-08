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
//    private List<Nodo> nodos;
    private List<Integer> nodos; //ids
    private List<Enlace> enlaces; 

    public Grafo(String filePath) {
//        nodos = new ArrayList<Nodo>();
        nodos = new ArrayList<Integer>();
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
                String par[] = strLine.split(" -> "); //separa en dos dejando solo los nodos
                if (par.length == 2) {
                    int a = Integer.parseInt(par[0]);//lee id nodo a
                    int b = Integer.parseInt(par[1]);//lee id nodo b

                    if (nodos.isEmpty() || !exists(a)) {
                        //nodos.add(new Nodo(a, -1)); //se añade el nodo a
                        nodos.add(a); //se añade el nodo a
                    }

                    if (!exists(b)) {//no existe
                        //nodos.add(new Nodo(b, -1)); //se añade el nodo b
                        nodos.add(b); //se añade el nodo a
                    }
                    //añadimos relación
                    //addEnlace(a, b);
                    Enlace rel = new Enlace(a,b);
                    if (!enlaces.contains(rel)) enlaces.add(rel);
                }
            }
            //Close the input stream
            in.close();
        } catch (IOException e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

//    public Nodo getRaiz() {
//        return getNodo(RAIZ);
//    }

    //devuelve el nodo o null si no existe
//    public Nodo getNodo(int id) {
//
//        for (Nodo nodo : nodos) {
//            if (nodo.getNodeId() == id) {
//                return (nodo);
//            }
//        }
//
//        return (new Nodo(-1, -1));
//    }


    //devuelve true si se ha reemplazado el nodo
//    public boolean replaceNodo(int id, Nodo newNodo) {
//        for (int i = 0; i < nodos.size(); i++) {
//            if (nodos.get(i).getNodeId() == id) {
//                nodos.set(i, newNodo);
//                return true;
//            }
//        }
//        return false;
//    }

    public boolean exists(int id) {
        return (nodos.indexOf(id) != -1);
        //return (getNodo(id).getNodeId() == id);
    }

    /*
     Añade la relación a->b, se supone que los nodos a y b existen
     devuelve true si se ha creado el enlace
     */
//    private boolean addEnlace(int a, int b) {
//        if (!exists(a) || !exists(b)){
//            System.out.println("No se puede crear el enlace, los nodos deben pertenecer al grafo");
//            return false;
//        }
//        //lectura de los nodos
//        Nodo nodoA = getNodo(a);
//        Nodo nodoB = getNodo(b);
//        //modificación de los nodos
//        nodoA.addSucesor(b);
//        nodoB.addPredecesor(a);
//        //guarda los nodos
//        return (replaceNodo(a, nodoA) && replaceNodo(b, nodoB));
//    }

    public void print() {
        System.out.println("Grafo:");
//        for (Nodo nodo : nodos) {
//            for (int sucesor : nodo.sucesores()) {
//                System.out.println(nodo.getNodeId() + " => " + getNodo(sucesor).getNodeId());
//            }
//        }
        for (Enlace rel : enlaces) {
                System.out.println(rel.Pre() + " => " + rel.Post());
        }
    }
    
    //devuelve true si el grafo tiene la relación a->b
//    public boolean hasEnlace(int a, int b){
//        if (exists(a) && exists(b)){ //los nodos a y b pertenecen al grafo
//            Nodo nodoA = getNodo(a);
//            return (nodoA.hasSucesor(b));
//        }
//        return false;
//    }
    
    public int size(){
        return nodos.size();
    }
    
    public int getNodo(int index){
        return (nodos.get(index));
    }

    List<Integer> getNodos() {        
        return nodos;
    }
    
    List<Enlace> getEnlaces() {        
        return enlaces;
    }
    
    
}
