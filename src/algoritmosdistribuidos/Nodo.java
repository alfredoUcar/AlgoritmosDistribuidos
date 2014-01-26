
package algoritmosdistribuidos;

import com.trendrr.beanstalk.BeanstalkClient;
import com.trendrr.beanstalk.BeanstalkException;
import com.trendrr.beanstalk.BeanstalkJob;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

/**
 * @author Pablo Riutort
 * @author Alfredo Ucendo
 */
public class Nodo extends Thread {

    //configuración beanstalkd
    final static String HOST = "localhost";
    final static int PORT = 11300;
    private final BeanstalkClient Client;
    private final String tube;
    //mensajes
    final static String SIGNAL = "signal";
    final static String FIN = "fin";
    final static String TRABAJO = "100";

    private boolean seguir; // flag para los nodos 'no entorno'

    private final int id;
    private int idPadre;
    private int inDeficit, outDeficit;

    private final List<Integer> nodosEntrantes; //nodos de los que puede recibir mensajes
    private final List<Integer> nodosSalientes; //nodos a los que puede enviar mensajes 
    private List<Integer> inDeficits; //contador de in deficits de cada nodo entrante

    private final List<Integer> idPredecesores; //nodos de los que ha recibido algún mensaje
    private final List<Integer> idSucesores; //nodos a los que ha enviado algún mensaje
    private int mensajesEnviados;
    private long tiempo;

    public Nodo(int id, List<Integer> entrantes, List<Integer> salientes) {
        this.id = id;
        this.inDeficit = 0;
        this.outDeficit = 0;
        this.idPadre = -1;
        mensajesEnviados = 0;
        idPredecesores = new ArrayList<>();
        idSucesores = new ArrayList<>();
        nodosEntrantes = entrantes;
        nodosSalientes = salientes;
        seguir = true;
        tube = String.valueOf(id);
        Client = new BeanstalkClient(HOST, PORT, tube);
        initIncomingDeficits();
    }    
    
    @Override
    public void run() {
        if (id == Grafo.RAIZ) {
            entorno();
        } else {
            noEntorno();
        }
    }
    
    //acciones llevadas a cabo por el nodo raiz 
    private void entorno() {
        tiempo = System.currentTimeMillis(); //inicia el tiempo

        mandarTrabajo(TRABAJO);
        while (outDeficit > 0) {//mientras me falten signals por recibir           
            comprobarSignals();
        }
        tiempo = System.currentTimeMillis() - tiempo; //detiene el tiempo
        finalizar();
    }
    
    //acciones llevadas a cabo por cualquier nodo que no sea raiz
    private void noEntorno() {
        while (seguir) {
            Mensaje msg = recieve();
            String contenido = msg.getMessage();
            int origen = msg.getId();
            switch (contenido) {//procesa el mensaje recibido
                case SIGNAL:
                    recieveSignal();
                    break;
                case FIN: //propaga el mensaje de finalizar
                    finalizar();
                    break;
                default: //se trata de un trabajo
                    procesarTrabajo(origen, contenido);
            }
        }
        Client.close();
    }

    /**
     * **********************************************************
     * FUNCIONES QUE HACEN USO DEL BEANSTALKD
     * **********************************************************
     */
    /**
     * envía el mensaje a la tubería del receptor
     *
     * @param mensaje información que se desea transmitir
     * @param idRecepetor nodo destinatario
     */
    public void send(String mensaje, int idRecepetor) {
        //codifica el origen en el mensaje para ser identificado por el destinatario
        String message = (tube + ":" + mensaje);
        try {
            Client.useTube(String.valueOf(idRecepetor));
            Client.put(1l, 0, 5000, message.getBytes());
        } catch (BeanstalkException ex) {
            Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Obtiene el mensaje de su tubería
     *
     * @return mensaje parseado
     */
    private Mensaje recieve() {
        BeanstalkJob job;
        String[] message;
        String origen;
        String contenido;
        try {
            Client.useTube(tube);
            job = Client.reserve(1);
            message = (new String(job.getData())).split(":"); //separa el origen del contenido
            origen = message[0];
            contenido = message[1];
        } catch (BeanstalkException ex) {
            contenido = "";
            origen = "-1";
        }

        Mensaje msj = new Mensaje(Integer.parseInt(origen), contenido);
        return msj;
    }

    /**
     * **********************************************************
     * FUNCIONES PARA COMUNICARSE CON OTROS NODOS
     * **********************************************************
     */
    /**
     * Método para enviar un mensaje de un nodo a otro. Se envía un mensaje de
     * la cola de mensajes del nodo a otro nodo especificado por id. El
     * outDeficit del nodo emisor aumenta.
     *
     * @param mensaje
     * @param idReceptor
     */
    public void sendMessage(String mensaje, int idReceptor) {
        if (idPadre != -1 || id == Grafo.RAIZ) {//si está activo
            send(mensaje, idReceptor);
            outDeficit++;
            mensajesEnviados++;
            addSucesor(idReceptor);
        }
    }


    public void recieveMessage(int idEmisor) {
        if (idPadre == -1) {
            //es el primer mensaje que recibe y asigna su emisor como el padre
            idPadre = idEmisor;
        }

        addPredecesor(idEmisor);

        try {
            int index = nodosEntrantes.lastIndexOf(idEmisor);
            inDeficits.set(index, inDeficits.get(index) + 1); //inDeficit para ese nodo
            inDeficit++;
        } catch (Exception e) {}
    }

    public boolean sendSignal() {
        if (inDeficit > 1) {//si no es el último signal...
            int i;
            for (i = 0; i < inDeficits.size(); i++) {
                if ((inDeficits.get(i) > 1) || (inDeficits.get(i) == 1 && nodosEntrantes.get(i) != idPadre)) {
                    break; //encontrado inDeficit para decrementar
                }
            }

            if (i < inDeficits.size()) {
                sendMessage(SIGNAL, nodosEntrantes.get(i));
                inDeficits.set(i, inDeficits.get(i) - 1);
                inDeficit--;
                return true;
            }
            return false;
        } else if ((inDeficit == 1) && (outDeficit == 0)) {//último signal
            send(SIGNAL, idPadre); //envía el signal al padre
            inDeficits.set(nodosEntrantes.indexOf(idPadre), 0);
            inDeficit = 0;
            idPadre = -1;
            return true;
        }
        return false;
    }
    
    /**
     * función del nodo entorno que comprueba si ha recibido algún signal
     */
    private void comprobarSignals() {
        try {
            //miro si me llegan signals
            if (recieve().getMessage().equals(SIGNAL)) {
                recieveSignal();
            }
        } catch (Exception e) {
            Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, e.getStackTrace());
        }
    }

    public void recieveSignal() {
        outDeficit--;
    }
    
    private void mandarTrabajo(String trabajo) {
        for (int saliente : nodosSalientes) {//envía el trabajo a todos los nodos salientes                
            sendMessage(trabajo, saliente);
        }
    }  
    
    /**
     * Propaga el mensaje de finalizar a todos los hijos
     */
    private void finalizar() {
        seguir = false;
        for (Integer saliente : nodosSalientes) {
            sendMessage(FIN, saliente);
        }
    }
    
    /**********************************************
     *                  INTERFAZ
     **********************************************/
    public int getNodeId() {
        return this.id;
    }

    public int getPadreId() {
        return this.idPadre;
    }    

    public List<Integer> predecesores() {
        return idPredecesores;
    }

    public List<Integer> sucesores() {
        return idSucesores;
    }

    long getTime() {
        return tiempo;
    }

    int getNumMensajes() {
        return mensajesEnviados;
    }

    /*********************************************************
     *                  FUNCIONES AUXILIARES
     *********************************************************/
    //inicializacion del vector de inDeficits
    private void initIncomingDeficits() {
        inDeficits = new ArrayList<>(nodosEntrantes.size());
        // Llenamos de 0 el array
        for (Integer i : nodosEntrantes) {
            inDeficits.add(0);
        }
    }
    
    /*Añade un sucesor al nodo, si no existe*/
    private boolean addSucesor(int idSucesor) {
        if (idSucesores.contains(idSucesor)) {//ya existe este sucesor
            return false;
        } else {
            idSucesores.add(idSucesor); //añade el nuevo sucesor
            return true;
        }
    }

    /*Añade un predecesor al nodo, si no existe*/
    private boolean addPredecesor(int idPredecesor) {
        if (idPredecesores.contains(idPredecesor)) {//ya existe este predecesor
            return false;
        } else {
            idPredecesores.add(idPredecesor); //añade el nuevo predecesor
            return true;
        }
    }
    
    /**
     * Realiza un trabajo.
     * @param trabajo string que contiene el trabajo a realizar
     */
    private void hacerTrabajo(String trabajo) {
        int espera = Integer.parseInt(trabajo); //interpreta el trabajo como un tiempo de espera
        try {
            Thread.sleep(espera);
        } catch (InterruptedException ex) {
            Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }     

    private void procesarTrabajo(int origen, String trabajo) {
        recieveMessage(origen);
        if (idPadre == origen) {
            mandarTrabajo(trabajo);
        }
        hacerTrabajo(trabajo);
        sendSignal();
    }
}
