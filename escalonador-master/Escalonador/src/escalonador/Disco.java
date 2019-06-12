package escalonador;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 *
 * @author Ricardo Monteiro
 */
public class Disco {
    private int id;             // Identificador do disco
    private Processo p;         // Processo atualmente no disco
    private int tempo;          // Tempo necessário para se realizar uma função no disco
    private int tempoUtilizado; // Tempo que o processo atual está utilizando o disco
    
    private PrintStream erro;
    
    //Inicia disco
    public Disco(int id) throws UnsupportedEncodingException{
        this.erro = new PrintStream(System.err, true, "UTF-8");
        this.id = id;
        this.tempo = 2;
    }
    
    //cpu recebe processo
    public int recebeProcesso(Processo p){
        if(p != null){
            this.p = p;
            return 0;
        }
        this.erro.println("Erro 1 (Disco.recebeProcesso): Não existe processo para ser adicionado.");
        return 1;
    }
    
    // Função que verifica se o processo terminou sua função no disco
    public boolean terminouExecucao(){
        if(!this.isOcioso()){
            if (this.tempoUtilizado == this.tempo){
                return true;
            }
        }
        return false;
    }
    
    // Função que incrementa o tempo do processo atual no disco
    public void incrementaTempo(){
        if(!this.isOcioso()){
            this.tempoUtilizado++;
        }
    }
    
    //verifica se disco está ocioso
    public boolean isOcioso(){
        if (this.p == null){
            return true;
        }
        return false;
    }
    
    //disco para a execução do processo atual e o envia para outro lugar
    public Object enviaProcesso(){
        if(!this.isOcioso()){   // O processo está no disco
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
    
    //retorna o processo no disco
    public Processo getProcesso(){
        return this.p;
    }
}