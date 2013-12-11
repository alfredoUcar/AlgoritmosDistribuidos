/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algoritmosdistribuidos;

/**
 *
 * @author pablo
 */
public class Mensaje {
    
    private int id;
    private String msg;

    public Mensaje(int id, String msg) {
        this.id = id;
        this.msg = msg;
    }
    
    public int getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }
    
}
