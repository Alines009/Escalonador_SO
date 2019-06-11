
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
        
        Impressora impressora[] = {new Impressora(0), new Impressora(1)};
        Disco disco[] = {new Disco(0), new Disco(1)};
        
        ArrayList<Processo> filaImpressora = new ArrayList();
        ArrayList<Processo> filaDisco = new ArrayList();
        
        boolean executando = true;
        RAM memoria = new RAM(); 
        
        fp.ImprimePrioridade();
        fc.ImprimeComum();
        int flag;
        try {
            Despachante d = new Despachante();
            while(executando){
                //Imprime o tempo e o processo atualmente nas CPUs
                System.out.println("Tempo: "+contador+"\n");
                System.out.println("NOVOS");
                for (int j = 0; j < novos.size(); j++){
                    System.out.println(novos.get(j).toString());
                }
                System.out.println();
                for (int j = 0; j < cpu.length; j++){
                    System.out.println("CPU " + j);
                    if(cpu[j].p != null) System.out.println(cpu[j].p.toString());
                }
                System.out.println();
                for (int j = 0; j < impressora.length; j++){
                    System.out.println("IMPRESSORA " + j);
                    if(impressora[j].p != null) System.out.println(impressora[j].p.toString());
                }
                System.out.println();
                for (int j = 0; j < disco.length; j++){
                    System.out.println("DISCO " + j);
                    if(disco[j].p != null) System.out.println(disco[j].p.toString());
                }
                System.out.println();
                System.out.println("FILA DE IMPRESSORA");
                for (int j = 0; j < filaImpressora.size(); j++){
                    System.out.println(filaImpressora.get(j).toString());
                }
                System.out.println();
                System.out.println("FILA DE DISCO");
                for (int j = 0; j < filaDisco.size(); j++){
                    System.out.println(filaDisco.get(j).toString());
                }
                System.out.println("------------------------------------");
                
                // Verificar atividade do cpu
                for(int i = 0; i < cpu.length; i++){
                    if(cpu[i].terminouExecucao()){  // O processo que está na cpu terminou sua eecução?
                        Processo p = (Processo) cpu[i].enviaProcesso();
                        
                        if((p.getPrinter() == 1) && (p.getTimePrinter() == 0)){   //O processo precisa usar a impressora?
                            filaImpressora.add(p);   // Mando para a fila de espera da impressora
                        } 
                        else if((p.getDisc() == 1) && (p.getTimeDisc() == 0)){   //O processo precisa usar o disco?
                            filaDisco.add(p);   // Mando para a fila de espera de discos
                        }
                        else if(p.getPriority() != 0 && p.getTimeCPU() != 0){   // O processo não possui prioridade?
                            fc.recebeProcesso(p,memoria);   // Mando de volta para o feedback
                        }
                    }
                    //if(cpu[i].)
                }
                
                // Verificar atividade da impressora
                for(int i = 0; i < impressora.length; i++){
                    if(impressora[i].terminouExecucao()){  // O processo que está na impressora terminou sua execução?
                        Processo p = (Processo) impressora[i].enviaProcesso();
                        p.setTimePrinter(-1);
                        p.setPrinter(0);
                        
                        if((p.getDisc() == 1) && (p.getTimeDisc() == 0)){   //O processo precisa usar o disco?
                            filaDisco.add(p);   // Mando para a fila de espera de discos
                        }
                        else if(p.getTimeCPU() != 0){   // O processo não terminou execução?
                            p.setPriority(1);           // Seto a prioridade para 1
                            p.setQtdExec(0);            // Seto a quantidade de execuções para 0
                            p.setArrivalTime(contador); //Seto o momento de chegada para o momento atual
                            novos.add(p);   // Mando de volta para a lista de novos
                        }
                    } else {
                        impressora[i].incrementaTempo();
                    }
                }
                
                // Verificar atividade do disco
                for(int i = 0; i < disco.length; i++){
                    if(disco[i].terminouExecucao()){  // O processo que está no disco terminou sua execução?
                        Processo p = (Processo) disco[i].enviaProcesso();
                        p.setTimeDisc(-1);
                        p.setDisc(0);
                        
                        if((p.getPrinter() == 1) && (p.getTimePrinter() == 0)){   //O processo precisa usar a impressora?
                            filaImpressora.add(p);   // Mando para a fila de espera de impressoras
                        }
                        else if(p.getTimeCPU() != 0){   // O processo não terminou execução?
                            p.setPriority(1);           // Seto a prioridade para 1
                            p.setQtdExec(0);            // Seto a quantidade de execuções para 0
                            p.setArrivalTime(contador); //Seto o momento de chegada para o momento atual
                            novos.add(p);   // Mando de volta para a lista de novos
                        }
                    } else {
                        disco[i].incrementaTempo();
                    }
                }
                
                if(contador%2==0){
                    int i = 0;
                    while(i < novos.size()){
                        flag = d.Despachar(novos.get(i), contador, fp, fc,memoria);
                        if(flag == 0){
                            novos.remove(i);
                        }else{
                            i += 1;
                        }
                    }
                    executando = terminou(fp,fc,novos,cpu,impressora,disco,filaImpressora,filaDisco);
                }
                
                //Existe algum processo para executar nas impressoras?
                if(!filaImpressora.isEmpty()){
                    // Faço uma verificação se todas estão ocupadas
                    ArrayList<Boolean> ocupacoes = new ArrayList<Boolean>();
                    for(int j = 0; j < impressora.length; j++){
                        ocupacoes.add(impressora[j].isOciosa());
                    }
                    // Enquanto existir impressora livre e a fila de impressão está com processos? Se sim, envia processo para a impressora
                    while(ocupacoes.contains(true) && (!filaImpressora.isEmpty())){
                        Processo p = filaImpressora.remove(0);
                        int impressoraDisponivel = ocupacoes.indexOf(true);
                        impressora[impressoraDisponivel].recebeProcesso(p);
                        ocupacoes.set(impressoraDisponivel, false);
                    }
                }
                
                //Existe algum processo para executar nos discos?
                if(!filaDisco.isEmpty()){
                    // Faço uma verificação se todos estão ocupados
                    ArrayList<Boolean> ocupacoes = new ArrayList<Boolean>();
                    for(int j = 0; j < disco.length; j++){
                        ocupacoes.add(disco[j].isOcioso());
                    }
                    // Enquanto existir disco livre e a fila de discos está com processos? Se sim, envia processo para o disco
                    while(ocupacoes.contains(true) && (!filaDisco.isEmpty())){
                        Processo p = filaDisco.remove(0);
                        int discoDisponivel = ocupacoes.indexOf(true);
                        disco[discoDisponivel].recebeProcesso(p);
                        ocupacoes.set(discoDisponivel, false);
                    }
                }
                
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
                            prioridades.set(cpuDisponivel, p.getPriority());
                        }else{
                            if ((p.getPrinter() == 1) && (p.getTimePrinter() == 0)){
                                filaImpressora.add(p);
                            }
                            else if ((p.getDisc() == 1) && (p.getTimeDisc() == 0)){
                                filaDisco.add(p);
                            }
                            else {
                                cpu[cpuDisponivel].recebeProcesso(p, 2);
                                prioridades.set(cpuDisponivel, p.getPriority());
                            }
                        }
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
                        
                    //}
                Thread.sleep (1000); 
            
        
                //System.out.print("\nTempo: "+contador);
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
    
    public static boolean terminou(FilaPrioridade fp, FilaComum fc, ArrayList<Processo> novos, CPU cpu[], Impressora impressora[], Disco disco[], ArrayList<Processo> filaImpressora, ArrayList<Processo> filaDisco){
        // Se a fila de prioridades, a fila comum, a fila de novos, a fila da impressora e a fila de disco estão vazias
        if(fp.isVazia() && fc.isVazia() && novos.isEmpty() && filaImpressora.isEmpty() && filaDisco.isEmpty()){
            boolean flag = true;
            // Se alguma impressora não estiver ociosa, flag virará falso
            for(int j = 0; (j < cpu.length) && flag ; j++){
                if(!cpu[j].isOcioso()){
                    flag = false;
                }
            }
            for(int j = 0; (j < impressora.length) && flag ; j++){
                if(!impressora[j].isOciosa()){
                    flag = false;
                }
            }
            for(int j = 0; (j < disco.length) && flag ; j++){
                if(!disco[j].isOcioso()){
                    flag = false;
                }
            }
            return !flag;
        }
        return true;
    }
        
}
