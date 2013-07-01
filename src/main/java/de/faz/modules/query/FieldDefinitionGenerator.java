/*
 * Copyright (c) 2013. F.A.Z. Electronic Media GmbH
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of F.A.Z. Electronic Media GmbH and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to F.A.Z. Electronic Media GmbH
 * and its suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from F.A.Z. Electronic Media GmbH.
 */

package de.faz.modules.query;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Stack;

import javax.annotation.concurrent.Immutable;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public class FieldDefinitionGenerator {

    private final Stack<FieldDefinition> fieldStack;

    public FieldDefinitionGenerator() {
        fieldStack = new Stack<FieldDefinition>();
    }

    public <T extends Mapping> T createFieldDefinition(Class<T> mappingClass) {
        return enhanceWithInterceptor(mappingClass, new MethodInterceptor() {
            @Override
            public Object intercept(final Object o, final Method method, final Object[] objects, final MethodProxy methodProxy) throws Throwable {
                String fieldName = null;
                Integer boost = null;

                MapToField mapping = method.getAnnotation(MapToField.class);
                if(mapping != null) {
                    fieldName = mapping.value();
                }

                BoostResult boostResult = method.getAnnotation(BoostResult.class);
                if(boostResult != null) {
                    boost = boostResult.value();
                }

                if(fieldName != null) {
                    fieldStack.push(new FieldDefinition(fieldName, boost));
                }

                return null;
            }
        });
    }

    <T extends Mapping> T enhanceWithInterceptor(Class<T> enhancedClass, Callback interceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(interceptor);
        enhancer.setSuperclass(enhancedClass);
        Class<?>[] argumentTypes = getConstructorArgumentsFromClass(enhancedClass);
        T proxiedObject;
        if(argumentTypes == null) {
            proxiedObject = (T) enhancer.create();
        } else {
            proxiedObject = (T) enhancer.create(argumentTypes, new Object[argumentTypes.length]);
        }
        return proxiedObject;
    }

    public String getFieldNameOf(Object fieldMethod) {
        String fieldName = null;
        if(isNotEmpty()) {
            FieldDefinition definition = pop();
            fieldName = definition.name.toString();
        }

        return fieldName;
    }

    public boolean isEmpty() {
        return fieldStack.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public FieldDefinition pop() {
        return fieldStack.pop();
    }

    private Class<?>[] getConstructorArgumentsFromClass(Class<?> cls) {
        Constructor[] constructors = cls.getConstructors();
        if(constructors != null && constructors.length > 0) {
            return constructors[0].getParameterTypes();
        }
        return null;
    }
}

class FieldDefinition {
    final CharSequence name;
    final int boost;

    public FieldDefinition(CharSequence name, Integer boost) {
        this.name = name;
        if(boost != null) {
            this.boost = boost;
        } else {
            this.boost = 1;
        }
    }

    CharSequence getName() {
        return name;
    }

    FieldDefinition setName(String name) {
        return new FieldDefinition(name, boost);
    }

    int getBoost() {
        return boost;
    }

    FieldDefinition setBoost(int boost) {
        return new FieldDefinition(name, boost);
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj instanceof FieldDefinition) {
            FieldDefinition def = (FieldDefinition) obj;
            return name.equals(def.name) && boost == def.boost;
        }
        return super.equals(obj);
    }
}
