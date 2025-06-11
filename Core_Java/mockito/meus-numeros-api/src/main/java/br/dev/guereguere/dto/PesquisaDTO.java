package br.dev.guereguere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PesquisaDTO implements Serializable {

    private final long serialVersionUID = 1L;


    private int first;
    private int rows;
    private int page;
    private String sortField;
    private String sortOrder;
    private HashMap<String, Filter> filters;
    private int totalRecords;
}