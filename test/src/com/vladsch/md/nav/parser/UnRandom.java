package com.vladsch.md.nav.parser;

import java.util.Random;

public class UnRandom extends Random {
    public UnRandom() {
    }

    public UnRandom(long l) {
        super(l);
    }

    @Override
    public int nextInt() {
        return 4;
    }

    @Override
    public int nextInt(int i) {
        return i > 0 ? i - 1 : i;
    }
}
