/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmosdistribuidos;

import com.trendrr.beanstalk.BeanstalkClient;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pablo
 */
public class AlgoritmosDistribuidos {

    private static String fileName = "resultados.txt";
    private static String graphFile = "graph.dot";

    static long tiempo = 0; //duración del trabajo
    static int mensajes = 0; //mensajes enviados durante el trabajo

    static Grafo red = new Grafo(graphFile); //crea el grafo que define las conexiones de la red
    static Nodo[] nodos = new Nodo[red.size()]; //nodos de la red
    static BeanstalkClient Client = new BeanstalkClient(Nodo.HOST, Nodo.PORT, String.valueOf(Nodo.RAIZ));
    static int iteraciones = 10; //repeticiones del trabajo    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        //preparación del fichero de salida
        FileWriter output = new FileWriter(fileName);
        BufferedWriter writer = new BufferedWriter(output);

        System.out.println("Empezando las " + iteraciones + " iteraciones...");
        for (int i = 0; i < iteraciones; i++,mensajes=0,tiempo=0) { //realiza el trabajo
            crearHilos();
            iniciarHilos();
            finalizarHilos();
            guardarResultados(i,writer); //guarda los resultados de la iteración actual
        }
        
        //cerramos el fichero de salida
        writer.close();
        output.close();

        System.out.println("Guardados resultados en el fichero '" + fileName + "'");
    }
    
    public static void crearHilos() {
        //generación de los hilos de la red
        for (int j = 0; j < red.getNodes().size(); j++) {
            int id = red.getNodes().get(j);
            Nodo n = new Nodo(id, red.getPredecesores(j), red.getSucesores(j)); //crea el nodo
            nodos[j] = n; //añadimos el nodo a la lista
        }
    }

    public static void iniciarHilos() {
        for (Nodo n : nodos) {
            n.start();
        }
    }

    public static void finalizarHilos() {
        //reaper
        for (Nodo n : nodos) {
            try {
                n.join();
                if (n.getNodeId() == Grafo.RAIZ) {
                    tiempo = n.getTime(); //obtiene el tiempo de la raiz
                }
                mensajes += n.getNumMensajes();
            } catch (InterruptedException ex) {
                Logger.getLogger(AlgoritmosDistribuidos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void guardarResultados(int iteracion, BufferedWriter writer) {
        try {
            writer.write("#### Iteración " + (iteracion + 1) + " ####\n");
            writer.write("tiempo tardado: " + tiempo + "ms\n");
            writer.write("mensajes enviados: " + mensajes + "\n");
            writer.write(Grafo.spanningTreeStr(nodos));
        } catch (IOException e) {
            System.out.println("Error al guardar los resultados de la iteración #" + iteracion + ": " + e.getMessage());
        }
    }

}
