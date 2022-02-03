package org.opengroup.osdu.core.common.util;

import org.opengroup.osdu.core.common.SwaggerDoc;

import java.util.LinkedList;
import java.util.List;

public class KindParser {
    public static List<String> parse(Object kind) {
        if(kind == null)
            throw new IllegalArgumentException(SwaggerDoc.KIND_VALIDATION_CAN_NOT_BE_NULL_OR_EMPTY);

        List<String> kinds;
        if(kind instanceof String) {
            kinds = new LinkedList<>();
            kinds.add((String) kind);
        }
        else if(kind instanceof List<?>){
            kinds = (List<String>) kind;
            // The above case is subtle. It won't throw exception even if kind is a list of integers or mixed types.
            // Check the type of each item
            for(int i = 0; i < kinds.size(); i++) {
                if(!(kinds.get(i) instanceof String)) {
                    throw new IllegalArgumentException(SwaggerDoc.KIND_VALIDATION_Not_SUPPORTED_TYPE);
                }
            }
        }
        else {
            throw new IllegalArgumentException(SwaggerDoc.KIND_VALIDATION_Not_SUPPORTED_TYPE);
        }

        return kinds;
    }

}
