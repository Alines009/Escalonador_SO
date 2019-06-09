package escalonador;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 *
 * @author Ricardo Monteiro
 */
public class Disco {
    private int id;
    private ArrayList<Processo> lista;
    
    private PrintStream erro;
    
    public Disco(int id) throws UnsupportedEncodingException{
        this.erro = new PrintStream(System.err, true, "UTF-8");
        this.id = id;
        this.lista = new ArrayList<Processo>();
    }
    
    public int recebeProcesso(Processo p){
        if(p != null){
            this.lista.add(p);
            return 0;
        }
        this.erro.println("Erro 1 (Disco.recebeProcesso): Não existe processo para ser adicionado.");
        return 1;
    };
    
    
    public Object enviaProcesso(){
        if(!this.lista.isEmpty()){
            return this.lista.remove(0);
        }
        this.erro.println("Erro 2 (Disco.enviaProcesso): Fila Vazia.");
        return 2;
    }
}
