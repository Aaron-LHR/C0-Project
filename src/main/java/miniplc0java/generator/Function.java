package miniplc0java.generator;

import miniplc0java.error.GenerateError;
import miniplc0java.instruction.Instruction;
import miniplc0java.tokenizer.IdentType;

import java.util.ArrayList;

public class Function {
    String name;
    String ret_slots;
    String param_slots;
    String loc_slots;
    String body_count;
    ArrayList<String> body_items = new ArrayList<>();

    public Function(int name, IdentType ret_identType, int param_slots, int loc_slots, ArrayList<Instruction> instructions) throws GenerateError {
        this.name = String.format("%08x", name);
        if (ret_identType == IdentType.VOID) {
            this.ret_slots = "00000000";
        } else {
            this.ret_slots = "00000001";
        }
        this.param_slots = String.format("%08x", param_slots);
        this.loc_slots = String.format("%08x", loc_slots);
        this.body_count = String.format("%08x", instructions.size());
        for (Instruction instruction: instructions) {
            body_items.add(instruction.getGenerateInstruction());
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String item: body_items) {
            stringBuilder.append(item);
        }
//        System.out.println("re:" + ret_slots);
        return name + ret_slots + param_slots + loc_slots + body_count + stringBuilder.toString();
    }
}
