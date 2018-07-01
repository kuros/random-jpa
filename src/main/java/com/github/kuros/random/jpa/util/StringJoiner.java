package com.github.kuros.random.jpa.util;


public final class StringJoiner {

    private final String[] sequence;
    private final int sequenceLength;
    private final String lastString;
    private int index = 0;

    public StringJoiner(final String... sequence) {
        this.sequence = sequence;
        sequenceLength = getSize(sequence);

        // Compute the last string that we'll return once we've exceeded the sequence length.
        // This prevents us from having to look up the last string repeatedly, since in normal
        // usage the last string is going to be the one that is used most often.
        lastString = (sequenceLength > 0) ? sequence[sequenceLength - 1] : "";
    }

    private int getSize(final String[] array) {
        return array == null ? 0 : array.length;
    }


    public String next() {
        final String value;

        if (index >= sequenceLength) {
            value = lastString;
        } else {
            value = sequence[index];
            index++;
        }

        return (value != null) ? value : "";
    }


    @Override
    public String toString() {

        return "StringJoiner(" +
                "items=" + sequenceLength +
                ", index=" + index +
                ")";
    }


    //
    // Common sequences
    //
    public static StringJoiner comma() {
        return new StringJoiner("", ", ");
    }

}
