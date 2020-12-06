package miniplc0java.instruction;

public enum Operation {
    nop, push, pop, loca, arga, globa, load64, store64, call,
    add_i, add_f, sub_i, sub_f, mul_i, mul_f, div_i, div_f, div_u,
    cmp_i, cmp_u, cmp_f, neg_i, neg_f,
    set_lt, set_gt,
    br, br_false, br_true,
    ftoi, itof,
    ret
}
