/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmosdistribuidos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author pablo
 */
public class Resultado {

    private int numMensajes;
    private long tiempo;
    private long tiempoAux;
    private List <Enlace> spanningTree;

    public Resultado() {
        numMensajes = 0;
        tiempo = 0;
        tiempoAux = 0;
        spanningTree = new ArrayList<>();
    }

    public synchronized void incrementarMensajes() {
        numMensajes++;
    }

    public int getMensajes() {
        return numMensajes;
    }

    public void iniciarTiempo() {
        tiempoAux = System.currentTimeMillis();
    }

    public void pararTiempo() {
        tiempo = System.currentTimeMillis() - tiempoAux;
    }

    public long getTiempo() {
        return tiempo;
    }

    public void insertarRelacion(int origen, int destino) {
        Enlace link = new Enlace(origen, destino);
        if(!spanningTree.contains(link)){
            spanningTree.add(link);
        }
    }
    
    public void printSpanningTree(){
        System.out.println("Spanning Tree:");
        for (Enlace rel : spanningTree) {
            System.out.println(rel.Pre() + " => " + rel.Post());
        }
    }
    
}
