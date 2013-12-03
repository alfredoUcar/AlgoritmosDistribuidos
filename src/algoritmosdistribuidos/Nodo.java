/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmosdistribuidos;

import java.util.AbstractList;

/**
 *
 * @author pablo
 */
public class Nodo extends Thread {

    private int id, idPadre, inDeficit, outDeficit;
    private int deudores[];
    private boolean terminado;

    AbstractList<Integer> idPadres;
    AbstractList<Integer> idHijos;

    public Nodo(int id, int idPadre) {
        this.id = id;
        this.inDeficit = 0;
        this.outDeficit = 0;
        this.idPadre = idPadre;
        this.terminado = false;
    }

    /**
     * Método para enviar un mensaje de un nodo a otro. Se envía un mensaje de
     * la cola de mensajes del nodo a otro nodo especi ficado por id. El
     * outDeficit del nodo emisor aumenta.
     *
     * @param idDestino
     */
    public void sendMensj(String mensaje, int idReceptor, int myId) {
        //enviamos el mensaje al nodo indicado
        this.outDeficit++;
    }

    public void receiveMensj(String mensaje, int idEmisor) {
        this.inDeficit++;
    }

    public boolean sendSignal(/*signal, E, */int myId) {
        if (this.inDeficit > 1) {
           if ((this.inDeficit == 1) && (this.terminado) && (this.outDeficit == 0)){
               //enviarle el mensaje al padre y acabar.
           }
            /*
             E ← some edge E with inDeficit[E] = 0
             send(signal, E, myID)
             decrement inDeficit[E] and inDeficit
             */
            //send(signal, E, myId);
            this.inDeficit--;
            return true;
        }
        return false;
    }

    public void receiveSignal() {
        receive(signal,_);
        this.outDeficit--;
    }
    
    public boolean Terminado(int inDeficit){
         return this.terminado=(inDeficit == 0);
    }
}
