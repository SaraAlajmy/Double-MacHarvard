package main;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class Package2 {
    private static short[] instructionMemory = new short[1024];
    private static byte[] dataMemory = new byte[2048];
    private static byte[] registers = new byte[64];
    private static short pc = 0;
    private static byte SREG = 0;

    private static int starting = 0;

    public static void pipeline(){
        short[] instruction = {0,0};
        Vector<Object> values = new Vector<>();
        for(int cycle = 0; cycle<instructionMemory.length+2; cycle++, starting++){
            short[] tempInstruction = {0,0};
            Vector<Object> tempValues = new Vector<>();
            if(cycle<instructionMemory.length)
                tempInstruction = fetch();
            if(cycle<instructionMemory.length+1 && starting>0)
                tempValues = decode(instruction);
            if(starting>1)
                execute(((byte[])tempValues.get(0))[0], ((byte[])tempValues.get(0))[1], ((byte[])tempValues.get(0))[2], ((byte[])tempValues.get(0))[3], ((byte[])tempValues.get(0))[4], (short)tempValues.get(1));
            instruction = tempInstruction; values = tempValues;
        }
    }
    public static short[] fetch(){
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

//        for(int i = 0; i < instructionMemory.length; i++) {
            short instruction = instructionMemory[pc];
            pc++;
            return new short[]{instruction, pc};
//            decode(instruction);
//        }

    }

    public static Vector decode(short[] instruction){
        byte opcode = (byte) ((instruction[0] & 0b1111000000000000) >>> 12);  // bits 15:12
        byte R1 = (byte) ((instruction[0] & 0b0000111111000000) >>> 6); // 11:6
        byte inR1 = registers[R1];
        byte R2orImm= (byte) (instruction[0] & 0b0000000000111111); //6:0
        byte inR2 = registers[R2orImm];
        Vector<Object> result = new Vector<>();
        result.add(new byte[]{opcode, R1, R2orImm, inR1, inR2});
        result.add(instruction[1]);
        return result;
        // excute(opcode,R1, R2orImm, inR1, inR2);
    }

    public static void execute(byte opcode, short r1 ,short r2orImm, byte inR1, byte inR2, short pcOld){
        SREG = 0;
        switch (opcode){
            case 0: registers[r1] = add(inR1, inR2);break;
            case 1: registers[r1] = sub(inR1, inR2);break;
            case 2: registers[r1]=(byte)(inR1*inR2); updateNegAndZero(registers[r1]);break;
            case 3: registers[r1]=(byte)r2orImm;break;
            case 4: if(inR1==0){pc = (byte) (pcOld+r2orImm);starting = 0;}break;
            case 5: registers[r1] = (byte)(inR1 & inR2); updateNegAndZero(registers[r1]);break;
            case 6: registers[r1] = (byte)(inR1 | inR2); updateNegAndZero(registers[r1]);break;
            case 7: pc = concatenate(inR1, inR2); starting =0; break;
            case 8: registers[r1] = (byte)(((inR1 & 0xFF)<<r2orImm) | ((inR1 & 0xFF)>>>(8- r2orImm))); updateNegAndZero(registers[r1]);break;
            case 9: registers[r1] = (byte)(((inR1 & 0xFF)>>> r2orImm) | ((inR1 & 0xFF)<<(8- r2orImm))); updateNegAndZero(registers[r1]);break;
            case 10: registers[r1]=  dataMemory[r2orImm] ;break;
            case 11: dataMemory[r2orImm] =  inR1;break;
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
        pipeline();
//        registers[0] = (byte) -128;
//        registers[1] = (byte) -128;
//        execute((byte)0, (byte)0, (byte)1);
//        System.out.println("add:");
//        System.out.println(registers[0]);
//        System.out.println(Integer.toBinaryString(SREG));
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


