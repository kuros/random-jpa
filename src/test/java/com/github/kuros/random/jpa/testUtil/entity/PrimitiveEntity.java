package com.github.kuros.random.jpa.testUtil.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "primitive_table")
public class PrimitiveEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "default_boolean")
    private boolean defaultBoolean;

    @Column(name = "default_byte")
    private byte defaultByte;

    @Column(name = "default_short")
    private short defaultShort;

    @Column(name = "default_int")
    private int defaultInt;

    @Column(name = "default_long")
    private long defaultLong;

    @Column(name = "default_float")
    private float defaultFloat;

    @Column(name = "default_double")
    private double defaultDouble;

    @Column(name = "default_char")
    private char defaultChar;

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public boolean isDefaultBoolean() {
        return defaultBoolean;
    }

    public void setDefaultBoolean(final boolean defaultBoolean) {
        this.defaultBoolean = defaultBoolean;
    }

    public byte getDefaultByte() {
        return defaultByte;
    }

    public void setDefaultByte(final byte defaultByte) {
        this.defaultByte = defaultByte;
    }

    public short getDefaultShort() {
        return defaultShort;
    }

    public void setDefaultShort(final short defaultShort) {
        this.defaultShort = defaultShort;
    }

    public int getDefaultInt() {
        return defaultInt;
    }

    public void setDefaultInt(final int defaultInt) {
        this.defaultInt = defaultInt;
    }

    public long getDefaultLong() {
        return defaultLong;
    }

    public void setDefaultLong(final long defaultLong) {
        this.defaultLong = defaultLong;
    }

    public float getDefaultFloat() {
        return defaultFloat;
    }

    public void setDefaultFloat(final float defaultFloat) {
        this.defaultFloat = defaultFloat;
    }

    public double getDefaultDouble() {
        return defaultDouble;
    }

    public void setDefaultDouble(final double defaultDouble) {
        this.defaultDouble = defaultDouble;
    }

    public char getDefaultChar() {
        return defaultChar;
    }

    public void setDefaultChar(final char defaultChar) {
        this.defaultChar = defaultChar;
    }
}
