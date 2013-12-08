/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algoritmosdistribuidos;

/**
 *
 * @author alfredo
 */
public class Enlace {
    private int a,b;
    
    public Enlace(int a, int b){
        this.a=a;
        this.b=b;
    }
    
    public void setPre(int a){
        this.a=a;
    }
    
    public void setPost(int b){
        this.b=b;
    }
    
    public boolean equals(Enlace e){
        return (a==e.a && b==e.b);        
    }

    public int Pre() {
        return a;
    }

    public int Post() {
        return b;
    }
    
    
}
