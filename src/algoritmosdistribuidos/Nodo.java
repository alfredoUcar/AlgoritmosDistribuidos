/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmosdistribuidos;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pablo
 */
public class Nodo extends Thread {

    private int id, inDeficit, outDeficit;
    private int idPadre = -1;
    private int deudores[];
    private boolean terminado;

    private List<Integer> idPredecesores;
    private List<Integer> idSucesores;

    public Nodo(int id, int idPadre) {
        
        this.id = id;
        this.inDeficit = 0;
        this.outDeficit = 0;
        this.idPadre = idPadre;
        this.terminado = false;
        idPredecesores=new ArrayList<Integer>();
        idSucesores=new ArrayList<Integer>();
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
        if (this.idPadre != -1){
            this.outDeficit++;
        }
        
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
        //receive(signal,_);
        this.outDeficit--;
    }
    
    public boolean Terminado(int inDeficit){
         return this.terminado=(inDeficit == 0);
    }
    
    //devuelve el identificador del nodo
    public int getNodeId(){
        return this.id;
    }
    
    /*Añade un hijo al nodo*/
    public void addSucesor(int idSucesor){
        idSucesores.add(idSucesor);
    }
    
    /*Añade un predecesor al nodo*/
    public void addPredecesor(int idPredecesor){
        idPredecesores.add(idPredecesor);
    }
    
    public List <Integer> predecesores(){
        return idPredecesores;
    }
    
    public List <Integer> sucesores(){
        return idSucesores;
    }

    boolean hasSucesor(int id) {
        for (int sucesor : idSucesores){
            if (sucesor==id) return true;
        }
        return false;
    }
    
    boolean hasPredecesor(int id) {
        for (int predecesor : idPredecesores){
            if (predecesor==id) return true;
        }
        return false;
    }
}
