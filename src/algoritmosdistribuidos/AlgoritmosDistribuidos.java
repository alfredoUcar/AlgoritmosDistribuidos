/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmosdistribuidos;

import com.trendrr.beanstalk.BeanstalkClient;
import com.trendrr.beanstalk.BeanstalkException;
import com.trendrr.beanstalk.BeanstalkJob;
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
        Nodo[] nodos = new Nodo[red.size()-1];

        //crea hilos de la red, salvo la raiz que es el proceso en el que nos encontramos(entorno)
        for (int i=0; i<red.getNodes().size();i++) {
            int id=red.getNodes().get(i);            
            if (id==0) {
                continue; //nodo raiz
            }
            Nodo n = new Nodo(id); //crea el nodo
            nodos[i-1]=n; //añadimos el nodo a la lista
        }
        
        //inicializa los predecesores y sucesores de cada nodo
        for(Nodo n : nodos){
            int id=n.getNodeId();
            for (Enlace e : red.getLinks()){
                if(id==e.Pre()) n.addSucesor(e.Post()); //relación n->b
                if(id==e.Post()) n.addPredecesor(e.Pre()); //relación a->n
            }
            n.initDeficits();
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
