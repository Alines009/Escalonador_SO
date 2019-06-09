
package escalonador;

import java.util.ArrayList;

public class Escalonador {
    
    public static void main(String[] args) throws Exception{
        int contador = 0;
        ArrayList<Processo> novos = new ArrayList(); //Cria lista de processos novos
        Leitura leitura = new Leitura();
        novos = leitura.lerArquivos();//Lendo o arquivo processos.txt
        
        FilaPrioridade fp = new FilaPrioridade();
        FilaComum fc = new FilaComum();
        
        CPU cpu[] = {new CPU(0), new CPU(1), new CPU(2), new CPU(3)};
        
        boolean executando = true;
        RAM memoria = new RAM(); 
        
        fp.ImprimePrioridade();
        fc.ImprimeComum();
        int flag;
        try {
            Despachante d = new Despachante();
            while(executando){
                if(contador%2==0){
                    // Verficar atividade do cpu
                    for(int i = 0; i < cpu.length; i++){
                        if(cpu[i].terminouExecucao()){  // O processo que está na cpu terminou sua eecução?
                            Processo p = (Processo) cpu[i].enviaProcesso();
                            
                            if(p.getPriority() != 0 && p.getTimeCPU() != 0){   // O processo não possui prioridade?
                                fc.recebeProcesso(p,memoria);   // Mando de volta para o feedback
                            }
                        }
                    }
                    int i = 0;
                    while(i < novos.size()){
                        flag = d.Despachar(novos.get(i), contador, fp, fc,memoria);
                        if(flag == 0){
                            novos.remove(i);
                        }else{
                            i += 1;
                        }
                    }
                    executando = terminou(fp,fc,novos,cpu);
                    // Existe algum processo para executar na CPU?
                    if((!fp.isVazia()) || (!fc.isVazia())){
                        // Faço uma verificação se todos estão ocupados e sua prioridade
                        ArrayList<Integer> prioridades = new ArrayList<Integer>();
                        for(int j = 0; j < cpu.length; j++){
                            prioridades.add(cpu[j].getPrioridadeProcesso());
                        }
                        // Enquanto existir processador livre e alguma das filas estão com processos? Se sim, envia processo para a CPU
                        while(prioridades.contains(-1) && ((!fp.isVazia()) || (!fc.isVazia())) ){
                            Processo p;
                            // Existe processo na fila de prioridade para executar na CPU?
                            if(!fp.isVazia()){
                                p = (Processo) fp.enviaProcesso();
                            
                            // Existe processo na fila comum para executar na CPU?
                            }else{
                                p = (Processo) fc.enviaProcesso();
                            }
                            int cpuDisponivel = prioridades.indexOf(-1);
                            if(p.getPriority() == 0){
                                cpu[cpuDisponivel].recebeProcesso(p);
                            }else{
                                cpu[cpuDisponivel].recebeProcesso(p, 2);
                            }
                            prioridades.set(cpuDisponivel, p.getPriority());
                            
                        }
                    }
                    /*
                    // Se a fila de prioridades está vazia vá para a fila comum
                    if(fp.isVazia()){
                        // Se a fila comum e a fila de novos está também vazia verifica-se as CPUs  
                        if(fc.isVazia()){
                            // Verifico se os CPUs estão ociosos, se sim encerra-se o escalonador
                            if(novos.isEmpty()){
                                boolean flag2 = true;
                                for(int j = 0; (j < cpu.length) && flag2 ; j++){
                                    if(!cpu[j].isOcioso()){
                                        flag2 = false;
                                    }
                                }
                                if(!flag2){
                                    executando = false;
                                }
                            }
                        }else{
                            // Faço uma verificação se todos estão ocupados e sua prioridade
                            ArrayList<Integer> prioridades = new ArrayList<Integer>();
                            for(int j = 0; j < cpu.length; j++){
                                prioridades.add(cpu[j].getPrioridadeProcesso());
                            }
                            // Existe processador livre? Se sim, envia processo para a CPU
                            if(prioridades.contains(-1)){
                                int cpuDisponivel = prioridades.indexOf(-1);
                                cpu[cpuDisponivel].recebeProcesso((Processo) fc.enviaProcesso(), 2);
                            }
                        }*/
                        
                    }
                Thread.sleep (1000); 
            
        
                System.out.print("\nTempo: "+contador);
                contador +=1;
                for(int i = 0; i < cpu.length; i++){
                    if(!cpu[i].isOcioso()){
                        cpu[i].incrementaTempo();
                    }
                }
                    
            }
 
       //     fp.ImprimePrioridade();
         //   fc.ImprimeComum();
        } catch (InterruptedException ex) {}
        
    }
    
    public static boolean terminou(FilaPrioridade fp, FilaComum fc, ArrayList<Processo> novos, CPU cpu[]){
        // Se a fila de prioridades está vazia vá para a fila comum
        if(fp.isVazia()){
            // Se a fila comum e a fila de novos está também vazia verifica-se as CPUs  
            if(fc.isVazia()){
                // Verifico se os CPUs estão ociosos, se sim encerra-se o escalonador
                if(novos.isEmpty()){
                    boolean flag = true;
                    for(int j = 0; (j < cpu.length) && flag ; j++){
                        if(!cpu[j].isOcioso()){
                            flag = false;
                        }
                    }
                    if(flag){
                        return false;
                    }
                    return true;
                }
            }
        }
        return true;
    }
        
}
