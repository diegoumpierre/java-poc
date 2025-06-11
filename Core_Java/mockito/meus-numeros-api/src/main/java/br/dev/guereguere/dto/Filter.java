package br.dev.guereguere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Filter {


    private String value;
    private MatchModeEnum matchMode;


}
