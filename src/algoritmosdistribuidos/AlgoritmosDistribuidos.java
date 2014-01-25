/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmosdistribuidos;

import com.trendrr.beanstalk.BeanstalkClient;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

        FileWriter output = new FileWriter("iteraciones.txt");
        BufferedWriter writer = new BufferedWriter(output);
        
        CustomLogger log = new CustomLogger(new File("/users/algoritmos_distribuidos.log"));
        Grafo red = new Grafo("graph.dot");
        Nodo[] nodos = new Nodo[red.size()];
        BeanstalkClient Client = new BeanstalkClient(Nodo.HOST, Nodo.PORT, String.valueOf(Nodo.RAIZ));
        int iteraciones = 10;
        System.out.println("Empezando las "+iteraciones+" iteraciones");
        for (int i = 0; i < iteraciones; i++) {            
            //crea hilos de la red, salvo la raiz que es el proceso en el que nos encontramos(entorno)
            for (int j = 0; j < red.getNodes().size(); j++) {
                int id = red.getNodes().get(j);
                Nodo n = new Nodo(id, red.getPredecesores(j), red.getSucesores(j)); //crea el nodo
//                System.out.println("[#" + id + "]  nodos entrantes: " + red.getPredecesores(j).toString() + " nodos salientes: " + red.getSucesores(j).toString());
                nodos[j] = n; //añadimos el nodo a la lista
            }

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
            
            writer.write("#### Iteración " + (i + 1) + " ####\n");
            writer.write("tiempo tardado: " + tiempo + "ms");
            writer.write("mensajes enviados: " + mensajes + "\n");
            writer.write(Grafo.spanningTreeStr(nodos));
        }
        
        writer.close();
        output.close();
        
        System.out.println("Guardados resultados en el fichero");

    }

}
