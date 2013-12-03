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
    AbstractList<Nodo> nodos;
    
    public Grafo(Nodo raiz){
        nodos.add(raiz);
    }
    public Grafo(AbstractList<Nodo> listaNodos){
        nodos=listaNodos;        
    }
    
    public Nodo getRaiz(){
        return getNodo(0);        
    }
        
    
    //devuelve el nodo o null si no existe
    public Nodo getNodo(int id){
        Nodo nodo=null;
        Iterator<Nodo> it= nodos.iterator();        
        while(it.hasNext()){
            Nodo n = it.next();
            if (n.id==id){
                nodo=n; break; //encontrado
            }
        }
        return nodo;
    }
}
