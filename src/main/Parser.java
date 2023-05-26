package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {

    private ArrayList<Instruction> instructions = new ArrayList<Instruction>();
    public ArrayList<String> binaryInstructions = new ArrayList<>();
    private boolean HAZARDSavoidance;
    
    public Parser() {
        this(false);
    }
    public Parser(boolean HAZARDSavoidance) {
        this.HAZARDSavoidance = HAZARDSavoidance;
        readFromTextFileToInstructionList();
    }
    // public ArrayList<Instruction> getGeneratedInstructions() {
    //     return instructions;
    // }
    public ArrayList<String> getBinaryInstructions() {
        return binaryInstructions;
    }

    public void readFromTextFileToInstructionList() {
        // function to read from a text file named input.txt in the src folder
        String filePath = "input.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
            	if(line.length() == 0) continue;
            	
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
                } else if(tokens[0].equalsIgnoreCase("nop")){
                    instruction = new Instruction(InstType.NOP);
                } else {
                    throw new IllegalArgumentException("Invalid instruction");
                }

                if(instruction.getType() != InstType.NOP) {
                    // set register 1 for all other than nop

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
                }

                instructions.add(instruction);
                binaryInstructions.add(instruction.getInstructionBits());
                
                //System.out.println(line);
            }

            if(HAZARDSavoidance){
                preprocessInstructionsForDataHazards(instructions, binaryInstructions);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static class Pair{
        Integer id;
        Instruction instruction;
        Pair(Integer id, Instruction instruction){
            this.id = id;
            this.instruction = instruction;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null || !(obj instanceof Pair)) return false;

            return id.equals(((Pair)obj).id);
        }
    }

    private void preprocessInstructionsForDataHazards(ArrayList<Instruction> original, ArrayList<String> originalBinary) {
        ArrayList<Pair> instructionsP = new ArrayList<>();
        ArrayList<Pair> instructionsWithNOPS = new ArrayList<>();

        for(int i = 0; i<original.size(); i++){
            instructionsP.add(new Pair(i, original.get(i)));
        }

        int size = instructionsP.size();
        for (int i = 0; i < size-1; i++) {
            instructionsWithNOPS.add(new Pair(i, original.get(i)));
            if(hasHazardWithNextInstruction(instructionsP.get(i).instruction, instructionsP.get(i+1).instruction)){
                instructionsWithNOPS.add(i+1, new Pair(null, new Instruction(InstType.NOP)));
            }
        }
        instructionsWithNOPS.add(new Pair(size-1, original.get(size-1)));


        // now compare both lists and adjust offsets of jump and branch instructions
        for (int i = 0; i < instructionsP.size(); i++) {
            Instruction curr = instructionsP.get(i).instruction;
            if(curr.type == InstType.JR || curr.type == InstType.BEQZ ) {
                int id = getIdOfTheDestinationInstruction(instructionsP, i);
                // set the new offset of corresponding destination offset
                for(int j = 0; j < instructionsWithNOPS.size(); j++){
                    if(instructionsWithNOPS.get(j).id == id){
                        setJRorBEQZinstructionWithNewOffset(instructionsWithNOPS, i, j);
                        break;
                    }
                }
            }
        }


        //set instructions arraylist and binary instructions arraylist to new values
        ArrayList<Instruction> newinstructions = new ArrayList<>();
        ArrayList<String> bininstructions = new ArrayList<>();
        for (Pair inst : instructionsWithNOPS) {
            newinstructions.add(inst.instruction);
            bininstructions.add(inst.instruction.getInstructionBits());
        }
        this.instructions = newinstructions;
        this.binaryInstructions = bininstructions;


    }

    

    private int getIdOfTheDestinationInstruction(ArrayList<Pair> instructionsP, int curr) {
        Instruction inst = instructionsP.get(curr).instruction;
        
            return instructionsP.get(curr + inst.getImm()).id;

        
        // return 0;
    }

    private void setJRorBEQZinstructionWithNewOffset(ArrayList<Pair> instructionsNOPS, int instID, int newPlace){
        for (int i = 0; i < instructionsNOPS.size(); i++) {
            if(instructionsNOPS.get(i).id == instID){
                instructionsNOPS.get(i).instruction.setImm((byte) (newPlace - i));
                break;
            }
        }
    }
    private boolean hasHazardWithNextInstruction(Instruction curr, Instruction next){

        //before adding check for hazards where i1.r1 = i2.r2 and type R-Type in case not SB
        if(!curr.isImmediateValid() && curr.type != InstType.JR) { //curr instruction is R-Type
            if(!next.isImmediateValid()){ //next instruction is R-Type

                return (curr.getReg1() == next.getReg1() || curr.getReg1() == next.getReg2()) ;
            } else { //next instruction is I-Type
                return (next.type != InstType.LB && next.type != InstType.LDI)
                        &&(curr.getReg1() == next.getReg1());
            }
        } else { //curr instruction is I-Type
            if(curr.type != InstType.SB && curr.type != InstType.BEQZ && curr.type != InstType.JR) {
                if(!next.isImmediateValid()){ //next instruction is R-Type
                    return (curr.getReg1() == next.getReg1() || curr.getReg1() == next.getReg2()) ;
                } else { //next instruction is I-Type
                    return (next.type != InstType.LB && next.type != InstType.LDI)&&(curr.getReg1() == next.getReg1());
                }

            } else if( curr.type == InstType.SB){
                return (next.type == InstType.LB && curr.getImm() == next.getImm());
            }

        }


        return false;
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
