
package algoritmosdistribuidos;

/**
 * @author Pablo Riutort
 * @author Alfredo Ucendo
 */
public class Mensaje {
    
    private final int id; //origen
    private final String message;

    public Mensaje(int id, String msg) {
        this.id = id;
        this.message = msg;
    }
    
    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
    
    public boolean equals(String txt){
        return (message == null ? txt == null : message.equals(txt));
    }
    
}
