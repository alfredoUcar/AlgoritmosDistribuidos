/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algoritmosdistribuidos;

import java.io.File;
import java.util.*;


/**
 *
 * @author pablo
 */
public class Grafo {
    static final int RAIZ = 0; //id de la raiz
    AbstractList<Nodo> nodos;
    
    public Grafo(Nodo raiz){
        nodos.add(raiz);
    }
    public Grafo(AbstractList<Nodo> listaNodos){
        nodos=listaNodos;        
    }
    
    public Nodo getRaiz(){
        return getNodo(RAIZ);        
    }
        
    
    //devuelve el nodo o null si no existe
    public Nodo getNodo(int id){
        Nodo nodo=null;
        Iterator<Nodo> it= nodos.iterator();        
        while(it.hasNext()){
            Nodo n = it.next();
            if (n.getNodeId()==id){
                nodo=n; break; //encontrado
            }
        }
        return nodo;
    }
}
