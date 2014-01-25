/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmosdistribuidos;

import com.trendrr.beanstalk.BeanstalkClient;
import com.trendrr.beanstalk.BeanstalkException;
import com.trendrr.beanstalk.BeanstalkJob;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.*;

/**
 *
 * @author pablo
 */
public class Nodo extends Thread {

    final static String SIGNAL = "signal";
    final static String FIN = "fin";
    final static String HOST = "localhost";
    final static int PORT = 11300;
    final static int RAIZ = 0;

    private int numMensajes;
    private int id, inDeficit, outDeficit;
    private int idPadre = -1;
//    private boolean terminado;
    private BeanstalkClient Client;
    private String tube;
    private List<Integer> inDeficits;
    private List<Integer> idPredecesores;
    private List<Integer> idSucesores;
    private List<Integer> nodosEntrantes;
    private List<Integer> nodosSalientes;
    private boolean seguir;
    private long tiempo;

    public Nodo(int id, List<Integer> entrantes, List<Integer> salientes) {
        this.id = id;
        this.inDeficit = 0;
        this.outDeficit = 0;
        this.idPadre = -1;
//        this.terminado = false;
        numMensajes = 0;
        idPredecesores = new ArrayList<>();
        idSucesores = new ArrayList<>();
        nodosEntrantes = entrantes;
        nodosSalientes = salientes;
        seguir = true;
        tube = String.valueOf(id);
        Client = new BeanstalkClient(HOST, PORT, tube);
        inDeficits = new ArrayList<Integer>(nodosEntrantes.size());
        // Llenamos de 0 el array
        for (Integer i : nodosEntrantes) {
            inDeficits.add(0);
        }
    }

    //A continuación los métodos que hacen uso del Beanstalk
    /**
     *
     * @param mensaje
     * @param idRecpetor
     */
    public void send(String mensaje, int idRecpetor) {
        String message = (tube + ":" + mensaje);
        try {
            Client.useTube(String.valueOf(idRecpetor));
            Client.put(1l, 0, 5000, message.getBytes());
        } catch (BeanstalkException ex) {
            Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return
     */
    private Mensaje recieve() {
        BeanstalkJob job = null;
        String message;
        String origen;
        String msg;
        try {
            Client.useTube(tube);
            job = Client.reserve(1);
            message = new String(job.getData());
            msg = message.split(":")[1];
            origen = message.split(":")[0];
        } catch (Exception ex) {
            msg = "";
            origen = "-1";
        }

        Mensaje msj = new Mensaje(Integer.parseInt(origen), msg);
        return msj;
    }

    //Hasta aquí Beanstalk
    /**
     * Método para enviar un mensaje de un nodo a otro. Se envía un mensaje de
     * la cola de mensajes del nodo a otro nodo especi ficado por id. El
     * outDeficit del nodo emisor aumenta.
     *
     * @param idDestino
     */
    public void sendMensj(String mensaje, int idReceptor) {
        //enviamos el mensaje al nodo indicado        
        if (idPadre != -1 || id == RAIZ) {//solo nodos activos
            send(mensaje, idReceptor);
            outDeficit++;
            numMensajes++;
            //si es el primer mensaje recibido de este nodo lo añadimos a 
            //nuestros sucesores del spanning tree
            if (!idSucesores.contains(idReceptor)) {
                idSucesores.add(idReceptor);
            }
        }
    }

    public void recieveMensj(int idEmisor) {
//        System.out.println("idPadre = "+idPadre);
        if (idPadre == -1) {
            idPadre = idEmisor;
//            System.out.println("[#" + id + "] asignado el padre #" + idEmisor);
        }
        //si es el primer mensaje recibido de este nodo lo añadimos a 
        //nuestros predecesores del spanning tree
        if (!idPredecesores.contains(idEmisor)) {
            idPredecesores.add(idEmisor);
        }
        try {
            int index = nodosEntrantes.lastIndexOf(idEmisor);
            inDeficits.set(index, inDeficits.get(index) + 1);
            inDeficit++;
        } catch (Exception e) {
            System.out.println("Error en #" + id + ", no se pudo recibir mensaje de #" + idEmisor + ":\n\t"
                    + "def size: " + inDeficits.size() + " | entr size: " + nodosEntrantes.size());
        }
    }

    public boolean sendSignal() {
        if (inDeficit > 1) {
            int i;
            for (i = 0; i < inDeficits.size(); i++) {
//                System.out.println("send signal a #" + inDeficits.get(i));
                if ((inDeficits.get(i) > 1) || (inDeficits.get(i) == 1 && nodosEntrantes.get(i) != idPadre)) {
                    break;
                }
            }
            if (i < inDeficits.size()) {
                sendMensj(SIGNAL, nodosEntrantes.get(i));
                inDeficits.set(i, inDeficits.get(i) - 1);
                inDeficit--;
                return true;
            }
            return false;
        } else if ((inDeficit == 1) && (outDeficit == 0)) {
            send(SIGNAL, idPadre);
            inDeficits.set(nodosEntrantes.indexOf(idPadre), 0); //si falla probar nodosEntrantes.index...
            inDeficit = 0;
            idPadre = -1;
            return true;
        }
        return false;
    }

    public void recieveSignal() {
        outDeficit--;
    }

//    public boolean terminado(int inDeficit) {
//        return terminado = (inDeficit == 0);
//    }

    //devuelve el identificador del nodo
    public int getNodeId() {
        return this.id;
    }

    public int getPadreId() {
        return this.idPadre;
    }

    /*Añade un hijo al nodo*/
    public void addSucesor(int idSucesor) {
        idSucesores.add(idSucesor);
    }

    /*Añade un predecesor al nodo*/
    public void addPredecesor(int idPredecesor) {
        idPredecesores.add(idPredecesor);
    }

    public List<Integer> predecesores() {
        return idPredecesores;
    }

    public List<Integer> sucesores() {
        return idSucesores;
    }

    boolean hasSucesor(int id) {
        return idSucesores.contains(id);
    }

    boolean hasPredecesor(int id) {
        return idPredecesores.contains(id);

    }
    
    long getTime(){
        return tiempo;
    }
    
    int getNumMensajes(){
        return numMensajes;
    }

    @Override
    public void run() {
        if (id == RAIZ) {
            entorno();
        } else {
            noEntorno();
        }
    }

    void print() {
        System.out.println("id:\t" + id);
        System.out.println("padre:\t" + idPadre);
        System.out.println("predecesores:\t" + idPredecesores.toString());
        System.out.println("sucesores:\t" + idSucesores.toString());
    }

    void initDeficits() {
        //ponemos a cero todos los inDeficit del nodo
        for (int i = 0; i < idPredecesores.size(); i++) {
            inDeficits.add(0);
        }
    }

    protected void trabajo(String mensaje) {
        //el trabajo consiste en esperar un tiempo
        int tiempo = Integer.parseInt(mensaje);
        try {
            Thread.sleep(tiempo);
        } catch (InterruptedException ex) {
            Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void entorno() {

        Mensaje msg = new Mensaje(-1, "");
        String trabajo = "50";
        tiempo = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            
            for (int saliente : nodosSalientes) {//envía trabajo a todos los nodos salientes                
//                System.out.println("[#" + id + "] envío de trabajo a #" + saliente + " : " + trabajo);
                sendMensj(trabajo, saliente);
            }

            while (outDeficit > 0) {
                //mientras me deban mensajes
                try {
                    msg = recieve();
                    //miro si me llegan signals
                    if (msg.getMsg().equals(SIGNAL)) {
                        recieveSignal();
                    }
                } catch (Exception e) {
                    System.out.println(e.getStackTrace());
                }
            }
            
         
        }
        tiempo = System.currentTimeMillis()-tiempo;
//        System.out.println("Mandamos terminar a todos los nodos");
        // Decimos a todos los nodos que se paren
        for (int saliente : nodosSalientes) {
            sendMensj(FIN, saliente);
        }

    }

    private void noEntorno() {
        Mensaje resp;
        String msg; // Mensaje recibido
        int origen; // Emisor del mensaje

        while (seguir) {
            resp = recieve();
            msg = resp.getMsg();
            origen = resp.getId();
//            String header = ("[#" + id + "] mensaje recibido de #" + origen + " : " + msg);
            switch (msg) {
                case SIGNAL:
//                    System.out.println(header + "\t=>\t" + SIGNAL);
                    recieveSignal();
                    break;
                case FIN: //propaga el mensaje de finalizar
//                    System.out.println(header + "\t=>\t" + FIN);
                    seguir = false;
                    for (Integer saliente : nodosSalientes) {
                        sendMensj(msg, saliente);
                    }
                    break;
                case "":
//                    System.out.println(header + "\t=>\tno hay trabajo");
                    sendSignal();
                    break;
                default:
//                    System.out.println(header + "\t=>\ttrabaja!");
//                    System.out.println("[#"+id+"]trabajo recibido de #"+origen+" : "+msg);
                    recieveMensj(origen);
                    if (idPadre == origen) {
                        for (int saliente : nodosSalientes) {
                            if (saliente != idPadre) {
                                sendMensj(msg, saliente);
                            }
                        }
                    }
//                    System.out.println("[#"+id+"] trabajo a realizar: "+msg+" (de #"+origen+")");
                    trabajo(msg);
                    sendSignal();
            }
        }
        seguir = true;
        Client.close();
    }

}
