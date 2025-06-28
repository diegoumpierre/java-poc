package com.poc;



/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
public class Badge {
    public String print(Integer id, String name, String department) {
        StringBuffer stringBuffer = new StringBuffer();

        if (id != null) stringBuffer.append("[").append(id).append("]").append(" - ");
        stringBuffer.append(name).append(" - ");
        if (department == null) stringBuffer.append("OWNER");
        else stringBuffer.append(department.toUpperCase());

        return stringBuffer.toString();
    }
}
