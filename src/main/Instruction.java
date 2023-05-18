package main;

public class Instruction {
    InstType type;
    int reg1, reg2;
    byte imm;
    private boolean immediateMakesSense = false; 

    public String getInstructionBits() {
        if (immediateMakesSense){
            return generateOPCODE() + generateReg1() + generateImm();
        } else {
            return generateOPCODE() + generateReg1() + generateReg2();
        }
    }

    public Instruction(InstType type) {
        this.type = type;

        if(type == InstType.LDI || type == InstType.BEQZ 
            || type == InstType.SLC || type == InstType.SRC 
                || type == InstType.LB || type == InstType.SB){
            immediateMakesSense = true;
        }
    }
    public void setReg1(int reg1){
        this.reg1 = reg1;
    }
    public void setReg2(int reg2){
        this.reg2 = reg2;
    }
    public void setImm(byte imm){
        this.imm = imm;
    }
    public InstType getType(){
        return type;
    }
    public boolean isImmediateValid(){
        return immediateMakesSense;
    }

    private String generateReg1(){
        String reg1 = Integer.toBinaryString(this.reg1);
        while(reg1.length()<6){
            reg1 = "0" + reg1;
        }
        return reg1;
    }

    private String generateReg2(){
        String reg2 = Integer.toBinaryString(this.reg2);
        while(reg2.length()<6){
            reg2 = "0" + reg2;
        }
        return reg2;
    }
    private String generateImm(){
        String imm = Integer.toBinaryString(this.imm);
        while(imm.length()<6){
            imm = "0" + imm;
        }
        return imm;
    }

    private String generateOPCODE(){
        switch(type){
            case ADD:
                return "000000";
            case SUB:
                return "000001";
            case MUL:
                return "000010";
            case LDI:
                return "000011";
            case BEQZ:
                return "000100";
            case AND:
                return "000101";
            case OR:
                return "000110";
            case JR:
                return "000111";
            case SLC:
                return "001000";
            case SRC:
                return "001001";
            case LB:
                return "001010";
            case SB:
                return "001011";
            default: 
                return "000000";
        }
    }
    
    public String toString() {
		return type +"'"+type.ordinal()+"'"+" (" +generateOPCODE()+")   " + "R"+reg1+ " (" + generateReg1() + ")   " 
				+ (isImmediateValid()? "Imm (" +generateImm() : "R"+reg2 + " ("+ generateReg2())  + ")";
	}
}
