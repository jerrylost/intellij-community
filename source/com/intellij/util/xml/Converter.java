/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.intellij.util.xml;

import com.intellij.psi.PsiClass;

/**
 * @author peter
 */
public interface Converter<T> {
  T fromString(String s, final ConvertContext context) throws ConvertFormatException;
  String toString(T t, final ConvertContext context);

  Converter<Integer> INTEGER_CONVERTER = new Converter<Integer>() {
    public Integer fromString(final String s, final ConvertContext context) throws ConvertFormatException {
      try {
        return Integer.decode(s);
      }
      catch (NumberFormatException e) {
        throw new ConvertFormatException(s, Integer.class);
      }
    }

    public String toString(final Integer t, final ConvertContext context) {
      return t.toString();
    }
  };

  Converter<Boolean> BOOLEAN_CONVERTER = new Converter<Boolean>() {
    public Boolean fromString(final String s, final ConvertContext context) throws ConvertFormatException {
      if ("true".equalsIgnoreCase(s)) {
        return Boolean.TRUE;
      }
      if ("false".equalsIgnoreCase(s)) {
        return Boolean.FALSE;
      }
      throw new ConvertFormatException(s, Boolean.class);
    }

    public String toString(final Boolean t, final ConvertContext context) {
      return t.toString();
    }
  };

  Converter<String> EMPTY_CONVERTER = new Converter<String>() {
    public String fromString(final String s, final ConvertContext context) throws ConvertFormatException {
      return s;
    }

    public String toString(final String t, final ConvertContext context) {
      return t;
    }
  };

  Converter<PsiClassReference> PSI_CLASS_REFERENCE_CONVERTER = new Converter<PsiClassReference>() {
    public PsiClassReference fromString(final String s, final ConvertContext context) throws ConvertFormatException {
      return new PsiClassReference() {
        public final String getClassName() {
          return s;
        }

        public final boolean hasClass() {
          return getPsiClass() != null;
        }

        public final PsiClass getPsiClass() {
          return context.findClass(getClassName());
        }

        public final String getValue() {
          return s;
        }
      };
    }

    public String toString(final PsiClassReference t, final ConvertContext context) {
      return t.getClassName();
    }
  };
}
