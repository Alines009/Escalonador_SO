
package escalonador;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 *
 * @author Ricardo Monteiro
 */
public class FilaComum {
    private ArrayList<ArrayList<Processo>> filaComum;   // lista de filas de processos sem prioridade
    private PrintStream erro;
    
    //inicia fila sem prioridade
    public FilaComum() throws UnsupportedEncodingException{
        this.erro = new PrintStream(System.err, true, "UTF-8");
        this.filaComum = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            this.filaComum.add(new ArrayList<Processo>());
        }
    }
    
    public int processoFinalizou(Processo p){
        if(p.getTimeCPU() == 0){
            return 20; 
        }else if(p.getTimeCPU() != 0){
            return 10; 
        }  
        this.erro.println("Erro 1 (FilaComum.processoFinalizou): Não existe processo para comparar o tempo.");
        return 1;
    }
    
    //recebe o processo na fila
    public int recebeProcesso(Processo p, RAM memoria){
            if(p.getQtdExec() == 0){ //Se o processo nunca foi executado
                memoria.alocaProcesso(p);
                ArrayList<Processo> a = this.filaComum.get(0); 
                a.add(p);
                this.filaComum.set(0, a);
                return 0;
            }else if(p.getQtdExec() == 1){ //Se o processo foi executado apenas uma vez
                ArrayList<Processo> a = this.filaComum.get(1); 
                a.add(p);
                this.filaComum.set(1, a);
                return 0;
            }else if (p.getQtdExec() >= 2){ //Se o processo foi executado mais de uma vez
                ArrayList<Processo> a = this.filaComum.get(2); 
                a.add(p);
                this.filaComum.set(2, a);
                return 0;
            }

            this.erro.println("Erro 1 (FilaComum.recebeProcesso): Não existe processo para ser adicionado.");
            return 1;
        
    }
    
    // retorna o primeiro processo na fila e o remove da fila
    public Object enviaProcesso(){
        ArrayList<Processo> a;
        for(int i = 0; i < 3; i++){
            a =  this.filaComum.get(i);
            if(!a.isEmpty()){
                Processo p = a.remove(0);
                this.filaComum.set(i,a);
                return p;
            }
        }
        this.erro.println("Erro 2 (FilaComum.enviaProcesso): Fila Vazia.");
        return 2;
    }
    
    //Imprime processos na fila
    public void ImprimeComum(){
        System.out.println(Cores.ANSI_RED + "FILA COMUM" + Cores.ANSI_RESET);
        for(int i = 0; i< this.filaComum.size(); i++){
            System.out.println(Cores.ANSI_GREEN + "    FILA "+i+":"+ Cores.ANSI_RESET);
            if(this.filaComum.get(i).size() == 0){
                System.out.println("        Fila Vazia");
            }else{
                for(int j = 0; j < this.filaComum.get(i).size(); j++){
                    System.out.println("        "+this.filaComum.get(i).get(j).toString());
                }
            }
        }
    }
    
    //retorna se fila está vazia
    public boolean isVazia(){
        for(int i = 0; i < this.filaComum.size(); i++){
            if(this.filaComum.get(i).size() != 0){
                return false;
            }
        }
        return true;
    }
}
