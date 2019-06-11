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
    public Processo p;
    private int tempo;
    private int tempoUtilizado;
    
    private PrintStream erro;
    
    public Disco(int id) throws UnsupportedEncodingException{
        this.erro = new PrintStream(System.err, true, "UTF-8");
        this.id = id;
        this.tempo = 2;
    }
    
    public int recebeProcesso(Processo p){
        if(p != null){
            this.p = p;
            return 0;
        }
        this.erro.println("Erro 1 (Disco.recebeProcesso): Não existe processo para ser adicionado.");
        return 1;
    }
    
    public boolean terminouExecucao(){
        if(!this.isOcioso()){
            if (this.tempoUtilizado == this.tempo){
                return true;
            }
        }
        return false;
    }
    
    public void incrementaTempo(){
        if(!this.isOcioso()){
            this.tempoUtilizado++;
        }
    }
    
    public boolean isOcioso(){
        if (this.p == null){
            return true;
        }
        return false;
    }
    
    public Object enviaProcesso(){
        if(!this.isOcioso()){   // O processo está na impressora
            Processo p = this.p;
            // "Reseto" a impressora
            this.p = null;
            this.tempoUtilizado = 0;
            
            // envio p
            return p;
        }
        this.erro.println("Erro 2 (Impressora.enviaProcesso): Não existe processo para ser enviado.");
        return 2;
    }
}