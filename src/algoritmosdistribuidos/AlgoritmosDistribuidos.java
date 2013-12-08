/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmosdistribuidos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pablo
 */
public class AlgoritmosDistribuidos {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
        CustomLogger log = new CustomLogger(new File("/users/algoritmos_distribuidos.log"));
        Grafo red = new Grafo("graph.dot");
        List<Nodo> nodos = new ArrayList<Nodo>() {
        };

        //crea hilos de la red, salvo la raiz que es el proceso en el que nos encontramos
        for (int id : red.getNodos()) {
            if (id == 0) {
                continue; //nodo raiz
            }
            Nodo n = new Nodo(id, -1);
            nodos.add(n); //añadimos el nodo a la lista
        }
        
        //inicia los hilos
        for (Nodo n : nodos) {
            n.start();
        }

        //reaper
        for (Nodo n : nodos) {
            try {
                n.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(AlgoritmosDistribuidos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
