package com.gp.common.mybatisplus.merchantpay.base;

import com.gp.common.mybatisplus.merchantpay.constants.PayMerchantCons;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

@Component
public class PayConstantsChecker {

    @PostConstruct
    public void checkDuplicate() throws IllegalAccessException {
        Field[] fields = PayMerchantCons.class.getDeclaredFields();
        Set<String> values = new HashSet<>();

        for (Field field : fields) {
            if (field.getType() == String.class) {
                field.setAccessible(true);
                String value = (String) field.get(null);
                if (!values.add(value)) {
                    throw new IllegalStateException(
                        "ConstantsPay 中存在重复 key: " + value
                    );
                }
            }
        }
    }
}
