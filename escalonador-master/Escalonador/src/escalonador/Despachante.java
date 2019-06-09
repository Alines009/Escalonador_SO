package escalonador;

import java.util.ArrayList;

public class Despachante {
    public int Despachar(Processo p, int quantum, FilaPrioridade fp, FilaComum fc, RAM memoria){
        int t;
        int arrival = p.getArrivalTime(); //Recupera o tempo de chegada do processo
        if(arrival <= quantum){
            int prioridade = p.getPriority();
            if(prioridade == 0){
                t = fp.recebeProcesso(p,memoria);
                
            } else {
                t = fc.recebeProcesso(p,memoria);
            }
            if(t == 0){
                return 0;
            }
        }
        return 11;   // O processo ainda não está pronto
    }

}
