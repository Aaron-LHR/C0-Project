package miniplc0java.instruction;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.CompileError;
import miniplc0java.error.ErrorCode;
import miniplc0java.error.GenerateError;
import miniplc0java.util.Pos;

public enum Operation {
    nop, push, pop, loca, arga, globa, load64, store64, call,
    add_i, add_f, sub_i, sub_f, mul_i, mul_f, div_i, div_f, div_u,
    cmp_i, cmp_u, cmp_f, neg_i, neg_f,
    set_lt, set_gt,
    br, br_false, br_true,
    ftoi, itof,
    stackalloc, ret,
    scan_i, scan_c, scan_f, print_i, print_c, print_f, print_s, println;

    public String getGenerateInstruction() throws GenerateError {
        switch (this) {
            case nop:
                return "00";
            case push:
                return "01";
            case pop:
                return "02";
            case loca:
                return "0a";
            case arga:
                return "0b";
            case globa:
                return "0c";
            case load64:
                return "13";
            case store64:
                return "17";
            case stackalloc:
                return "1a";
            case call:
                return "48";
            case add_i:
                return "20";
            case add_f:
                return "24";
            case sub_i:
                return "21";
            case sub_f:
                return "25";
            case mul_i:
                return "22";
            case mul_f:
                return "26";
            case div_i:
                return "23";
            case div_f:
                return "27";
            case div_u:
                return "28";
            case cmp_i:
                return "30";
            case cmp_u:
                return "31";
            case cmp_f:
                return "32";
            case neg_i:
                return "34";
            case neg_f:
                return "35";
            case set_lt:
                return "39";
            case set_gt:
                return "3a";
            case br:
                return "41";
            case br_false:
                return "42";
            case br_true:
                return "43";
            case itof:
                return "36";
            case ftoi:
                return "37";
            case ret:
                return "49";
            case scan_i:
                return "50";
            case scan_c:
                return "51";
            case scan_f:
                return "52";
            case print_i:
                return "54";
            case print_c:
                return "55";
            case print_f:
                return "56";
            case print_s:
                return "57";
            case println:
                return "58";
            default:
                throw new GenerateError(ErrorCode.InstructionNotFound, new Pos(0, 0));
        }
    }
}
