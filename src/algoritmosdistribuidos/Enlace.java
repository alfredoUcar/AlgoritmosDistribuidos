
package algoritmosdistribuidos;

/**
 * @author Pablo Riutort
 * @author Alfredo Ucendo
 */
public class Enlace {
    private final int a,b;
    
    public Enlace(int a, int b){
        this.a=a;
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
