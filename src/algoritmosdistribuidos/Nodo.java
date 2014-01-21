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
import java.util.List;
import java.util.logging.*;

/**
 *
 * @author pablo
 */
public class Nodo extends Thread {

    final static String SIGNAL = "SIGNAL";
    final static String FIN = "FIN";
    final static String HOST = "localhost";
    final static int PORT = 11300;
    final static int RAIZ = 0;

    private static int numMensajes = 0;
    private int id, inDeficit, outDeficit;
    private int idPadre = -1;
    private boolean terminado;
    private BeanstalkClient Client;
    private String tube;
    private List<Integer> inDeficits;
    private List<Integer> idPredecesores;
    private List<Integer> idSucesores;
    private List<Integer> nodosEntrantes;
    private List<Integer> nodosSalientes; 
    private int trabajo;
    private boolean seguir;

    public Nodo(int id, List<Integer> entrantes,List<Integer> salientes) {
        this.id = id;
        this.inDeficit = 0;
        this.outDeficit = 0;
        this.idPadre = -1;
        this.terminado = false;
        idPredecesores = new ArrayList<>();
        idSucesores = new ArrayList<>();
        nodosEntrantes = entrantes;
        nodosSalientes = salientes;
        inDeficits = new ArrayList<>();
        seguir = true;
        tube = String.valueOf(id);
        Client = new BeanstalkClient(HOST, PORT, tube);
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
        }
    }

    public void recieveMensj(int idEmisor) {
        if (idPadre == -1) {
            idPadre = idEmisor;
            //TODO: hacer algo más??
        }
        System.out.println("tam idPred: "+idPredecesores.size());
        int index = idPredecesores.get(idEmisor);
        idPredecesores.set(index, inDeficits.get(index) + 1);
        inDeficit++;
    }

    public boolean sendSignal() {
        if (inDeficit > 1) {
            int i;
            for (i = 0; i < inDeficits.size(); i++) {
                if ((inDeficits.get(i) > 1) || (inDeficits.get(i) == 1 && idPredecesores.get(i) != idPadre)) {
                    break;
                }
            }

            if (i < inDeficits.size()) {                
                sendMensj(SIGNAL, idPredecesores.get(i));
                inDeficits.set(i, inDeficits.get(i) - 1);
                inDeficit--;
                return true;
            }
            return false;
        } else if ((inDeficit == 1) && (terminado) && (outDeficit == 0)) {
            send(SIGNAL, idPadre);
            inDeficits.set(inDeficits.indexOf(idPadre), 0);
            inDeficit = 0;
            idPadre = -1;
            return true;
        }
        return false;
    }

    public void recieveSignal() {
        outDeficit--;
    }

    public boolean terminado(int inDeficit) {
        return terminado = (inDeficit == 0);
    }

    //devuelve el identificador del nodo
    public int getNodeId() {
        return this.id;
    }
    
    public int getPadreId(){
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
        for (int i = 0; i < 10; i++) {
            for (int saliente : nodosSalientes) {
                sendMensj("50", saliente);
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
                }
            }
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
            switch (msg) {
                case SIGNAL:    recieveSignal();break;
                case FIN:
                    seguir = false;
                    for (Integer idSucesor : idSucesores) {
                        sendMensj(msg, idSucesor);
                    }
                    break;
                case "":    sendSignal();break;
                default:                 
                    System.out.println("origen: "+origen);
                    recieveMensj(origen);
                    if (idPadre == origen) {
                        for (int idPredecesor : idPredecesores) {
                            if (idPredecesor != idPadre) {
                                sendMensj(msg, idPredecesor);
                            }
                        }
                    }
                    trabajo(msg);
                    sendSignal();
            }
        }
        Client.close();
    }
}
