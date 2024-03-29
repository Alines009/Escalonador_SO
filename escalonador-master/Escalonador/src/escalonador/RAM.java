
package escalonador;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 *
 * @author Ricardo Monteiro
 */
public class RAM {
    private int espacoTotal;
    private int espacoAlocado;
    private ArrayList<Integer> fila;
    private PrintStream erro;
    private int quadroDaMP[];
    private int quadrosAlocados;
    
    public RAM() throws UnsupportedEncodingException{
        this.erro = new PrintStream(System.err, true, "UTF-8");
        this.espacoAlocado = 0;
        this.fila = new ArrayList<Integer>();
        this.espacoTotal = 16384; // 16GB em MB (MegaByte)   
        this.quadrosAlocados = 0;        //A MP eh dividida em 256 quadros cada uma de tamanho igual a 64MB  
        this.quadroDaMP = new int [256];
    }
    
    int alocaProcesso(Processo p){
        try{
            if(!this.fila.contains(p.getId())){
                if( (this.getQuadrosAlocados() + p.getQtdPaginas()) <= this.getQuadroDaMP().length){
                    int n = p.getQtdPaginas();
                    System.out.println("\n Qtd de páginas para o processo "+p.getId()+": "+p.getQtdPaginas());
                    int i = 0; //contador
                    ArrayList<Integer> quadrosUsados = new ArrayList();
                    while(n!=0){
                        if(this.getQuadroDaMP()[i] == 0){
                            this.getQuadroDaMP()[i] = p.getId();
                            quadrosUsados.add(i);
                            n--;
                        }
                        i++;
                    }
                    this.setQuadrosAlocados(this.getQuadrosAlocados() + p.getQtdPaginas());
                    //System.out.println("Página do Proc | Quadro da MP");
                    p.setTabelaDePaginas(quadrosUsados);
                    this.setEspacoAlocado(this.getEspacoAlocado() + p.getMemory());
                    this.getFila().add(p.getId());
                    return 0;
                }
                this.erro.println("Erro 3 (RAM.alocaProcesso): Não há espaço para o processo para ser alocado.");
                return 3;
            } 
            return 1;
        }catch(Exception e){
            this.erro.println("Erro 1 (RAM.alocaProcesso): Não existe processo para ser alocado.");
            return 1;
        }
    }
    
     int desalocaProcesso(Processo p){
        if( !this.fila.isEmpty()){
            if(this.getFila().contains(p.getId())){ //Se o processo P se encontra na MP
                ArrayList<Integer> quadrosUsados = p.getTabelaDePaginas();
                for(int j = 0; j< quadrosUsados.size() ;j++){ //Para cada pagina do processo
                    int i = quadrosUsados.get(j);   //Há a desalocacao da memoria
                    this.getQuadroDaMP()[i] = 0;
                }
                p.setTabelaDePaginas(null);
                this.quadrosAlocados -= p.getQtdPaginas();
                p.setQtdPaginas(0);
                this.setEspacoAlocado(this.getEspacoAlocado() - p.getMemory());
                int index = this.fila.indexOf(p.getId());
                this.fila.remove(index);
               

                
                
                return 0;
                
            }
            this.erro.println("Erro 1 (RAM.desalocaProcesso): Não existe processo para ser desalocado.");
            return 1;
        }
        this.erro.println("Erro 2 (RAM.desalocaProcesso): Fila Vazia.");
        return 2;
    }

 
    
    
    
    
    
    public int getEspacoTotal() {
        return espacoTotal;
    }

    /**
     * @param espacoTotal the espacoTotal to set
     */
    public void setEspacoTotal(int espacoTotal) {
        this.espacoTotal = espacoTotal;
    }

    /**
     * @return the espacoAlocado
     */
    public int getEspacoAlocado() {
        return espacoAlocado;
    }

    /**
     * @param espacoAlocado the espacoAlocado to set
     */
    public void setEspacoAlocado(int espacoAlocado) {
        this.espacoAlocado = espacoAlocado;
    }

    /**
     * @return the fila
     */
    public ArrayList<Integer> getFila() {
        return fila;
    }

    /**
     * @param fila the fila to set
     */
    public void setFila(ArrayList<Integer> fila) {
        this.fila = fila;
    }

    /**
     * @return the quadroDaMP
     */
    public int[] getQuadroDaMP() {
        return quadroDaMP;
    }

    /**
     * @param quadroDaMP the quadroDaMP to set
     */
    public void setQuadroDaMP(int[] quadroDaMP) {
        this.quadroDaMP = quadroDaMP;
    }

    /**
     * @return the quadrosAlocados
     */
    public int getQuadrosAlocados() {
        return quadrosAlocados;
    }

    /**
     * @param quadrosAlocados the quadrosAlocados to set
     */
    public void setQuadrosAlocados(int quadrosAlocados) {
        this.quadrosAlocados = quadrosAlocados;
    }
    
    public void imprimeRAM(){
        System.out.println(Cores.ANSI_RED + "MEMORIA RAM" + Cores.ANSI_RESET);
        System.out.println();
        System.out.println("Paginacao - ID Processo");
        for(int i = 0; i < (this.quadroDaMP.length/4); i++){
            System.out.println("Pagina: "+i+" - "+"ID Processo: "+this.quadroDaMP[i]+"       "+"Pagina: "+(64+i)+" - "+"ID Processo: "+this.quadroDaMP[64+i]+"       "+"Pagina: "+(128+i)+" - "+"ID Processo: "+this.quadroDaMP[128+i]
            +"       "+"Pagina:"+(192+i)+" - "+"ID Processo: "+this.quadroDaMP[192+i]);
        }
        System.out.println(Cores.ANSI_RED +"****************" + Cores.ANSI_RESET);
    }
}
