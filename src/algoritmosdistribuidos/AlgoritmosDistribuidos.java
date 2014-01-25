/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmosdistribuidos;

import com.trendrr.beanstalk.BeanstalkClient;
import java.io.File;
import java.io.IOException;
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
        Nodo[] nodos = new Nodo[red.size()];
<<<<<<< HEAD
        BeanstalkClient Client= new BeanstalkClient(Nodo.HOST, Nodo.PORT, String.valueOf(Nodo.RAIZ));
=======
        BeanstalkClient Client = new BeanstalkClient(Nodo.HOST, Nodo.PORT, String.valueOf(Nodo.RAIZ));
        int trabajo = 60; //esperar 100 milisegundos
        int numTrabajos = 10;
        for (int i = 0; i < numTrabajos; i++) {
            //crea hilos de la red, salvo la raiz que es el proceso en el que nos encontramos(entorno)
            for (int j = 0; j < red.getNodes().size(); j++) {
                int id = red.getNodes().get(j);
                Nodo n = new Nodo(id, red.getPredecesores(j), red.getSucesores(j)); //crea el nodo
//                System.out.println("[#" + id + "]  nodos entrantes: " + red.getPredecesores(j).toString() + " nodos salientes: " + red.getSucesores(j).toString());
                nodos[j] = n; //añadimos el nodo a la lista
            }
>>>>>>> pruebas

            //inicia los hilos
            for (Nodo n : nodos) {
                n.start();
            }
            
            long tiempo = -1;
            int mensajes = 0;
            //reaper
            for (Nodo n : nodos) {
                try {                    
                    n.join();          
                    if (n.getNodeId()==Grafo.RAIZ) tiempo = n.getTime();
                    mensajes+=n.getNumMensajes();
                } catch (InterruptedException ex) {
                    Logger.getLogger(AlgoritmosDistribuidos.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            System.out.println("  #### Iteración " + (i + 1) + " ####");
                System.out.println("\ttiempo tardado: " + tiempo + "ms");
                System.out.println("\tmensajes enviados: " + mensajes + "\n");
                Grafo.printSpanningTree(nodos);
                System.out.print("\n");
        }

    }

}
