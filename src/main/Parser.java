package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {

    private ArrayList<Instruction> instructions = new ArrayList<Instruction>();
    private ArrayList<String> binaryInstructions = new ArrayList<>();
    public Parser() {
        readFromTextFileToInstructionList();
    }

    public ArrayList<Instruction> getGeneratedInstructions() {
        return instructions;
    }
    public ArrayList<String> getBinaryInstructions() {
        return binaryInstructions;
    }

    public void readFromTextFileToInstructionList() {
        // function to read from a text file named input.txt in the src folder
        String filePath = "input.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");

                //set instruction type
                Instruction instruction;
                if(tokens[0].equalsIgnoreCase("add")){
                    instruction = new Instruction(InstType.ADD);
                } else if(tokens[0].equalsIgnoreCase("sub")){
                    instruction = new Instruction(InstType.SUB);
                } else if(tokens[0].equalsIgnoreCase("mul")){
                    instruction = new Instruction(InstType.MUL);
                } else if(tokens[0].equalsIgnoreCase("ldi")){
                    instruction = new Instruction(InstType.LDI);
                } else if(tokens[0].equalsIgnoreCase("beqz")){
                    instruction = new Instruction(InstType.BEQZ);
                } else if(tokens[0].equalsIgnoreCase("and")){
                    instruction = new Instruction(InstType.AND);
                } else if(tokens[0].equalsIgnoreCase("or")){
                    instruction = new Instruction(InstType.OR);
                } else if(tokens[0].equalsIgnoreCase("jr")){
                    instruction = new Instruction(InstType.JR);
                } else if(tokens[0].equalsIgnoreCase("slc")){
                    instruction = new Instruction(InstType.SLC);
                } else if(tokens[0].equalsIgnoreCase("src")){
                    instruction = new Instruction(InstType.SRC);
                } else if(tokens[0].equalsIgnoreCase("lb")){
                    instruction = new Instruction(InstType.LB);
                } else if(tokens[0].equalsIgnoreCase("sb")){
                    instruction = new Instruction(InstType.SB);
                } else {
                    throw new IllegalArgumentException("Invalid instruction");
                }

                // set register 1
                if(tokens[1].contains("R") || tokens[1].contains("r")) //if it contains R, remove it -- ex: R1 -> 1
                    instruction.setReg1(Integer.parseInt(tokens[1].substring(1)));
                else
                    instruction.setReg1(Integer.parseInt(tokens[1]));
                
                //set immediate or register 2
                if(instruction.isImmediateValid()){
                    instruction.setImm(Byte.parseByte(tokens[2]));
                } else {
                    if(tokens[2].contains("R") || tokens[2].contains("r")) //if it contains R, remove it -- ex: R1 -> 1
                        instruction.setReg2(Integer.parseInt(tokens[2].substring(1)));
                    else
                        instruction.setReg2(Integer.parseInt(tokens[2]));
                }
                

                instructions.add(instruction);
                binaryInstructions.add(instruction.getInstructionBits());
                
                //System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public String toString() {
    	StringBuilder sb = new StringBuilder("Instructions ==> "+binaryInstructions + "\n");
    	for (Instruction ins: instructions) {
			sb.append(ins +"\n");
		}
		return  sb.toString();
	}
    
    public static void main(String[] args) {
		Parser p = new Parser();
		System.out.println(p);
		
	}
}
