package parser;

import java.math.BigDecimal;

public class FormulaValue {
    // valor pode ser um booleano ou um número: se número é null, então é booleano
    private final boolean bool;
    private final BigDecimal number;
    public FormulaValue(boolean bool) {
        this(bool, null);
    }
    public FormulaValue(double d) {
        this(false, new BigDecimal(d));
    }
    public FormulaValue(BigDecimal number) {
        this(false, number);
    }
    public FormulaValue(boolean bool, BigDecimal number) {
        this.bool = bool;
        this.number = number;
    }
    public Boolean booleanValue() {
        return this.bool;
    }
    public BigDecimal numberValue() {
        return this.number;
    }
    public boolean isNumber() {
        return this.number != null;
    }
    @Override
    public int hashCode() {
        return this.isNumber() ? this.number.hashCode() : Boolean.hashCode(this.bool);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final FormulaValue other = (FormulaValue) obj;
        boolean thisIsNumber = this.isNumber();
        if (thisIsNumber != other.isNumber()) {
            return false;
        }
        return thisIsNumber ? this.number.equals(other.number) : this.bool == other.bool;
    }

    @Override
    public String toString() {
        return this.isNumber() ? String.valueOf(this.number) : String.valueOf(this.bool);
    }
}