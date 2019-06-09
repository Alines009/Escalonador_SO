
package escalonador;

import java.util.ArrayList;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class FilaPrioridade {
    private ArrayList<Processo> filaPrioridade;
    private PrintStream erro;
    
    public FilaPrioridade() throws UnsupportedEncodingException{
        this.erro = new PrintStream(System.err, true, "UTF-8");
        this.filaPrioridade = new ArrayList<Processo>();
    }
    
    public int processoFinalizou(Processo p){
        if(p.getTimeCPU() == 0){
            return 20;            
        }else if(p.getTimeCPU() != 0){
            return 10;      
        }  
        this.erro.println("Erro 1 (FilaPrioridade.processoFinalizou): Não existe processo para comparar o tempo.");
        return 1;     
    }
    
    public int recebeProcesso(Processo p, RAM memoria){
        try{
            memoria.alocaProcesso(p);
            this.filaPrioridade.add(p);
            return 0;
        }catch(Exception e){
            return 1;
        }
    }
    
    public Object enviaProcesso(){
        try{
            return this.filaPrioridade.remove(0);
        }catch(Exception e){
            this.erro.println("Erro 2 (FilaPrioridade.enviaProcesso): Fila Vazia.");
            return 2;
        }
    }
    
    public void ImprimePrioridade(){
        if(this.filaPrioridade== null){
            System.out.println("Não há processos nesta fila");
        } else {
        System.out.println("PROCESSOS EM FILA DE PRIORIDADE");
        for(int i = 0; i< this.filaPrioridade.size(); i++){
            System.out.println(this.filaPrioridade.get(i).toString());
        }
        }
    }
    
    public boolean isVazia(){
        return (this.filaPrioridade.size() == 0)? true: false;
    }
}
