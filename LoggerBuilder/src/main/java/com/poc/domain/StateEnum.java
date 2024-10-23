package com.poc.domain;

import lombok.Getter;

import java.util.Arrays;

/**
 * Represents de State's
 */
@Getter
public enum StateEnum {
     RS("RS"),
     SC("SC"),
     RJ("RJ"),
     SP("SP");

     private final String stringCode;

     StateEnum(String stringCode) {
          this.stringCode = stringCode;
     }

    public static StateEnum toEnum(String stringCode) {

          if (stringCode == null) {
               return null;
          }

          for (StateEnum x : StateEnum.values()) {
               if (stringCode.equals(x.getStringCode())) {
                    return x;
               }
          }

          throw new IllegalArgumentException("Invalid ID '"+stringCode+"' to state, please use {"+ Arrays.toString(StateEnum.values()) +"}");
     }

}