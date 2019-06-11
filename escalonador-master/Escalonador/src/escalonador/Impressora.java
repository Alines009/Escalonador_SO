package escalonador;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 *
 * @author Ricardo Monteiro
 */
public class Impressora {
    private int id;
    public Processo p;
    private int tempo;
    private int tempoUtilizada;
    
    private PrintStream erro;
    
    public Impressora(int id) throws UnsupportedEncodingException{
        this.erro = new PrintStream(System.err, true, "UTF-8");
        this.id = id;
        this.tempo = 3;
    }
    
    public int recebeProcesso(Processo p){
        if(p != null){
            this.p = p;
            return 0;
        }
        this.erro.println("Erro 1 (Impressora.recebeProcesso): Não existe processo para ser adicionado.");
        return 1;
    }
    
    public boolean terminouExecucao(){
        if(!this.isOciosa()){
            if (this.tempoUtilizada == this.tempo){
                return true;
            }
        }
        return false;
    }
    
    public void incrementaTempo(){
        if(!this.isOciosa()){
            this.tempoUtilizada++;
        }
    }
    
    public boolean isOciosa(){
        if (this.p == null){
            return true;
        }
        return false;
    }
    
    public Object enviaProcesso(){
        if(!this.isOciosa()){   // O processo está na impressora
            Processo p = this.p;
            // "Reseto" a impressora
            this.p = null;
            this.tempoUtilizada = 0;
            
            // envio p
            return p;
        }
        this.erro.println("Erro 2 (Impressora.enviaProcesso): Não existe processo para ser enviado.");
        return 2;
    }
}
