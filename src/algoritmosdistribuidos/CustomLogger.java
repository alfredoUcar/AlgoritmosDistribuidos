/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algoritmosdistribuidos;

import java.io.*;
import java.util.Date;

/**
 *
 * @author alfredo
 */
public class CustomLogger extends Thread {
    private int messages;
    private File output;
    private String lastMsg;
    private boolean stampDate;
    
    public CustomLogger(File log){
        messages=0;
        stampDate=true;
        output=log.exists()? log : new File("/users/algoritmos_distribuidos.log");
    }
    
    //stampDate: true para mostrar fecha y hora del mensaje
    public CustomLogger(File log, boolean stampDate){
        messages=0;
        stampDate=true;
        output=log.exists()? log : new File("/users/algoritmos_distribuidos.log");
    }
    
    //Añade el mensaje al log
    public void log(String message) throws IOException{
        Date now = new Date();
        String msg = stampDate? "["+now.toString()+"]"+message : message;
        if (!output.canWrite()) output.setWritable(true, false);
        FileWriter fw = new FileWriter(output.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out= new PrintWriter(bw);
        out.println(msg);
        messages++;
    }
    
    public int count(){
        return messages;
    }
    
    public void resetCount(){
        messages=0;
    }
}
