package anzihe.com.common_template.common;

import lombok.Data;

@Data
public  class  PageRequest<T> {

    private int current = 1;

    private int pageSize = 10;

    private  T date;

    private String sortField;

    private String sortOrder = "descend";
}
