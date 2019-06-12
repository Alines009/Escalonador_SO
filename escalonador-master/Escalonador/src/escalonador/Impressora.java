package escalonador;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 *
 * @author Ricardo Monteiro
 */
public class Impressora {
    private int id;                 // Identificador da impressora
    private Processo p;             // Processo atualmente na impressora
    private int tempo;              // Tempo necessário para se realizar uma função na impressora
    private int tempoUtilizada;     // Tempo que o processo atual está utilizando a impressora
    
    private PrintStream erro;
    
    //Inicia impressora
    public Impressora(int id) throws UnsupportedEncodingException{
        this.erro = new PrintStream(System.err, true, "UTF-8");
        this.id = id;
        this.tempo = 3;
    }
    
    //impressora recebe processo
    public int recebeProcesso(Processo p){
        if(p != null){
            this.p = p;
            return 0;
        }
        this.erro.println("Erro 1 (Impressora.recebeProcesso): Não existe processo para ser adicionado.");
        return 1;
    }
    
    // Função que verifica se o processo terminou sua função na impressora
    public boolean terminouExecucao(){
        if(!this.isOciosa()){
            if (this.tempoUtilizada == this.tempo){
                return true;
            }
        }
        return false;
    }
    
    // Função que incrementa o tempo do processo atual na impressora
    public void incrementaTempo(){
        if(!this.isOciosa()){
            this.tempoUtilizada++;
        }
    }
    
    //verifica se impressora está ociosa
    public boolean isOciosa(){
        if (this.p == null){
            return true;
        }
        return false;
    }
    
    //impressora para a execução do processo atual e o envia para outro lugar
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
    
    //retorna o processo na impressora
    public Processo getProcesso(){
        return this.p;
    }
}
