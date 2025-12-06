/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oop_class.jufin;

/**
 *
 * @author Akbar
 */
public enum month {
    JANUARY(1),
    FEBRUARY(2),
    MARCH(3),
    APRIL(4),
    MAY(5),
    JUNE(6),
    JULY(7),
    AUGUST(8),
    SEPTEMBER(9),
    OCTOBER(10),
    NOVEMBER(11),
    DECEMBER(12);
    
    private final int value;
    
    month(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    /**
     * @return
     */
    @Override
    public String toString() {
        return name();
    }
}
