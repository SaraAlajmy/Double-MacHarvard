package main;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Package2 {
    private static short[] instructionMemory = new short[1024];
    private static byte[] dataMemory = new byte[2048];
    private static byte[] registers = new byte[64];
    private static short pc = 0;
    private static byte SREG = 0;

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
        short immediate=0;//6:0
        short opcode = (short) ((instruction & 0b1111000000000000) >>> 12);  // bits 15:12
        short R1 = (short) ((instruction & 0b0000111111000000) >>> 6); // 11:6
        short R2orImm= (short) (instruction & 0b0000000000111111); //6:0
        execute(opcode, R1, R2orImm);
    }

    public static void execute(short opcode, short r1 ,short r2orImm){
        SREG = 0;
        switch (opcode){
            case 0: registers[r1] = add(registers[r1], registers[r2orImm]);break;
            case 1: registers[r1] = sub(registers[r1], registers[r2orImm]);break;
            case 2: registers[r1]=(byte)(registers[r1]*registers[r2orImm]); updateNegAndZero(registers[r1]);break;
            case 3: registers[r1]=(byte)r2orImm;break;
            case 4: pc=(registers[r1]==0)?(byte) (pc+1+r2orImm):pc;break;
            case 5: registers[r1] = (byte)(registers[r1] &registers[r2orImm]); updateNegAndZero(registers[r1]);break;
            case 6: registers[r1] = (byte)(registers[r1] | registers[r2orImm]); updateNegAndZero(registers[r1]);break;
            case 7: pc=  concatenate(registers[r1],registers[r2orImm]);break;
            case 8: registers[r1] = (byte)(((registers[r1] & 0xFF)<<r2orImm) | ((registers[r1] & 0xFF)>>>(8- r2orImm))); updateNegAndZero(registers[r1]);break;
            case 9: registers[r1] = (byte)(((registers[r1] & 0xFF)>>> r2orImm) | ((registers[r1] & 0xFF)<<(8- r2orImm))); updateNegAndZero(registers[r1]);break;
            case 10: registers[r1]=  dataMemory[r2orImm] ;break;
            case 11: dataMemory[r2orImm] =  registers[r1];break;
        }
    }

    private static void updateCarry(byte a, byte b, short result){
        int carry = ((result & 0b0000000100000000) >>> 8);
        if(carry==1)
            SREG = (byte)(SREG | 0b00010000);
    }

    private static void updateValid(byte a, byte b, short result){
        byte bit6Carry = (byte)(((a&0b01111111) + (b&0b01111111))>>>7);
      //  bit6Carry = (byte)((((a&0b01000000)>>>6) + ((b&0b01000000)>>>6))>>1);
      //  byte bit7Carry = (byte)((((a&0b10000000)>>>7) + ((b&0b10000000)>>>7) + bit6Carry)>>1 );
        byte bit7Carry = (byte)((result & 0b0000000100000000) >>> 8);
        if((bit6Carry^bit7Carry) == 1)
            SREG = (byte)(SREG | 0b00001000);
    }

    private static void updateNegAndZero(byte result){
        if(result<0)
            SREG = (byte)(SREG | 0b00000100);
        if(result == 0)
            SREG = (byte)(SREG | 0b00000001);
    }

    private static void updateSign(){
        int neg = (SREG & 0b00000100) >>> 2;
        int valid = (SREG & 0b00001000) >>> 3;
        SREG = (byte)(SREG|((neg^valid) << 1));
    }

    public static byte add(byte a, byte b){
        short result = (short)(a + b);
        updateCarry(a, b, result);
        updateValid(a, b, result);
        updateNegAndZero((byte) result);
        updateSign();
        return (byte)result;
    }

    public static byte sub(byte a, byte b){
        short result = (short)(a - b);
        updateValid(a, b, result);
        updateNegAndZero((byte) result);
        updateSign();
        return (byte)result;
    }


    public static short concatenate(byte a , byte b){
        return (short) ((a<<8)|b);
    }

    public static void main (String[]args){
        registers[0] = (byte) -128;
        registers[1] = (byte) -128;
        execute((byte)0, (byte)0, (byte)1);
        System.out.println("add:");
        System.out.println(registers[0]);
        System.out.println(Integer.toBinaryString(SREG));
//        registers[0] = (byte) -127; //11111101
//        registers[1] = (byte) 64;
//        dataMemory[0] = (byte) 4;
//        execute((byte)8, (byte)0, (byte)1);
//        System.out.println("slc:");
//        System.out.println(registers[0]);
//        System.out.println(Integer.toBinaryString(SREG));
//        registers[0] = (byte) -127; // 11000000
//        registers[1] = (byte) 64;
//        execute((byte)9, (byte)0, (byte)1);
//        System.out.println("src:");
//        System.out.println(registers[0]);
//        System.out.println(Integer.toBinaryString(SREG));
//        registers[0] = (byte) -64;
//        registers[1] = (byte) 64;
//        execute((byte)10, (byte)0, (byte)0);
//        System.out.println("lb:");
//        System.out.println(registers[0]);
//        System.out.println(Integer.toBinaryString(SREG));
//        registers[0] = (byte) -64;
//        registers[1] = (byte) 64;
//        execute((byte)11, (byte)0, (byte)0);
//        System.out.println("sb:");
//        System.out.println(dataMemory[0]);
//        System.out.println(Integer.toBinaryString(SREG));

    }
}


