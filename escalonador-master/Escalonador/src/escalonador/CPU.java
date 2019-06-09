
package escalonador;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class CPU {
    private int id;
    private Processo p;
    private int tempoUtilizacao;
    private int tempoTotal;         // Variavel que verifica o tempo total em que o processo atual está na cpu
    private PrintStream erro;
    
    public CPU(int id) throws UnsupportedEncodingException{
        this.erro = new PrintStream(System.err, true, "UTF-8");
        this.id = id;
        this.p = null;
        this.tempoTotal = 0;
        this.tempoUtilizacao = 0;
    }
    
    public int recebeProcesso(Processo p){
        if(p != null){
            this.p = p;
            this.tempoUtilizacao = p.getTimeCPU();
            return 0;
        }
        this.erro.println("Erro 1 (CPU.recebeProcesso): Não existe processo para ser adicionado.");
        return 1;        
    }
    
    public int recebeProcesso(Processo p, int t){
        if(p != null){
            this.p = p;
            if(p.getTimeCPU() < t){
                this.tempoUtilizacao = p.getTimeCPU();
            }else{
                this.tempoUtilizacao = t;
            }
            return 0;
        }
        this.erro.println("Erro 1 (CPU.recebeProcesso): Não existe processo para ser adicionado.");
        return 1;
    }
    
    public Object enviaProcesso(){
        if(!this.isOcioso()){   // O processo está na cpu
            // Modifico o tempo restante do uso de CPU
            this.p.setTimeCPU(
                (this.p.getTimeCPU() - this.tempoUtilizacao));
                
            // Acrescento +1 na quatidade de execuções   
            this.p.setQtdExec(
                (this.p.getQtdExec() + 1)
            );
            
            Processo p = this.p;
            // "Reseto" a cpu
            this.p = null;
            this.tempoTotal = 0;
            this.tempoUtilizacao = 0;
            
            // envio p
            return p;
        }
        this.erro.println("Erro 2 (CPU.enviaProcesso): Não existe processo para ser enviado.");
        return 2;
        
    }
    
    public boolean isOcioso(){
        if (this.p == null){
            return true;
        }
        return false;
    }
    
    public int getPrioridadeProcesso(){
        if(p == null){
            return -1;
        }
        return p.getPriority();
    }
    
    // Função que incrementa o tempo total
    public void incrementaTempo(){
        this.tempoTotal += 1;
    }
    
    // Função que verifica que o tempo o processo atingiu o tempo limite
    public boolean terminouExecucao(){
        if(!this.isOcioso()){
            if (this.tempoTotal == this.tempoUtilizacao){
                return true;
            }
        }
        return false;
    }
}
