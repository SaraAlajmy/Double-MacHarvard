import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Package2 {
    private static short[] instructionMemory = new short[1024];
    private static byte[] dataMemory = new byte[2048];
    private static byte[] registers = new byte[64];
    private static short pc = 0;
    private byte SREG = 0;


    public void fetch(){
// read from a txt file?
        try {
            BufferedReader br = new BufferedReader(new FileReader("input.txt"));
            String assemblyline;
            while (( assemblyline= br.readLine() ) != null){


            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for(int i = 0; i < instructionMemory.length; i++) {
            short instruction = instructionMemory[pc];
            decode(instruction);
            pc++;
        }

    }

    public void decode(short instruction){
        // Part of the decode stage....

        short opcode = 0;    // bits 15:12


        // Implement the Sign Extend and Control Unit here:
        short temp =0;
        short R1 =0; // 11:6
        short R2 =0 ;//6:0
        short immediate=0;//6:0
        temp = (short) (instruction & 0b1111000000000000);
        opcode = (short) (temp >>> 12) ;

        temp = (short) (instruction & 0b0000111111000000);
        R1 = (short) (temp >>> 6);
        temp = (short) (instruction & 0b0000000000111111);
        R2=temp;
        immediate=temp;
        execute(opcode, R1,R2,immediate);

    }

    public static void execute(short opcode, short r1 ,short r2,short immediate){
        switch (opcode){
            case 0:registers[r1] = (byte) (registers[r1]+ registers[r2]);break;
            case 1: registers[r1] = (byte)(registers[r1]+ registers[r2]);break;
            case 2: registers[r1]=(byte)(registers[r1]* registers[r2]);break;
            case 3: registers[r1]=(byte)(registers[immediate]);break;
            case 4:  pc=(registers[r1]==0)?(byte) (pc+1+registers[immediate]):pc;break;
            case 5:  registers[r1] = (byte) (registers[r1] &registers[r2]);break;
            case 6 :registers[r1] = (byte) (registers[r1] | registers[r2]);break;
            case 7 :pc=  concatenate(registers[r1],registers[r2]);break;
            case 8 :registers[r1] = (byte) (registers[r1]<<immediate |registers[r1]>>>(8- immediate));break;
            case 9 :registers[r1] = (byte) (registers[r1]>>> immediate| registers[r1]<<(8- immediate));break;
            case 10: registers[r1]=  dataMemory[immediate] ;break;
            case 11:  dataMemory[immediate] =  registers[r1];break;
        }

    }
    public static short concatenate(byte a , byte b){
        short temp;

        temp=(short) (a|0b0000000000000000);
        temp=(short) (temp << 8);
        temp=(short) (temp|b);
        return temp;



    }

    public static void main (String[]args){
        registers[0]=0b00000100;
        registers[1]=0b00001100;
        execute((short)7,(short)0,(short)1,(short)1)  ;
        System.out.print(pc);




    }
}


