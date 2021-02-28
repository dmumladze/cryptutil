package com.citigroup.icg.cmc;

public interface Visitor<E> {
    void visit(E obj);
}
