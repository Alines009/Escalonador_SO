
package escalonador;

import java.util.ArrayList;

public class Escalonador {
    
    public static void main(String[] args) throws Exception{
        int contador = 0;
        ArrayList<Processo> novos = new ArrayList();    //Cria lista de processos novos
        Leitura leitura = new Leitura();
        novos = leitura.lerArquivos();                  //Lendo o arquivo processos.txt
        
        // Cria Fila de Prontos
        FilaPrioridade fp = new FilaPrioridade();
        FilaComum fc = new FilaComum();
        
        // Cria as CPUs
        CPU cpu[] = {new CPU(0), new CPU(1), new CPU(2), new CPU(3)};
        // Cria a memória RAM
        RAM memoria = new RAM();
        
        // Cria as interfaces de entrada e saída, e com suas respectivas filas
        Impressora impressora[] = {new Impressora(0), new Impressora(1)};
        Disco disco[] = {new Disco(0), new Disco(1)};
        ArrayList<Processo> filaImpressora = new ArrayList();
        ArrayList<Processo> filaDisco = new ArrayList();
        
        boolean executando = true;
         
        System.out.println(Cores.ANSI_RED + "INICIANDO ESCALONADOR" + Cores.ANSI_RESET);
        System.out.println(Cores.ANSI_RED + "*********************" + Cores.ANSI_RESET);
        Thread.sleep (2000); 
        
        int flag;
        try {
            // Crio o desachante
            Despachante d = new Despachante();
            while(executando){
                
                status(contador, novos, cpu, impressora, disco, filaImpressora, 
                        filaDisco, memoria, fp, fc);
                // Verificar atividade do cpu
                for(int i = 0; i < cpu.length; i++){
                    if(cpu[i].terminouExecucao()){                              // O processo que está na cpu terminou sua eecução?
                        Processo p = (Processo) cpu[i].enviaProcesso();
                        
                        if((p.getPrinter() == 1) && (p.getTimePrinter() == 0)){ //O processo precisa usar a impressora?
                            filaImpressora.add(p);                              // Mando para a fila de espera da impressora
                        } 
                        else if((p.getDisc() == 1) && (p.getTimeDisc() == 0)){  //O processo precisa usar o disco?
                            filaDisco.add(p);                                   // Mando para a fila de espera de discos
                        }
                        else if(p.getPriority() != 0 && p.getTimeCPU() != 0){   // O processo não possui prioridade?
                            fc.recebeProcesso(p,memoria);                       // Mando de volta para o feedback
                        }else if(p.getTimeCPU() == 0){
                            memoria.desalocaProcesso(p);
                        }
                    }
                    
                }
                
                // Verificar atividade da impressora
                for(int i = 0; i < impressora.length; i++){
                    if(impressora[i].terminouExecucao()){                       // O processo que está na impressora terminou sua execução?
                        Processo p = (Processo) impressora[i].enviaProcesso();
                        p.setTimePrinter(-1);
                        p.setPrinter(0);
                        
                        if((p.getDisc() == 1) && (p.getTimeDisc() == 0)){       //O processo precisa usar o disco?
                            filaDisco.add(p);                                   // Mando para a fila de espera de discos
                        }
                        else if(p.getTimeCPU() != 0){   // O processo não terminou execução?
                            p.setPriority(1);           // Seto a prioridade para 1
                            p.setQtdExec(0);            // Seto a quantidade de execuções para 0
                            p.setArrivalTime(contador); //Seto o momento de chegada para o momento atual
                            novos.add(p);               // Mando de volta para a lista de novos
                        }
                    } else {
                        impressora[i].incrementaTempo();
                    }
                }
                
                // Verificar atividade do disco
                for(int i = 0; i < disco.length; i++){
                    if(disco[i].terminouExecucao()){                            // O processo que está no disco terminou sua execução?
                        Processo p = (Processo) disco[i].enviaProcesso();
                        p.setTimeDisc(-1);
                        p.setDisc(0);
                        
                        if((p.getPrinter() == 1) && (p.getTimePrinter() == 0)){ //O processo precisa usar a impressora?
                            filaImpressora.add(p);                              // Mando para a fila de espera de impressoras
                        }
                        else if(p.getTimeCPU() != 0){                           // O processo não terminou execução?
                            p.setPriority(1);                                   // Seto a prioridade para 1
                            p.setQtdExec(0);                                    // Seto a quantidade de execuções para 0
                            p.setArrivalTime(contador);                         //Seto o momento de chegada para o momento atual
                            novos.add(p);                                       // Mando de volta para a lista de novos
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
               
                Thread.sleep (1000); 

                contador +=1;
                // Verifica quais CPUs estão com processos. Caso verdadeiro, incrementa o contador de tempo da CPU
                for(int i = 0; i < cpu.length; i++){
                    if(!cpu[i].isOcioso()){
                        cpu[i].incrementaTempo();
                    }
                }
                    
            }
            status(contador, novos, cpu, impressora, disco, filaImpressora, 
                        filaDisco, memoria, fp, fc);
 
            System.out.println(Cores.ANSI_GREEN + "*********************" + Cores.ANSI_RESET);
            System.out.println(Cores.ANSI_GREEN + "ESCALONADOR TERMINADO" + Cores.ANSI_RESET);
            System.out.println(Cores.ANSI_GREEN + "*********************" + Cores.ANSI_RESET);
        } catch (InterruptedException ex) {}
        
    }
    
    public static boolean terminou(FilaPrioridade fp, FilaComum fc, ArrayList<Processo> novos, CPU cpu[], Impressora impressora[], Disco disco[], ArrayList<Processo> filaImpressora, ArrayList<Processo> filaDisco){
        // Se a fila de prioridades, a fila comum, a fila de novos, a fila da impressora e a fila de disco estão vazias
        if(fp.isVazia() && fc.isVazia() && novos.isEmpty() && filaImpressora.isEmpty() && filaDisco.isEmpty()){
            
            // Se alguma impressora não estiver ociosa, flag virará falso
            for(int j = 0; (j < cpu.length); j++){
                if(!cpu[j].isOcioso()){
                    return true;
                }
            }
            for(int j = 0; (j < impressora.length); j++){
                if(!impressora[j].isOciosa()){
                    return true;
                }
            }
            for(int j = 0; (j < disco.length); j++){
                if(!disco[j].isOcioso()){
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public static void status(int contador, ArrayList<Processo> novos, CPU cpu[],
            Impressora impressora[], Disco disco[], ArrayList<Processo> filaImpressora, ArrayList<Processo> filaDisco,
            RAM memoria, FilaPrioridade fp, FilaComum fc){
            //Imprime o tempo
                System.out.println(Cores.ANSI_BLUE + "Tempo: " + contador + Cores.ANSI_RESET);
                
                // Imprime a fila de novos
                System.out.println(Cores.ANSI_RED + "FILA DE NOVOS" + Cores.ANSI_RESET);
                for (int j = 0; j < novos.size(); j++){
                    System.out.println("    "+novos.get(j).toString());
                }
                System.out.println();
                
                 // Imprime os processos atualmente nas CPUs
                for (int j = 0; j < cpu.length; j++){
                    //System.out.println("CPU " + j);
                    if(cpu[j].getProcesso() != null){ 
                        System.out.println("CPU " + j + ": " + cpu[j].getProcesso().toString());
                    }else{
                        System.out.println(Cores.ANSI_PURPLE + "CPU " + j + ": CPU Ociosa" + Cores.ANSI_RESET);
                    }
                }
                System.out.println();
                
                // Imprime os processos na impressora
                for (int j = 0; j < impressora.length; j++){
                    
                    if(impressora[j].getProcesso() != null) {
                        System.out.println("IMPRESSORA " + j + ": " + impressora[j].getProcesso().toString());
                    }
                    else{
                        System.out.println(Cores.ANSI_PURPLE + "IMPRESSORA " + j + ": IMPRESSORA Ociosa" + Cores.ANSI_RESET);
                    }
                }
                System.out.println();
                
                // Imprime os processos que estão o disco
                for (int j = 0; j < disco.length; j++){
                    
                    if(disco[j].getProcesso() != null){
                        System.out.println("DISCO " + j + ": " + disco[j].getProcesso().toString());
                    }else{
                        System.out.println(Cores.ANSI_PURPLE + "DISCO " + j + ": DISCO Ocioso" + Cores.ANSI_RESET);
                    }
                }
                System.out.println();
                
                // Imprime a fila de impressora
                System.out.println(Cores.ANSI_RED + "FILA DE IMPRESSORA" + Cores.ANSI_RESET);
                if(filaImpressora.size() == 0){
                    System.out.println("    Fila Vazia");
                }else{
                    for (int j = 0; j < filaImpressora.size(); j++){
                        System.out.println("    " + filaImpressora.get(j).toString());
                    }
                }
                System.out.println();
                
                // Imprime a fila de disco
                System.out.println(Cores.ANSI_RED + "FILA DE DISCO" + Cores.ANSI_RESET);
                if(filaDisco.size() == 0){
                    System.out.println("    Fila Vazia");
                }else{
                    for (int j = 0; j < filaDisco.size(); j++){
                        System.out.println("    " + filaDisco.get(j).toString());
                    }
                }
                // Imprime as filas de Prioridade e Comum
                fp.ImprimePrioridade();
                fc.ImprimeComum();
                
                // Imprime os Processos alocados na RAM
                memoria.imprimeRAM();
                System.out.println("------------------------------------");
            
    }
        
}
