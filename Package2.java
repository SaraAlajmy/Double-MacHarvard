public class Package2 {

    private static short[] instructionMemory = new short[1024];
    private static byte[] dataMemory = new byte[2048];
    private static byte[] registers = new byte[66];
    private int pc = 0;

    public void fetch(){
// read from a txt file?

        for(int i = 0; i < instructionMemory.length; i++) {
            short instruction = instructionMemory[pc];
            decode(instruction);
            pc++;
        }

    }

    public void decode(short instruction){

    }

    public void execute(){

    }
}