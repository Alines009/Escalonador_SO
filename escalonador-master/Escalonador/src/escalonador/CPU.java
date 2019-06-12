
package escalonador;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class CPU {
    private int id;                 // Identificador da cpu
    private Processo p;             // Processo atualmente na cpu
    private int tempoUtilizacao;    // Tempo que o processo atual precisa ficar na cpu
    private int tempoTotal;         // Tempo total em que o processo atual está na cpu
    private PrintStream erro;
    
    //Inicia cpu
    public CPU(int id) throws UnsupportedEncodingException{ 
        this.erro = new PrintStream(System.err, true, "UTF-8");
        this.id = id;
        this.p = null;
        this.tempoTotal = 0;
        this.tempoUtilizacao = 0;
    }
    
    //cpu recebe processo com prioridade
    public int recebeProcesso(Processo p){
        if(p != null){
            this.p = p;
            this.tempoUtilizacao = p.getTimeCPU();  //cpu executará processo até o final
            return 0;
        }
        this.erro.println("Erro 1 (CPU.recebeProcesso): Não existe processo para ser adicionado.");
        return 1;        
    }
    
    //cpu recebe processo sem prioridade
    public int recebeProcesso(Processo p, int t){
        if(p != null){
            this.p = p;
            
            if((p.getPrinter() == 1) && (p.getDisc() == 1)){    // caso processo precise de impressora e disco
                if ((p.getTimePrinter() <= p.getTimeDisc()) && (p.getTimePrinter() <= t)) this.tempoUtilizacao = p.getTimePrinter();    //caso processo necessite da impressora antes ou ao mesmo tempo do disco, e antes ou ao mesmo tempo de acabar o quantum, p será executado até o momento que necessitar da impressora
                else if ((p.getTimeDisc() < p.getTimePrinter()) && (p.getTimeDisc() <= t)) this.tempoUtilizacao = p.getTimeDisc();      //caso processo necessite do disco antes da impressora, e antes ou ao mesmo tempo de acabar o quantum, p será executado até o momento que necessitar do disco
                else this.tempoUtilizacao = t;                                                                                          //caso contrario, p será executado até o fim do quantum
            } else if ((p.getPrinter() == 1) && (p.getTimePrinter() <= t)){ this.tempoUtilizacao = p.getTimePrinter();  // caso processo precise somente de impressora, e antes ou ao mesmo tempo de acabar o quantum, p será executado até o momento que necessitar da impressora
            } else if ((p.getDisc() == 1) && (p.getTimeDisc() <= t)){ this.tempoUtilizacao = p.getTimeDisc();           // caso processo precise somente de disco, e antes ou ao mesmo tempo de acabar o quantum, p será executado até o momento que necessitar do disco
            } else if(p.getTimeCPU() < t){      //caso o tempo necessário para findar o processo seja menor que o quantum, p será executado até o fim
                this.tempoUtilizacao = p.getTimeCPU();
            }else{                              //caso contrario, p será executado até o fim do quantum
                this.tempoUtilizacao = t;
            }
            return 0;
            
        }
        this.erro.println("Erro 1 (CPU.recebeProcesso): Não existe processo para ser adicionado.");
        return 1;
    }
    
    //cpu para a execução do processo atual e o envia para outro lugar
    public Object enviaProcesso(){
        if(!this.isOcioso()){   // O processo está na cpu
            // Modifico o tempo restante do uso de CPU
            this.p.setTimeCPU(
                (this.p.getTimeCPU() - this.tempoUtilizacao));
                
            // Acrescento +1 na quatidade de execuções   
            this.p.setQtdExec(
                (this.p.getQtdExec() + 1));
            
            // Modifico o tempo restante até o pedido de uso do disco, caso exista
            if (this.p.getDisc() == 1) this.p.setTimeDisc(this.p.getTimeDisc() - this.tempoUtilizacao);
            
            // Modifico o tempo restante até o pedido de uso da impressora, caso exista
            if (this.p.getPrinter() == 1) this.p.setTimePrinter(this.p.getTimePrinter() - this.tempoUtilizacao);
            
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
    
    //verifica se cpu está ociosa
    public boolean isOcioso(){
        if (this.p == null){
            return true;
        }
        return false;
    }
    
    //envia a prioridade do processo atualmente na cpu, e -1 caso não haja processo na cpu
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
    
    // Função que verifica que o processo atingiu o tempo limite
    public boolean terminouExecucao(){
        if(!this.isOcioso()){
            if (this.tempoTotal == this.tempoUtilizacao){
                return true;
            }
        }
        return false;
    }
    
    //retorna o processo na cpu
    public Processo getProcesso(){
        return this.p;
    }
}
