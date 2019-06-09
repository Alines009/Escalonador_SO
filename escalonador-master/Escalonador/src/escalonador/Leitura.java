/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package escalonador;

import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class Leitura {
    int id = 0;
    public ArrayList lerArquivos() throws Exception{
        ArrayList <Processo> novos = new ArrayList();
        FileReader arq = new FileReader("processos.txt"); //Lê os processos no arquivo
        BufferedReader lerArq = new BufferedReader(arq);
        String linha = lerArq.readLine();
        while (linha != null){
            Processo p = CriarProcessos(linha);
            novos.add(p);            
            linha = lerArq.readLine();
        }
        return novos;
    }
    
    public Processo CriarProcessos(String linha){
        String array[] = new String [6];
        array = linha.split(", ");
        Processo p = new Processo(id,Integer.parseInt(array[0]),Integer.parseInt(array[1]),Integer.parseInt(array[2]),Integer.parseInt(array[3]),Integer.parseInt(array[4]),Integer.parseInt(array[5]));
        id += 1;
        return p;
    }
}
